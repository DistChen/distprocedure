package dist.common.procedure.define;

import dist.common.rules.define.RuleInfo;

public class ProcedureOutPrameter extends ProcedureParameter {
    private Class voClass;
    private RuleInfo ruleInfo;


    public RuleInfo getRuleInfo() {
        return ruleInfo;
    }

    public void setRuleInfo(RuleInfo ruleInfo) {
        this.ruleInfo = ruleInfo;
    }

    public Class getVoClass() {
        return voClass;
    }

    public void setVoClass(Class voClass) {
        this.voClass = voClass;
    }

    public ProcedureOutPrameter() {}

    public ProcedureOutPrameter(String parameterName, Integer sqlType) {
        super(parameterName, sqlType);
    }

    public ProcedureOutPrameter(String parameterName, Integer sqlType, Class voClass) {
        super(parameterName, sqlType);
        this.voClass = voClass;
    }

    public ProcedureOutPrameter(String parameterName, Integer sqlType, Class voClass,RuleInfo ruleInfo) {
       this(parameterName, sqlType, voClass);
       this.ruleInfo=ruleInfo;
    }

}
