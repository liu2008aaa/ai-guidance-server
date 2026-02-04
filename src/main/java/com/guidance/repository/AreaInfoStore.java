package com.guidance.repository;

import com.alibaba.dashscope.utils.JsonUtils;
import com.guidance.vo.AreaInfo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

import static com.guidance.utils.StringUtils.shortAreaName;

/**
 * 区域信息 存储
 * @author liuyu
 */
@Component
@Slf4j
public class AreaInfoStore{
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    public AreaInfoStore(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init(){
        String sql = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "    id SERIAL PRIMARY KEY," +
                        "    code VARCHAR(255) NOT NULL," +
                        "    name VARCHAR(255) NOT NULL," +
                        "    short_name VARCHAR(255) NOT NULL," +
                        "    level smallint NOT NULL," +
                        "    names VARCHAR(500) NOT NULL," +
                        "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ");\n" +
                        "CREATE UNIQUE INDEX IF NOT EXISTS idx_code ON area_info(code);",
                "area_info"
        );
        try (Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create area_info table", e);
        }
    }

    /**
     * 根据缩略名 查询 区域级别
     *
     * @return
     */
    public AreaInfo queryLevelByShortName(String shorName) {
        String sql = "SELECT name,code,short_name,level,names FROM area_info WHERE short_name = ? ";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, shorName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("code");
                    String name = rs.getString("name");
                    String shortName = rs.getString("short_name");
                    int level = rs.getInt("level");
                    String namesStr = rs.getString("names");
                    String[] names = JsonUtils.fromJson(namesStr,String[].class);
                    return AreaInfo.builder().code(code).name(name).shortName(shortName).level(level).names(names).build();
                }
            }
        } catch (Exception e) {
            log.error("queryLevelByShortName has error",e);
        }
        return null;
    }

    /**
     * 存储区域信息
     *
     * @param info
     */
    public void save(AreaInfo info) {
        if(info == null){
            return;
        }
        if(isExists(info.getCode())){
            return;
        }
        String sql = "INSERT INTO area_info (code,name,short_name,level,names) VALUES (?,?, ?,?,?)";
        jdbcTemplate.update(sql, info.getCode(),info.getName(), shortAreaName(info.getName()), info.getLevel(), JsonUtils.toJson(info.getNames()));
    }

    /**
     * 查询是否有重复
     *
     * @param code
     * @return
     */
    private boolean isExists(String code) {
        String sql = "SELECT 1 FROM area_info WHERE code = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs!=null && rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("isExists failed", e);
        }
    }
}