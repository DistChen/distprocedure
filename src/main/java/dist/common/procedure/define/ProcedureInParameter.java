package dist.common.procedure.define;

public class ProcedureInParameter extends ProcedureParameter {

    private  String dateFormat="yyyy/MM/dd";

    public ProcedureInParameter() {
    }

    public ProcedureInParameter(String parameterName, Integer sqlType) {
        super(parameterName, sqlType);
    }
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}
