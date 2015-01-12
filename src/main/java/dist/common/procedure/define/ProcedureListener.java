package dist.common.procedure.define;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by dist on 14-12-30.
 */
public class ProcedureListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
       String procedureFiles = servletContextEvent.getServletContext().getInitParameter("procedureFiles");
       String[]fileNames=procedureFiles.split("[\\s,]");
       ProcedureRepository.setProcedures(ProcedureFile.loadFile(fileNames));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
       DBConnector.closeConn();
    }
}
