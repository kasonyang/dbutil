package site.kason.dbutil;

import java.sql.*;
import java.util.*;

/**
 * @author KasonYang
 */
public class DBConnection {

    private Connection jdbc;

    public DBConnection(Connection jdbc) {
        this.jdbc = jdbc;
    }

    public int execute(String sql) throws SQLException {
        return execute(sql, new Object[0]);
    }

    public int execute(String sql, Object... params) throws SQLException {
        if (params.length > 0) {
            try(PreparedStatement stmt = prepareStatement(sql, params, false)){
                return stmt.executeUpdate();
            }
        } else {
            try (Statement stmt = jdbc.createStatement()) {
                return stmt.executeUpdate(sql);
            }
        }
    }

    public List<Map<String, Object>> executeAndGetGeneratedKeys(String sql, Object... params) throws SQLException {
        if (params.length > 0) {
            try(PreparedStatement stmt = prepareStatement(sql, params, true)){
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                return this.formatResultSet(rs);
            }
        } else {
            try (Statement stmt = jdbc.createStatement()) {
                stmt.executeUpdate(sql);
                return formatResultSet(stmt.getGeneratedKeys());
            }
        }
    }

    public List<Map<String, Object>> query(String sql) throws SQLException {
        return this.query(sql, new Object[0]);
    }

    public List<Map<String, Object>> query(String sql, Object... params) throws SQLException {
        ResultSet rs;
        if (params.length > 0) {
            rs = prepareStatement(sql, params, false).executeQuery();
        } else {
            rs = jdbc.createStatement().executeQuery(sql);
        }
        List<Map<String, Object>> list = formatResultSet(rs);
        rs.getStatement().close();
        return list;
    }

    private List<Map<String, Object>> formatResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new LinkedList<>();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        String[] colNames = new String[cols];
        for (int i = 0; i < cols; i++) {
            colNames[i] = md.getColumnLabel(i + 1);
        }
        while (rs.next()) {
            HashMap<String, Object> it = new HashMap<>();
            for (int i = 0; i < cols; i++) {
                it.put(colNames[i], rs.getObject(i + 1));
            }
            list.add(it);
        }
        return list;
    }

    private PreparedStatement prepareStatement(String sql, Object[] params, boolean returnGeneratedKeys) throws SQLException {
        if (params.length > 0) {
            PreparedStatement stmt = jdbc.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                stmt.setObject(i + 1, p);
            }
            return stmt;
        } else {
            return jdbc.prepareCall(sql);
        }
    }

}
