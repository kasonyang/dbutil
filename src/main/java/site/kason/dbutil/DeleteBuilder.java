package site.kason.dbutil;

/**
 * @author KasonYang
 */
public class DeleteBuilder extends WhereBuilder {

    public String buildSql(String table) {
        String where = super.buildSql();
        String whereSeg = where.trim().isEmpty() ? "" : (" where " + where + " ");
        return "delete from " + table + whereSeg;
    }

    public Object[] buildBindings(){
        return super.buildBindings();
    }

}
