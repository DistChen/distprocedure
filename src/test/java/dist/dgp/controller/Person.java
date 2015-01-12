package dist.dgp.controller;

import java.sql.Date;

/**
 * Created by dist on 14-12-30.
 */
public class Person {
    public String getNaMe() {
        return naMe;
    }

    public String getStrID() {
        return strID;
    }

    public void setStrID(String strID) {
        this.strID = strID;
    }

    private String strID;

    public void setNaMe(String naMe) {
        this.naMe = naMe;
    }

    private String naMe;


    public Date getAge() {
        return age;
    }

    public void setAge(Date age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    private Date age;
    private String school;
}
