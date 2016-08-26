package dist.dgp.controller;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by dist on 14-12-30.
 */
public class Person {

    private BigDecimal id;
    private Date age;
    private String school;
    private String name;
    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


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

}
