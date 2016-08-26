package dist.common.procedure.define.listener;

import dist.common.procedure.define.DBConnector;
import dist.common.procedure.define.ProcedureFile;
import dist.common.procedure.define.ProcedureRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by ChenYanping on 14-12-30.
 */
public class ProcedureListener implements ServletContextListener {

    ProcedureFileMonitor fileMonitor;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
       String [] fileNames = servletContextEvent.getServletContext().getInitParameter("ProcedureFiles").split("[\\s,]");
       String autoDeploy = servletContextEvent.getServletContext().getInitParameter("ProcedureFileAutoupdate");
       if (autoDeploy!=null && autoDeploy.equalsIgnoreCase("true")){
           String ListenerFileInterval = servletContextEvent.getServletContext().getInitParameter("ProcedureFileInterval");
           fileMonitor=new ProcedureFileMonitor(fileNames,Long.parseLong(ListenerFileInterval));
           fileMonitor.start();
       }
       ProcedureRepository.setProcedures(ProcedureFile.loadFile(fileNames));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
       DBConnector.closeConn();
       if (this.fileMonitor!=null){
           this.fileMonitor.stop();
       }
    }
}
