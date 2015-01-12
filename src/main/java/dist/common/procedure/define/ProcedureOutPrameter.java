package dist.common.procedure.define;

public class ProcedureOutPrameter extends ProcedureParameter {
    private Class voClass;

    public Class getVoClass() {
        return voClass;
    }

    public void setVoClass(Class voClass) {
        this.voClass = voClass;
    }

    public ProcedureOutPrameter() {
    }

    public ProcedureOutPrameter(String parameterName, Integer sqlType) {
        super(parameterName, sqlType);
    }

    public ProcedureOutPrameter(String parameterName, Integer sqlType, Class voClass) {
        super(parameterName, sqlType);
        this.voClass = voClass;
    }

}
