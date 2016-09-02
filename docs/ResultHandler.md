# 3、数据的二次业务处理

通过 DistProcedure 得到的数据，特别是通过游标返回来的数据，如果没有二次处理，那么结果就是一个List，成员就是与记录所对应的model类(通过配置文件的vo属性来将记录映射到类的实例)。但是很多时候，数据是需要二次组合之后才能用的，或者说只需要部分数据。所以DistProcedure还提供了一些接口来满足这些要求。

### executeClass
这个属性指定了当DistProcedure从数据库得到结果之后，需要再执行的类，此类需要继承dist.common.procedure.define.ProcedureExecutor，也就是说在DistProcedure层面就进行了拦截。上一章节给过这么一个示例：

```
<procedure id="testPro">
    <proName>pro_test</proName>
    <!--<desc>this is a test</desc>
    <executeClass>dist.inteceptor.DistInteceptor</executeClass>
    <executeMethod>personHandler</executeMethod>-->
    <parameters>
        <parameter name="p_str" type="in" dataType="varchar"/>
        <parameter name="p_num" type="in" dataType="number" />
        <parameter name="p_strDate" type="in" dataType="date" format="yyyy-MM-dd" />
        <parameter name="p_date" type="in" dataType="date" />
        <parameter name="p_info" type="out" dataType="varchar" />
        <parameter name="p_cursor" type="out" dataType="cursor" vo="dist.dgp.controller.Person">
            <!--<rule ruleFile="distrule.drl" />-->
        </parameter>
    </parameters>
</procedure>
```
上面把 executeClass 屏蔽了，如果不屏蔽掉，当从数据库得到结果后，会实例化一个 dist.inteceptor.DistInteceptor 类，并将得到的结果传给此类中的某个方法，那传给什么方法？由下面的 executeMethod 节点指定。也就是说同样一个拦截类可以应用到多个存储过程中，只需要指定不同的executeMethod即可。

### executeMethod
该属性所代表的意思就是拦截类所需要执行的方法，此方法会接收一个 Map 类型的数据，key 就是 type 为 out 类型的 paramter 节点的 name 属性，value 就是未经过任何处理的结果(已映射完成)，返回值就是二次处理后的数据。比如返回如下结果的存储过程，需要在 p_cursor 中的记录的 name 属性前加“0”：

![](https://raw.githubusercontent.com/DistChen/distprocedure/master/docs/images/3.png)

dist.inteceptor.DistInteceptor 代码如下：

```
package dist.inteceptor;

import dist.common.procedure.define.ProcedureExecutor;
import dist.dgp.controller.Person;
import java.util.List;
import java.util.Map;

public class DistInteceptor extends ProcedureExecutor{

    public Object PersonHandler(Map<String,Object> data){
       ((List<Person>)data.get("p_cursor")).stream().forEach(person->person.setName("0"+person.getName()));
       return data;
    }
}
```
