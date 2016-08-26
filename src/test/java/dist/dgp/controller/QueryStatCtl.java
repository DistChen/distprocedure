package dist.dgp.controller;

import dist.common.procedure.define.ProcedureCaller;
import dist.common.procedure.define.ProcedureModel;
import dist.common.procedure.define.ProcedureRepository;

import java.util.Date;
import java.util.Map;

/**
 * Created by dist on 14-12-23.
 */
public class QueryStatCtl {
    private Map<String,ProcedureModel> features;

    public Object testPro(String featureName,
                          String str,
                          String str2,
                          Integer num,
                          String strDate,
                          Date date){
        return (Map)ProcedureCaller.call(ProcedureRepository.getProcedure(featureName),str,str2,num,strDate,date);
    }


    /**
     *
     * @param featureID
     * @param strDate
     * @param date
     * @return
     */
    public Object testDate(String featureID,String strDate,Date date){
        return (Map)ProcedureCaller.call(this.features.get(featureID),strDate,date);
    }

    public Map<String, ProcedureModel> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, ProcedureModel> features) {
        this.features = features;
    }
}
