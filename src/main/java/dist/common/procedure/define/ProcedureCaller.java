package dist.common.procedure.define;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


/**
 * @author ChenyanPing
 * @date 2014/10/30
 * @description 存储过程调用者
 */

public class ProcedureCaller {
    /**
     * 调用存储过程
     *
     * @param procedureModel 某个功能对应的存储过程model
     * @param values         参数数组，顺序与存储过程保持一致
     * @return Map<String,Object>
     */
    public static Object call(ProcedureModel procedureModel,Object...values) {
            if (procedureModel != null) {
                try {
                    Class<?> cls = Class.forName(procedureModel.getExecuteClass());
                    Method method = cls.getMethod(procedureModel.getExecuteMethod(), new Class[]{ProcedureModel.class, List.class});
                    Object instance = cls.newInstance();
                    return method.invoke(instance, new Object[]{procedureModel, Arrays.asList(values)});
                } catch (ClassNotFoundException e) {
                    System.out.println("未找到类" + procedureModel.getExecuteClass() + ",请添加该类所在jar包。");
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    System.out.println("在类" + procedureModel.getExecuteClass() + "中未找到" + procedureModel.getExecuteMethod() + "方法。");
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    System.out.println("方法" + procedureModel.getExecuteMethod() + "的参数有误。");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            } else {
                System.out.println("请配置该功能对应的存储过程模型.");
                return null;
            }
    }
}
