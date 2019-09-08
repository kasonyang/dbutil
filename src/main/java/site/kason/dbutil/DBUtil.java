package site.kason.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author KasonYang
 */
public final class DBUtil {

    public static DBConnection connect(String jdbcUrl, String user, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        return new DBConnection(conn);
    }

    public static DBConnection connect(String jdbcUrl) throws SQLException {
        return new DBConnection(DriverManager.getConnection(jdbcUrl));
    }

}
