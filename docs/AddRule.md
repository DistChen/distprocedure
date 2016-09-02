# 4、启用规则引擎

上一章节所说的通过添加 executeClass 和 executeMethod 来进行二次业务处理是一种常规的方式，但是这样有一个很不好的地方：如果下次要求补上1而不是0，就需要重新编写代码并发布。也就是说代码与业务逻辑严重耦合了，为了解耦代码与业务逻辑，在 DistProcedue 引入了[规则引擎Drools](http://www.drools.org/)。当业务逻辑再次变更时，不需要修改修改代码，不需要重新发布，只修改规则文件即可满足变更的需求。

### 添加规则文件
之前有这样一个示例的配置片段：

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
上面代码中屏蔽掉的 rule 节点就是用来引用一个规则文件的，也可以使用绝对路径来引用规则文件。当 type 为 out 类型的 parameter 节点添加 rule 子节点时，表示这个结果映射为model类之后，会按照规则文件里面的要求进行一次规则处理，之后才会将结果返回，所以此时就可以不用 executeClass 拦截类的方式来处理了。在rule节点中还可以指定group、filterKey、filterType 等属性。因为一个规则文件中可以有很多规则，这些属性就确定了只执行某些规则。

下面是一个编写好的规则示例：

```
import dist.common.rules.define.*
import java.util.List
import dist.dgp.controller.Person

rule "testPro"
    no-loop true
    salience 1
    when
        obj:RuleObject(source!=null,source instanceof List)
    then
        try {
             List<Person> list=(List<Person>) obj.getSource();
             list.stream().forEach(person->person.setName("0"+person.getName()))
             Document document=CommonUtil.createDocument();
             obj.setResult(list);
        } catch (Exception e) {
             e.printStackTrace();
        }
end
```

当规则文件更新后，DistProcedure 会自动检测到变化并重新编译，无须重启程序。关于规则文件如何编写，里面的相关概念等知识点，请自行参考[规则引擎Drools](http://www.drools.org/)学习。