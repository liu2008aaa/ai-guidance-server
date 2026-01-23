package com.guidance.repository;

import com.alibaba.dashscope.utils.JsonUtils;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class PureJavaPgVectorStore implements EmbeddingStore<TextSegment> {

    private final DataSource dataSource;
    private final String table;

    public PureJavaPgVectorStore(DataSource dataSource, String table, int dimension) {
        this.dataSource = dataSource;
        this.table = table;
        initializeTable();
    }

    private void initializeTable() {
        String sql = String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                "id TEXT PRIMARY KEY, " +
                "embedding float[], " +
                "text_content TEXT, " +
                "metadata_json TEXT," +
                "content_hash TEXT" +
            ")",
            table
        );
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create embeddings table", e);
        }
    }

    // --- Add methods ---

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
            throw new IllegalArgumentException("ids and embeddings size mismatch");
        }
        if (textSegments != null && textSegments.size() != embeddings.size()) {
            throw new IllegalArgumentException("textSegments size mismatch");
        }

        String sql = String.format(
            "INSERT INTO %s (id, embedding, text_content, metadata_json,content_hash) " +
            "VALUES (?, ?, ?, ?,?) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            "embedding = EXCLUDED.embedding, " +
            "text_content = EXCLUDED.text_content, " +
            "metadata_json = EXCLUDED.metadata_json,",
            "content_hash = EXCLUDED.content_hash",
            table
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                ps.setString(1, ids.get(i));
                ps.setArray(2, conn.createArrayOf("float4", toObjectArray(embeddings.get(i).vector())));
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
            throw new RuntimeException("Failed to batch insert", e);
        }
    }

    // --- Search method (with cosine similarity in SQL) ---

    @Override
    public EmbeddingSearchResult<TextSegment> search(EmbeddingSearchRequest request) {
        Embedding queryEmbedding = request.queryEmbedding();
        int maxResults = request.maxResults();
        double minScore = request.minScore();

        // 构造查询向量数组
        Object[] queryArray = toObjectArray(queryEmbedding.vector());

        // 余弦相似度 SQL（无 pgvector）
        String sql = String.format(
            "SELECT " +
            "   id, " +
            "   embedding, " +
            "   text_content, " +
            "   metadata_json, " +
            "   ( " +
            "       SELECT " +
            "           CASE " +
            "               WHEN sqrt(sum(q.q_elem * q.q_elem)) * sqrt(sum(e_elem * e_elem)) = 0 THEN 0 " +
            "               ELSE sum(q.q_elem * e_elem) / (sqrt(sum(q.q_elem * q.q_elem)) * sqrt(sum(e_elem * e_elem))) " +
            "           END " +
            "       FROM " +
            "           unnest(?::float[]) WITH ORDINALITY AS q(q_elem, idx), " +
            "           unnest(embedding) WITH ORDINALITY AS e(e_elem, idx) " +
            "       WHERE q.idx = e.idx " +
            "   ) AS similarity " +
            "FROM %s " +
            "WHERE ( " +
            "   SELECT " +
            "       CASE " +
            "           WHEN sqrt(sum(q.q_elem * q.q_elem)) * sqrt(sum(e_elem * e_elem)) = 0 THEN 0 " +
            "           ELSE sum(q.q_elem * e_elem) / (sqrt(sum(q.q_elem * q.q_elem)) * sqrt(sum(e_elem * e_elem))) " +
            "       END " +
            "   FROM " +
            "       unnest(?::float[]) WITH ORDINALITY AS q(q_elem, idx), " +
            "       unnest(embedding) WITH ORDINALITY AS e(e_elem, idx) " +
            "   WHERE q.idx = e.idx " +
            ") >= ? " +
            "ORDER BY " +
            "   ( " +
            "       SELECT " +
            "           CASE " +
            "               WHEN sqrt(sum(q.q_elem * q.q_elem)) * sqrt(sum(e_elem * e_elem)) = 0 THEN 0 " +
            "               ELSE sum(q.q_elem * e_elem) / (sqrt(sum(q.q_elem * q.q_elem)) * sqrt(sum(e_elem * e_elem))) " +
            "           END " +
            "       FROM " +
            "           unnest(?::float[]) WITH ORDINALITY AS q(q_elem, idx), " +
            "           unnest(embedding) WITH ORDINALITY AS e(e_elem, idx) " +
            "       WHERE q.idx = e.idx " +
            "   ) DESC " +
            "LIMIT ?",
            table
        );

        List<EmbeddingMatch<TextSegment>> matches = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            Array querySqlArray = conn.createArrayOf("float4", queryArray);
            ps.setArray(1, querySqlArray);
            ps.setArray(2, querySqlArray);
            ps.setDouble(3, minScore);
            ps.setArray(4, querySqlArray);
            ps.setInt(5, maxResults);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double similarity = rs.getDouble("similarity");
                    if (similarity < minScore) continue;

                    String id = rs.getString("id");
                    Array embeddingArray = rs.getArray("embedding");
                    Float[] floats = (Float[]) embeddingArray.getArray();
                    float[] vector = toPrimitive(floats);
                    Embedding embedding = Embedding.from(vector);

                    String text = rs.getString("text_content");
                    TextSegment segment = (text != null) ? TextSegment.from(text) : null;

                    matches.add(new EmbeddingMatch<>(similarity, id, embedding, segment));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search failed", e);
        }

        return new EmbeddingSearchResult<>(matches);
    }

    // --- Helpers ---

    private void addInternal(String id, Embedding embedding, TextSegment textSegment) {
        addAll(singletonList(id), singletonList(embedding),
               textSegment == null ? null : singletonList(textSegment));
    }

    public static float[] toPrimitive(Float[] array) {
        if (array == null) {
            return null;
        }
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i] != null ? array[i].floatValue() : 0.0f; // 或抛异常，根据需求
        }
        return result;
    }

    private Object[] toObjectArray(float[] primitive) {
        if (primitive == null) {
            return null;
        }
        Float[] objectArray = new Float[primitive.length];
        for (int i = 0; i < primitive.length; i++) {
            objectArray[i] = primitive[i]; // 自动装箱：float → Float
        }
        return objectArray; // Float[] 是 Object[] 的子类型
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