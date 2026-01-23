package com.guidance.repository;

import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.postgresql.util.PGobject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * PgVector EmbeddingStore implementation compatible with langchain4j-core:0.36.0
 */
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
                    "metadata_json TEXT" +
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

        String sql = String.format(
            "INSERT INTO %s (id, embedding, text_content, metadata_json) " +
            "VALUES (?, ?::vector, ?, ?) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            "embedding = EXCLUDED.embedding, " +
            "text_content = EXCLUDED.text_content, " +
            "metadata_json = EXCLUDED.metadata_json",
            table
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                ps.setString(1, ids.get(i));
                ps.setString(2, vectorToString(embeddings.get(i).vector()));
                if (textSegments != null && textSegments.get(i) != null) {
                    ps.setString(3, textSegments.get(i).text());
                    ps.setString(4, serializeMetadata(textSegments.get(i).metadata()));
                } else {
                    ps.setNull(3, Types.VARCHAR);
                    ps.setNull(4, Types.VARCHAR);
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
        // Note: Filter support is optional in 0.36.0; we skip it for simplicity

        String vectorStr = vectorToString(queryEmbedding.vector());
        double maxDistance = 1.0 - minScore; // cosine distance threshold

        String sql = String.format(
            "SELECT id, embedding, text_content, metadata_json, (1 - (embedding <=> ?::vector)) AS similarity " +
            "FROM %s " +
            "WHERE (embedding <=> ?::vector) <= ? " +
            "ORDER BY embedding <=> ?::vector " +
            "LIMIT ?",
            table
        );

        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vectorStr);
            ps.setString(2, vectorStr);
            ps.setDouble(3, maxDistance);
            ps.setString(4, vectorStr);
            ps.setInt(5, maxResults);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double similarity = rs.getDouble("similarity");
                    if (similarity < minScore) continue; // safety check

                    String id = rs.getString("id");
                    float[] embeddingArray = stringToFloatArray((PGobject) rs.getObject("embedding"));
                    Embedding embedding = Embedding.from(embeddingArray);

                    String text = rs.getString("text_content");
                    TextSegment segment = (text != null) ? TextSegment.from(text) : null;

                    matches.add(new EmbeddingMatch<>(similarity, id, embedding, segment));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute search", e);
        }

        return new EmbeddingSearchResult<>(matches);
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
        return JsonUtils.toJson(metadata);
    }
}