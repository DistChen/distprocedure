package dist.common.procedure.define;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ChenYanping on 14-12-30.
 */
public class DBConnector {

    private static Logger log=Logger.getLogger(DBConnector.class);

    public static Connection conn = null;

    public static void conn(String driver,String url,String userName,String password){
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, userName, password);
            if (conn!=null){
                log.debug("数据库连接成功");
            }else{
                log.debug("数据库连接失败");
            }
        } catch (Exception e) {
            log.error("数据库连接失败:"+e.getMessage());
            e.printStackTrace();
        }
    }

    public static void closeConn(){
        try {
            conn.close();
        } catch (SQLException e) {
            log.error("数据库关闭异常:"+e.getMessage());
            e.printStackTrace();
        }
    }
}
