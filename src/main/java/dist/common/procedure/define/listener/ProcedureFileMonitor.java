package dist.common.procedure.define.listener;

import dist.common.procedure.define.ProcedureFile;
import dist.common.procedure.define.ProcedureRepository;
import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ChenYanping on 15-1-12.
 */
public class ProcedureFileMonitor {

    private static Logger log=Logger.getLogger(ProcedureFileMonitor.class);

    private List<ListeneredFile> files=new ArrayList<ListeneredFile>();
    private Timer timer;
    private long pollingInterval=1000;  //默认的检测时间间隔是 1 秒

    public ProcedureFileMonitor(String [] filePath) {
        initListeneredFiles(filePath);
        this.timer=new Timer(true);
    }

    public ProcedureFileMonitor(String [] filePath,Long minutes) {
        this(filePath);
        if (minutes!=null){
            this.pollingInterval=1000*minutes;
        }
    }

    private void initListeneredFiles(String [] filePath){
        for (String path:filePath){
            try {
                if (!path.isEmpty()){
                    if (!path.matches("^[a-zA-Z]:.*$")){
                        path= URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(path).getPath());
                    }
                    log.debug("启动对["+path+"]配置文件的监听.");
                    files.add(new ListeneredFile(path));
            }
            }catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }

    public void start(){
        this.timer.schedule(new FileMonitor(), 0, pollingInterval);
    }

    public void stop(){
        log.debug("结束监听存储过程配置文件:");
        this.timer.cancel();
    }

    /**
     * 设置检测时间间隔
     * @param minutes
     */
    public void setPollingInterval(long minutes) {
        this.pollingInterval = 1000*minutes;
    }

    private class FileMonitor extends TimerTask {
        public void run() {
           for (ListeneredFile file:files){
               if (file.check()){
                   log.debug("配置文件[" + file.getFilePath() + "]发生了变化！");
                   log.debug("---------重加载配置文件---------");
                   ProcedureRepository.addProcedures(ProcedureFile.loadFile(file.getFilePath()));
                   log.debug("---------文件重加载加载完毕---------");
               }
           }
        }
    }
}
