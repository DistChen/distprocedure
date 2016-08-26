package dist.dgp.test;

import dist.common.procedure.define.ProcedureCaller;
import dist.common.procedure.define.ProcedureFile;
import dist.common.procedure.define.ProcedureRepository;
import dist.dgp.controller.Person;
import dist.dgp.controller.QueryStatCtl;
import dist.dgp.util.ApplicationContextUtil;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


import java.util.*;
import java.util.Date;

/**
 * Created by dist on 14-12-23.
 */
public class AppTest {

    private static Logger log=Logger.getLogger(AppTest.class);
    @Test
    public void testGetCount(){
        /*QueryStatCtl ctl=(QueryStatCtl) ApplicationContextUtil.getBean("QueryStatCtl");
        Map<String,Object> obj=(Map<String,Object>)ctl.testPro("testPro",
                                                                "dataType配置为varchar",
                                                                "dataType配置为12(varchar)",
                                                                100,
                                                                "2010-08-22",
                                                               new Date());
        Object result=obj.get("p_cursor");
        if(result instanceof String){
            log.info(obj.get("p_cursor").toString());
        }else{
            for (Person person:(List<Person>)result){
                log.info(person.getName()+"   "+person.getSchool()+"    "+person.getAge().toLocaleString()+"   "+person.getId());
            }
        }*/
    }

    @Test
    public void testGet(){
       /* ProcedureRepository.setProcedures(ProcedureFile.loadFile(new String[]{"distfeatures.xml"}));
        Map<String,Object> obj = (Map<String,Object>)ProcedureCaller.call(ProcedureRepository.getProcedure("testPro"),
                                                              "dataType配置为varchar",
                                                              "dataType配置为12(varchar)",
                                                              100,
                                                              new java.sql.Date(new Date().getTime()),
                                                              new Date());

        Object result=obj.get("p_cursor");
        if(result instanceof String){
            log.info(obj.get("p_cursor").toString());
        }else{
            for (Person person:(List<Person>)result){
                log.info(person.getName()+"   "+person.getSchool()+"    "+person.getAge().toLocaleString()+"   "+person.getId());
            }
        }*/
    }


}
