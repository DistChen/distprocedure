package dist.dgp.test;

import dist.common.procedure.define.ProcedureCaller;
import dist.common.procedure.define.ProcedureFile;
import dist.common.procedure.define.ProcedureRepository;
import dist.dgp.controller.Person;
import dist.dgp.controller.QueryStatCtl;
import dist.dgp.util.ApplicationContextUtil;
import org.junit.Assert;
import org.junit.Test;


import java.util.*;
import java.util.Date;

/**
 * Created by dist on 14-12-23.
 */
public class AppTest {


    @Test
    public void testGetCount(){
        QueryStatCtl ctl=(QueryStatCtl) ApplicationContextUtil.getBean("QueryStatCtl");
        Map<String,Object> obj=(Map<String,Object>)ctl.testPro("testPro",
                                                                "dataType配置为varchar",
                                                                "dataType配置为12(varchar)",
                                                                100,
                                                                "2010-08-22",
                                                               new Date());
        Assert.assertNotNull(obj.get("p_info"));
        System.out.println(obj.get("p_info").toString());
        for (Person person:(List<Person>)obj.get("p_cursor")){
            System.out.println(person.getNaMe()+"   "+person.getSchool()+"    "+person.getAge().toLocaleString());
        }
    }

    @Test
    public void testGet(){
        ProcedureRepository.setProcedures(ProcedureFile.loadFile(new String[]{"features.xml"}));
        Map<String,Object> obj = (Map<String,Object>)ProcedureCaller.call(ProcedureRepository.getProcedure("testPro"),
                                                              "dataType配置为varchar",
                                                              "dataType配置为12(varchar)",
                                                              100,
                                                              new java.sql.Date(new Date().getTime()),
                                                              new Date());

        Assert.assertNotNull(obj.get("p_info"));
        System.out.println(obj.get("p_info").toString());
        for (Person person:(List<Person>)obj.get("p_cursor")){
            System.out.println(person.getNaMe()+"   "+person.getSchool()+"    "+person.getAge().toLocaleString());
        }

    }
}
