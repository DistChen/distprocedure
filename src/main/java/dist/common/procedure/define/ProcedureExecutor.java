package dist.common.procedure.define;


import dist.common.rules.define.RuleEngine;
import dist.common.rules.define.RuleInfo;
import dist.common.rules.define.RuleObject;
import oracle.jdbc.OracleTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author ChenyanPing
 * @date 2014/10/30
 * @description 存储过程执行器
 */
public class ProcedureExecutor  {

    private static Logger log=Logger.getLogger(ProcedureExecutor.class);

    private static void registerParameter(List<ProcedureParameter> paras, List<Object> values, CallableStatement cs) {
        try {
            for (ProcedureParameter para:paras){
                int i=paras.indexOf(para);
                if (para instanceof ProcedureInParameter){
                    if (para.getSqlType()==OracleTypes.DATE && values.get(i) instanceof String){
                        SimpleDateFormat dateFormat=new SimpleDateFormat(((ProcedureInParameter) para).getDateFormat());
                        java.util.Date date=dateFormat.parse((String)values.get(i));
                        cs.setObject(para.getParameterName(), new java.sql.Date(date.getTime()));
                    }else if(values.get(i) instanceof java.util.Date){
                        cs.setObject(para.getParameterName(), new java.sql.Date(((Date) values.get(i)).getTime()));
                    }else{
                        cs.setObject(para.getParameterName(), values.get(i));
                    }

                }else{
                    cs.registerOutParameter(para.getParameterName(),para.getSqlType());
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    public Object execute(ProcedureModel model,List<Object> values)  {
        try {
            Map<String,Object> results;
            String procedureName=String.format("{call %s(%s)}",model.getProcedureName(), StringUtils.repeat("?,",model.getProcedureParameters().size()-1)+"?");
            CallableStatement cs=DBConnector.conn.prepareCall(procedureName);
            registerParameter(model.getProcedureParameters(), values, cs);
            cs.execute();
            results=getResults(model.getProcedureParameters(), cs);
            cs.close();
            return handlerResult(results);
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Map<String,Object> getResults(List<ProcedureParameter> paras, CallableStatement cs) throws SQLException, InstantiationException, IllegalAccessException {
        Map<String, Object> results = new HashMap<String, Object> ();
        for (ProcedureParameter para:paras){
            if (para instanceof ProcedureOutPrameter){
                String key=para.getParameterName();
                if (para.getSqlType()== OracleTypes.CURSOR){
                    results.put(key, resultMapper(cs, para));
                }else{
                    results.put(key, cs.getObject(key));
                }
            }
        }
        return results;
    }

    /**
     * 结果映射
     * @param cs
     * @param para
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private Object resultMapper(CallableStatement cs, ProcedureParameter para) throws SQLException, IllegalAccessException, InstantiationException {
        List<Object> res=new ArrayList<Object>();
        ResultSet rs= (ResultSet) cs.getObject(para.getParameterName());
        ProcedureOutPrameter outPara=(ProcedureOutPrameter)para;
        while (rs.next()){
            Object instance = outPara.getVoClass().newInstance();
            Method[]methods=instance.getClass().getDeclaredMethods();
            for (Method method:methods){
                if (method.getName().startsWith("set")){//method.getParameterCount()==1
                    try {
                        String fieldName=method.getName().substring(3);
                        Object fieldValue= rs.getObject(fieldName);
                        method.invoke(instance,new Object[]{fieldValue});
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            res.add(instance);
        }
        rs.close();
        return ruleHandler(outPara,res);
    }

    /**
     * 结果规则处理
     * @param para
     * @param res
     * @return
     */
    private Object ruleHandler(ProcedureOutPrameter para,List<Object> res){
        RuleInfo ruleInfo=para.getRuleInfo();
        if (ruleInfo!=null && ruleInfo.getRuleFilePath()!=null){
            RuleObject obj=new RuleObject(res);
            RuleEngine.executeRule(ruleInfo.getRuleFilePath(),
                                   obj,
                                   ruleInfo.getGlobals(),
                                   ruleInfo.getRuleGroup(),
                                   ruleInfo.getFilterKey(),
                                   ruleInfo.getFilterType());
            return obj.getResult();
        }else{
            return res;
        }
    }

    public Object handlerResult(Object obj) {
        return obj;
    }

}
