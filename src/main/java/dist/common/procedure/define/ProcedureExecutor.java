package dist.common.procedure.define;


import oracle.jdbc.OracleTypes;
import org.apache.commons.lang.StringUtils;

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
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String,Object> getResults(List<ProcedureParameter> paras, CallableStatement cs) throws SQLException, InstantiationException, IllegalAccessException {
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

    private static List<Object> resultMapper(CallableStatement cs, ProcedureParameter para) throws SQLException, IllegalAccessException, InstantiationException {
        List<Object> res=new ArrayList<Object>();
        ResultSet rs= (ResultSet) cs.getObject(para.getParameterName());
        while (rs.next()){
            Object instance = ((ProcedureOutPrameter)para).getVoClass().newInstance();
            Method[]methods=instance.getClass().getDeclaredMethods();
            for (Method method:methods){
                if (method.getName().startsWith("set")){//method.getParameterCount()==1
                    try {
                        String fieldName=method.getName().substring(3);
                        Object fieldValue= rs.getObject(fieldName);
                        method.invoke(instance,new Object[]{fieldValue});
                    }catch (Exception e) {

                    }
                }
            }
            res.add(instance);
        }
        rs.close();
        return res;
    }

    public Object handlerResult(Object obj) {
        return obj;
    }

}
