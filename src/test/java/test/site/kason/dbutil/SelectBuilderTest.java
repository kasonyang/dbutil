package test.site.kason.dbutil;

import org.junit.Assert;
import org.junit.Test;
import site.kason.dbutil.SelectBuilder;

/**
 * @author KasonYang
 */
public class SelectBuilderTest {

    @Test
    public void test() {
        String table = "test";
        SelectBuilder wb = new SelectBuilder();
        wb.eq("name", "test");
        wb.eq("id",5);
        Assert.assertEquals("select * from test where name=? and id=?", wb.buildSql(table).trim());
        Assert.assertArrayEquals(new Object[]{"test", 5}, wb.buildBindings());

        wb.or(ob -> ob.eq("len", 4));
        Assert.assertEquals("select * from test where name=? and id=? or len=?", wb.buildSql(table).trim());

        wb.or(ob -> ob.eq("size", 4).eq("height", 2));
        Assert.assertEquals("select * from test where name=? and id=? or len=? or (size=? and height=?)", wb.buildSql(table).trim());
        Assert.assertArrayEquals(new Object[]{"test", 5, 4, 4, 2}, wb.buildBindings());
    }

}
