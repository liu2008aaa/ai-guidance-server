package com.guidance.repository;

import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static dev.langchain4j.internal.Utils.isNotNullOrBlank;
import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * PgVector EmbeddingStore implementation compatible with langchain4j-core:0.36.0
 */
@Slf4j
public class CompatiblePgVectorEmbeddingStore implements EmbeddingStore<TextSegment> {

    private final DataSource dataSource;
    private final String table;
    private final int dimension;

    public CompatiblePgVectorEmbeddingStore(DataSource dataSource, String table, int dimension) {
        this.dataSource = Objects.requireNonNull(dataSource, "dataSource must not be null");
        this.table = Objects.requireNonNull(table, "table must not be null");
        if (dimension <= 0) {
            throw new IllegalArgumentException("dimension must be > 0");
        }
        this.dimension = dimension;
        initializeTable();
    }

    private void initializeTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE EXTENSION IF NOT EXISTS vector");
            String createTable = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                    "id TEXT PRIMARY KEY, " +
                    "embedding vector(%d), " +
                    "text_content TEXT, " +
                    "metadata_json jsonb," +
                    "content_hash TEXT" +
                ")",
                table, dimension
            );
            stmt.execute(createTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize pgvector table", e);
        }
    }

    // --- Add methods (same as before) ---

    @Override
    public String add(Embedding embedding) {
        String id = UUID.randomUUID().toString();
        add(id, embedding);
        return id;
    }

    @Override
    public void add(String id, Embedding embedding) {
        addInternal(id, embedding, null);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        String id = UUID.randomUUID().toString();
        addInternal(id, embedding, textSegment);
        return id;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        List<String> ids = embeddings.stream()
            .map(e -> UUID.randomUUID().toString())
            .collect(toList());
        addAll(ids, embeddings, null);
        return ids;
    }

    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> textSegments) {
        List<String> ids = embeddings.stream()
            .map(e -> UUID.randomUUID().toString())
            .collect(toList());
        addAll(ids, embeddings, textSegments);
        return ids;
    }

    public void addAll(List<String> ids, List<Embedding> embeddings, List<TextSegment> textSegments) {
        if (ids.isEmpty() || embeddings.isEmpty()) return;
        if (ids.size() != embeddings.size()) {
            throw new IllegalArgumentException("ids and embeddings must have same size");
        }
        if (textSegments != null && textSegments.size() != embeddings.size()) {
            throw new IllegalArgumentException("textSegments size mismatch");
        }

        String sql =
            "INSERT INTO " + table + " (id, embedding, text_content, metadata_json,content_hash) " +
            "VALUES (?, ?::vector, ?, ?::jsonb,?) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            "embedding = EXCLUDED.embedding, " +
            "text_content = EXCLUDED.text_content, " +
            "metadata_json = EXCLUDED.metadata_json, " +
            "content_hash = EXCLUDED.content_hash";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                ps.setString(1, ids.get(i));
                ps.setString(2, vectorToString(embeddings.get(i).vector()));
                if (textSegments != null && textSegments.get(i) != null) {
                    //元数据+原始数据的hash
                    String hash = DigestUtils.md5Hex(textSegments.get(i).metadata().toString() + textSegments.get(i).text());
                    if(isContentExists(hash)){
                        continue;
                    }
                    ps.setString(3, textSegments.get(i).text());
                    ps.setString(4, serializeMetadata(textSegments.get(i).metadata()));
                    ps.setString(5, hash);
                } else {
                    ps.setNull(3, Types.VARCHAR);
                    ps.setNull(4, Types.VARCHAR);
                    ps.setNull(5, Types.VARCHAR);
                }
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to batch insert embeddings", e);
        }
    }

    // --- NEW: Implement search(EmbeddingSearchRequest) ---

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        Embedding queryEmbedding = request.queryEmbedding();
        int maxResults = request.maxResults();
        double minScore = request.minScore();
        Filter filter = request.filter();

        List<EmbeddingMatch<TextSegment>> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            String referenceVector = Arrays.toString(queryEmbedding.vector());
            String whereClause = "";
            if(filter instanceof IsEqualTo isEqualTo){
                String key = isEqualTo.key();
                Object value = isEqualTo.comparisonValue();
                String escapedValue = String.valueOf(value).replace("'", "''");
                escapedValue = "'" + escapedValue + "'";

                key = String.format("(%s::jsonb->>'%s')", "metadata_json",key);
                whereClause = format("(%s is not null) and (%s = %s)", "metadata_json", key, escapedValue);
            }
            whereClause = (whereClause.isEmpty()) ? "" : "AND " + whereClause;
            log.info("search.whereClause:{}",whereClause);
            String query = String.format(
                    "SELECT id, (2 - (embedding <=> '%s')) / 2 AS score, embedding, text_content, %s FROM %s " +
                            "WHERE round(cast(float8 (embedding <=> '%s') as numeric), 8) <= round(2 - 2 * %s, 8) %s "
                            + "ORDER BY embedding <=> '%s' LIMIT %s;",
                    referenceVector,
                    join(",", "metadata_json"),
                    table,
                    referenceVector,
                    minScore,
                    whereClause,
                    referenceVector,
                    maxResults
            );
            log.info("search.sql:{}",query);
            try (PreparedStatement selectStmt = connection.prepareStatement(query)) {
                try (ResultSet resultSet = selectStmt.executeQuery()) {
                    while (resultSet.next()) {
                        String embeddingId = resultSet.getString("id");
                        double score = resultSet.getDouble("score");

                        PGobject vector = (PGobject) resultSet.getObject("embedding");
                        Embedding embedding = new Embedding(stringToFloatArray(vector));

                        String text = resultSet.getString("text_content");
                        TextSegment textSegment = null;
                        if (isNotNullOrBlank(text)) {
                            Metadata metadata = decodeMetadata(resultSet.getString("metadata_json"));
                            textSegment = TextSegment.from(text, metadata);
                        }
                        result.add(new EmbeddingMatch<>(score, embeddingId, embedding, textSegment));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new EmbeddingSearchResult<>(result);
//        String vectorStr = vectorToString(queryEmbedding.vector());
//        double maxDistance = 1.0 - minScore; // cosine distance threshold
//
//        String sql = String.format(
//            "SELECT id, embedding, text_content, metadata_json, (1 - (embedding <=> ?::vector)) AS similarity " +
//            "FROM %s " +
//            "WHERE (embedding <=> ?::vector) <= ? " +
//            "ORDER BY embedding <=> ?::vector " +
//            "LIMIT ?",
//            table
//        );
//
//        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();
//        try (Connection conn = dataSource.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setString(1, vectorStr);
//            ps.setString(2, vectorStr);
//            ps.setDouble(3, maxDistance);
//            ps.setString(4, vectorStr);
//            ps.setInt(5, maxResults);
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    double similarity = rs.getDouble("similarity");
//                    if (similarity < minScore) continue; // safety check
//
//                    String id = rs.getString("id");
//                    float[] embeddingArray = stringToFloatArray((PGobject) rs.getObject("embedding"));
//                    Embedding embedding = Embedding.from(embeddingArray);
//
//                    String text = rs.getString("text_content");
//                    TextSegment segment = (text != null) ? TextSegment.from(text) : null;
//
//                    matches.add(new EmbeddingMatch<>(similarity, id, embedding, segment));
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to execute search", e);
//        }
//
//        return new EmbeddingSearchResult<>(matches);
    }

    // --- Helper Methods ---

    private void addInternal(String id, Embedding embedding, TextSegment textSegment) {
        addAll(singletonList(id), singletonList(embedding),
               textSegment == null ? null : singletonList(textSegment));
    }

    private String vectorToString(float[] vector) {
        if (vector == null || vector.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        sb.append(vector[0]);
        for (int i = 1; i < vector.length; i++) {
            sb.append(',').append(vector[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    private float[] stringToFloatArray(PGobject pgObj) throws SQLException {
        if (pgObj == null || pgObj.getValue() == null) {
            return new float[0];
        }
        String value = pgObj.getValue().trim();
        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1);
        }
        value = value.trim();
        if (value.isEmpty()) {
            return new float[0];
        }

        String[] parts = value.split(",");
        float[] array = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            array[i] = Float.parseFloat(parts[i].trim());
        }
        return array;
    }

    private String serializeMetadata(Metadata metadata) {
        if (metadata == null) return "{}";
        return JsonUtils.toJson(metadata.toMap());
    }

    private Metadata decodeMetadata(String metadata_json) {
        if (metadata_json == null) return new Metadata();
        return JsonUtils.fromJson(metadata_json,Metadata.class);
    }
    /**
     * 查询数据hash是否已存在
     * @param hash
     * @return
     */
    private boolean isContentExists(String hash) {
        String sql = "SELECT 1 FROM " + table + " WHERE content_hash = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs!=null && rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("isContentExists failed", e);
        }
    }
}