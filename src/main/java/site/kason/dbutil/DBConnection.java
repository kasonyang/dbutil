package site.kason.dbutil;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author KasonYang
 */
public class DBConnection implements AutoCloseable {

    private Connection jdbc;

    public DBConnection(Connection jdbc) {
        this.jdbc = jdbc;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        jdbc.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return jdbc.getAutoCommit();
    }

    public void commit() throws SQLException {
        jdbc.commit();
    }

    public void rollback() throws SQLException {
        jdbc.rollback();
    }

    @Override
    public void close() throws SQLException {
        jdbc.close();
    }

    public boolean isClosed() throws SQLException {
        return jdbc.isClosed();
    }


    public void setTransactionIsolation(int level) throws SQLException {
        jdbc.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return jdbc.getTransactionIsolation();
    }

    public int execute(String sql) throws SQLException {
        return execute(sql, new Object[0]);
    }

    public int execute(String sql, Object... params) throws SQLException {
        if (params.length > 0) {
            try (PreparedStatement stmt = prepareStatement(sql, params, false)) {
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
            try (PreparedStatement stmt = prepareStatement(sql, params, true)) {
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

    public List<Map<String, Object>> select(String table, SelectBuilder selectBuilder) throws SQLException {
        return query(selectBuilder.buildSql(table), selectBuilder.buildBindings());
    }

    public List<Map<String, Object>> select(String table, Consumer<SelectBuilder> selectBuilderConsumer) throws SQLException {
        SelectBuilder sb = new SelectBuilder();
        selectBuilderConsumer.accept(sb);
        return select(table, sb);
    }

    public List<Map<String, Object>> select(String table, Map<String,Object> conditions) throws SQLException {
        SelectBuilder sb = new SelectBuilder();
        conditions.forEach(sb::eq);
        return select(table, sb);
    }

    public Map<String, Object> selectOne(String table, SelectBuilder selectBuilder) throws SQLException {
        List<Map<String, Object>> list = select(table, selectBuilder);
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new IllegalArgumentException("unexpected result size:" + list.size());
        }
    }

    public Map<String, Object> selectOne(String table, Consumer<SelectBuilder> selectBuilderConsumer) throws SQLException {
        SelectBuilder sb = new SelectBuilder();
        selectBuilderConsumer.accept(sb);
        return selectOne(table, sb);
    }

    public Map<String, Object> selectOne(String table, Map<String, Object> conditions) throws SQLException {
        SelectBuilder sb = new SelectBuilder();
        conditions.forEach(sb::eq);
        return selectOne(table, sb);
    }

    public long count(String table, SelectBuilder selectBuilder) throws SQLException {
        List<Map<String, Object>> res = query(selectBuilder.buildCount(table), selectBuilder.buildBindings());
        Object count = res.get(0).get("count(*)");
        if (count instanceof Integer) {
            return ((Integer) count).longValue();
        }
        return (Long) count;
    }

    public long count(String table, Consumer<SelectBuilder> selectBuilderConsumer) throws SQLException {
        SelectBuilder sb = new SelectBuilder();
        selectBuilderConsumer.accept(sb);
        return count(table, sb);
    }

    public int update(String table, UpdateBuilder updateBuilder) throws SQLException {
        return execute(updateBuilder.buildSql(table), updateBuilder.buildBindings());
    }

    public int update(String table, Consumer<UpdateBuilder> updateBuilderConsumer) throws SQLException {
        UpdateBuilder ub = new UpdateBuilder();
        updateBuilderConsumer.accept(ub);
        return update(table, ub);
    }

    public void insert(String table, InsertBuilder insertBuilder) throws SQLException {
        int result = execute(insertBuilder.buildSql(table), insertBuilder.buildBindings());
        assert result == 1;
    }

    public void insert(String table, Consumer<InsertBuilder> insertBuilderConsumer) throws SQLException {
        InsertBuilder ib = new InsertBuilder();
        insertBuilderConsumer.accept(ib);
        insert(table, ib);
    }

    public void insert(String table, Map<String,Object> fieldValues) throws SQLException {
        InsertBuilder ib = new InsertBuilder();
        ib.set(fieldValues);
        insert(table, ib);
    }

    public Map<String,Object> insertAndGetGeneratedKeys(String table, InsertBuilder insertBuilder) throws SQLException {
        List<Map<String, Object>> result = executeAndGetGeneratedKeys(insertBuilder.buildSql(table), insertBuilder.buildBindings());
        assert result.size() == 1;
        return result.get(0);
    }

    public Map<String, Object> insertAndGetGeneratedKeys(String table, Consumer<InsertBuilder> insertBuilderConsumer) throws SQLException {
        InsertBuilder ib = new InsertBuilder();
        insertBuilderConsumer.accept(ib);
        return insertAndGetGeneratedKeys(table, ib);
    }

    public Map<String, Object> insertAndGetGeneratedKeys(String table, Map<String,Object> fieldValues) throws SQLException {
        InsertBuilder ib = new InsertBuilder();
        ib.set(fieldValues);
        return insertAndGetGeneratedKeys(table, ib);
    }

    public Object insertAndGetKey(String table, InsertBuilder insertBuilder) throws SQLException {
        return insertAndGetGeneratedKeys(table, insertBuilder).values().iterator().next();
    }

    public Object insertAndGetKey(String table, Consumer<InsertBuilder> insertBuilderConsumer) throws SQLException {
        return insertAndGetGeneratedKeys(table, insertBuilderConsumer).values().iterator().next();
    }

    public Object insertAndGetKey(String table, Map<String,Object> fieldValues) throws SQLException {
        return insertAndGetGeneratedKeys(table, fieldValues).values().iterator().next();
    }


    public int delete(String table, DeleteBuilder deleteBuilder) throws SQLException {
        return execute(deleteBuilder.buildSql(table), deleteBuilder.buildBindings());
    }

    public int delete(String table, Consumer<DeleteBuilder> deleteBuilderConsumer) throws SQLException {
        DeleteBuilder db = new DeleteBuilder();
        deleteBuilderConsumer.accept(db);
        return delete(table, db);
    }

    public int delete(String table, Map<String,Object> conditions) throws SQLException {
        DeleteBuilder db = new DeleteBuilder();
        conditions.forEach(db::eq);
        return delete(table, db);
    }

    private List<Map<String, Object>> formatResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new LinkedList<>();
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        String[] colNames = new String[cols];
        for (int i = 0; i < cols; i++) {
            colNames[i] = md.getColumnName(i + 1);
        }
        while (rs.next()) {
            Map<String, Object> it = new LinkedHashMap<>();
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
