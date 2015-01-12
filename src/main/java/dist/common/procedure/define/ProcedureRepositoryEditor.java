package dist.common.procedure.define;

import java.beans.PropertyEditorSupport;


public class ProcedureRepositoryEditor extends PropertyEditorSupport {


    @Override
    public void setAsText(String text) {
        try {
            String [] resources=text.split("[, ]");
            super.setValue(ProcedureFile.loadFile(resources));
        }
        catch (Exception e){
            super.setValue(text);
        }
    }
}
