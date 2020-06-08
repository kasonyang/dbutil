package test.site.kason.dbutil;

import org.junit.Test;
import site.kason.dbutil.InsertBuilder;

import static org.junit.Assert.assertEquals;

/**
 * @author KasonYang
 */
public class InsertBuilderTest {

    @Test
    public void test() {
        InsertBuilder ib = new InsertBuilder();
        ib.set("f","1");
        assertEquals("insert into test(f) values (?)", ib.buildSql("test"));
    }

}
