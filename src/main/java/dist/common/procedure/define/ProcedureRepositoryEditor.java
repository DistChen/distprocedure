package dist.common.procedure.define;

import org.apache.log4j.Logger;

import java.beans.PropertyEditorSupport;
import java.util.Map;


public class ProcedureRepositoryEditor extends PropertyEditorSupport {

    private static Logger log=Logger.getLogger(ProcedureRepositoryEditor.class);

    @Override
    public void setAsText(String text) {
        try {
            String [] resources=text.split("[, ]");
            Map<String, ProcedureModel> obj=ProcedureFile.loadFile(resources);
            ProcedureRepository.setProcedures(obj);
            super.setValue(obj);
        }
        catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            super.setValue(text);
        }
    }
}
