package dist.dgp.util;

import dist.common.procedure.define.ProcedureCaller;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by dist on 14-12-23.
 */
public class ApplicationContextUtil implements ApplicationContextAware{
    private static ApplicationContext appContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext=applicationContext;
        //ProcedureCaller.appContext=applicationContext;
    }
    public ApplicationContext getApplicationContext(){
        return this.appContext;
    }

    public static Object getBean(String name){
        if (appContext==null){
             loadApplicationContext();
        }
        return appContext.getBean(name);
    }
    private static void loadApplicationContext(){
        appContext=new ClassPathXmlApplicationContext("applicationContext.xml");
    }
}
