package site.kason.dbutil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author KasonYang
 */
public class WhereBuilder {

    private List<String> segments = new LinkedList<>();

    private List<Object> bindings = new LinkedList<>();

    private final static String OR = "or";

    private final static String AND = "and";

    public WhereBuilder eq(String column, Object value) {
        return addSeg(AND, column + "=?", value);
    }

    public WhereBuilder eq(String column, Collection<Object> values) {
        String seg = column + " in (" + values.stream().map(it -> "?").collect(Collectors.joining(",")) + ")";
        return addSeg(AND, seg, values);
    }

    public WhereBuilder eq(String column, Object... values) {
        return eq(column, Arrays.asList(values));
    }

    public WhereBuilder ne(String column, Object value) {
        return addSeg(AND, column + "<>?", value);
    }

    public WhereBuilder le(String column, Object value) {
        return addSeg(AND, column + "<=", value);
    }

    public WhereBuilder lt(String column, Object value) {
        return addSeg(AND, column + "<", value);
    }

    public WhereBuilder ge(String column, Object value) {
        return addSeg(AND, column + ">=", value);
    }

    public WhereBuilder gt(String column, Object value) {
        return addSeg(AND, column + ">", value);
    }

    public WhereBuilder notIn(String column, Collection<Object> values) {
        String seg = column + " not in (" + values.stream().map(it -> "?").collect(Collectors.joining(",")) + ")";
        return addSeg(AND, seg, values);
    }

    public WhereBuilder like(String column, Object value) {
        return addSeg(AND,column + " like ?", "%" + value + "%");
    }

    public WhereBuilder leftLike(String column, Object value) {
        return addSeg(AND,column + " like ?", value + "%");
    }

    public WhereBuilder rightLike(String column, Object value) {
        return addSeg(AND,column + " like ?", "%" + value);
    }

    public WhereBuilder and(Consumer<WhereBuilder> whereBuilderConsumer) {
        return addSeg(AND, whereBuilderConsumer);
    }

    public WhereBuilder or(Consumer<WhereBuilder> whereBuilderConsumer) {
        return addSeg(OR, whereBuilderConsumer);
    }

    protected String buildSql() {
        return String.join(" ", segments);
    }

    protected Object[] buildBindings() {
        return bindings.toArray(new Object[0]);
    }

    private WhereBuilder addSeg(String operator, Consumer<WhereBuilder> whereBuilderConsumer) {
        WhereBuilder wb = new WhereBuilder();
        whereBuilderConsumer.accept(wb);
        if (wb.bindings.isEmpty()) {
            return this;
        } else if (wb.bindings.size() == 1) {
            return addSeg(operator, wb.segments.get(0), wb.bindings);
        } else {
            return addSeg(operator, "(" + wb.buildSql() + ")", wb.bindings);
        }
    }

    private WhereBuilder addSeg(String operator, String segment, Object binding) {
        return addSeg(operator, segment, Collections.singleton(binding));
    }

    private WhereBuilder addSeg(String operator, String segment, Collection<Object> bindings) {
        this.bindings.addAll(bindings);
        if (segments.isEmpty()) {
            segments.add(segment);
        } else {
            segments.add(operator + " " + segment);
        }
        return this;
    }


}
