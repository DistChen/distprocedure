package dist.common.procedure.define;

import java.util.List;

/**
 * @author ChenyanPing
 * @date 2014/10/30
 */

public class ProcedureModel {
    private String procedureName;
    private String executeClass = "dist.common.procedure.define.ProcedureExecutor";
    private String executeMethod = "execute";
    private String desc="there is no description";

    private List<ProcedureParameter> procedureParameters;

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public List<ProcedureParameter> getProcedureParameters() {
        return procedureParameters;
    }

    public void setProcedureParameters(List<ProcedureParameter> procedureParameters) {
        this.procedureParameters = procedureParameters;
    }

    public String getExecuteClass() {
        return executeClass;
    }

    public void setExecuteClass(String executeClass) {
        this.executeClass = executeClass;
    }

    public String getExecuteMethod() {
        return executeMethod;
    }

    public void setExecuteMethod(String executeMethod) {
        this.executeMethod = executeMethod;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
