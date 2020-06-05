package site.kason.dbutil;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author KasonYang
 */
public class SelectBuilder extends WhereBuilder {

    private List<String> orderByList = new LinkedList<>();

    private List<String> groupByList = new LinkedList<>();

    private String selectFields = "*";

    private String limit = "";

    public SelectBuilder orderBy(String orderExpr) {
        orderByList.add(orderExpr);
        return this;
    }

    public SelectBuilder groupBy(String groupExpr) {
        groupByList.add(groupExpr);
        return this;
    }

    public SelectBuilder select(Collection<String> fields) {
        selectFields = String.join(",", fields);
        return this;
    }

    public SelectBuilder select(String... fields) {
        return select(Arrays.asList(fields));
    }

    public SelectBuilder limit(long offset, long size) {
        limit = offset + "," + size;
        return this;
    }

    public SelectBuilder limit(long size) {
        limit = String.valueOf(size);
        return this;
    }

    public String buildCount(String table) {
        return buildSql(table, "count(*)");
    }

    public String buildSql(String table) {
        return buildSql(table, selectFields);
    }

    public Object[] buildBindings() {
        return super.buildBindings();
    }

    private String buildSql(String table, String fields) {
        String where = super.buildSql();
        String whereSeg = where.trim().isEmpty() ? "" : (" where " + where + " ");
        String groupBySeg = groupByList.isEmpty() ? "" : (" group by " + String.join(",", groupByList) + " ");
        String orderBySeg = orderByList.isEmpty() ? "" : (" order by " + String.join(",", orderByList) + " ");
        String limitSeg = limit.trim().isEmpty() ? "" : (" limit " + limit);
        return "select " + fields + " from " + table + whereSeg + groupBySeg + orderBySeg + limitSeg;
    }

}
