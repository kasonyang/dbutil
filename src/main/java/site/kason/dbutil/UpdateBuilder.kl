package site.kason.dbutil;

import java.util.*;

/**
 * @author KasonYang
 */
public class UpdateBuilder extends WhereBuilder {

    private Map<String, Object> fieldValues = new LinkedHashMap<>();

    public UpdateBuilder set(String column, Object value) {
        fieldValues.put(column, value);
        return this;
    }

    public UpdateBuilder set(Map<String,Object> fieldValues) {
        this.fieldValues.putAll(fieldValues);
        return this;
    }

    public String buildSql(String table) {
        if (fieldValues.isEmpty()) {
            throw new IllegalStateException("update fields is empty");
        }
        StringBuilder setSegBuilder = new StringBuilder();
        for (Map.Entry<String, Object> fv : fieldValues.entrySet()) {
            setSegBuilder.append(fv.getKey()).append("=?,");
        }
        setSegBuilder.setLength(setSegBuilder.length() - 1);
        String setSeg = " set " + setSegBuilder.toString() + " ";
        String whereSql = super.buildSql();
        String whereSeg = whereSql.trim().isEmpty() ? "" : (" where " + whereSql + " ");
        return "update " + table + setSeg + whereSeg;
    }

    public Object[] buildBindings() {
        List<Object> bindings = new LinkedList<>();
        bindings.addAll(fieldValues.values());
        bindings.addAll(Arrays.asList(super.buildBindings()));
        return bindings.toArray();
    }

}
