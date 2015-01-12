package dist.common.procedure.define;

public abstract class ProcedureParameter {
    private String parameterName;
    private Integer sqlType;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Integer getSqlType() {
        return sqlType;
    }

    public void setSqlType(Integer sqlType) {
        this.sqlType = sqlType;
    }

    public ProcedureParameter() {
    }

    public ProcedureParameter(String parameterName, Integer sqlType) {
        this.parameterName = parameterName;
        this.sqlType = sqlType;
    }

}
