package dist.common.procedure.define;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ChenYanping on 14-12-30.
 */
public class DBConnector {

    public static Connection conn = null;

    public static void conn(String driver,String url,String userName,String password){
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeConn(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
