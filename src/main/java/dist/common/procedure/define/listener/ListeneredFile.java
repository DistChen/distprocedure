package dist.common.procedure.define.listener;

import java.io.File;

/**
 * Created by ChenYanping on 15-1-12.
 */
public class ListeneredFile {

    private long currentTime=-1;
    private String filePath;
    private File file;

    public ListeneredFile(String filePath){
        this.filePath=filePath;
        this.loadFile();
    }

    private boolean loadFile(){
        this.file=new File(this.filePath);
        if (this.file.exists()){
            if (this.currentTime!=file.lastModified()){
                this.currentTime=this.file.lastModified();
                return true;
            }else{
                return false;
            }
        }else {
            return false;
        }
    }

    public boolean check(){
        return this.loadFile();
    }

    public String getFilePath() {
        return filePath;
    }
}
