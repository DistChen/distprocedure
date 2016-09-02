# 2、添加存储过程配置文件

上面反复提到了**存储过程配置文件**，那这个文件是干嘛用的，该怎么写？在数据库里编写了一个存储过程之后，DistProcedure 能够帮助你不用编写任何代码来完成调用及结果映射过程，其与数据库沟通的桥梁就是这个配置文件，这个配置文件相当于告诉DistProcedure:你可以调用哪些存储过程，它们各自的参数是什么，返回类型是什么，数据是否需要二次业务处理等信息。
> 此配置文件的编写规范写在了这个 [XML Schema](https://distchen.gitbooks.io/distprocedure/content/docs/distfeatures_xsd.html) 中。
>
>在IDE中使用这个XML Schema可以简化编写过程并校验配置文件，可以智能提示及实时的错误提示，如下所示：
![](https://raw.githubusercontent.com/DistChen/distprocedure/master/docs/images/1.png)
![](https://raw.githubusercontent.com/DistChen/distprocedure/master/docs/images/2.png)

### 添加数据源
在配置文件中需要指定数据源(未用连接池，请忽略，内部版本直接与spring使用的，不需要配置数据源)，不然DistProcedure不知道该连接哪个数据库，一个示例配置如下：。
> 注意：如果是多配置文件，在任意一个配置文件中指定数据源即可。

```
<datasource>
    <driver>oracle.jdbc.driver.OracleDriver</driver>
    <url>jdbc:oracle:thin:@127.0.0.1:1521:ORCL</url>
    <username>scott</username>
    <password>pass</password>
</datasource
```
很多情况下，关于数据源等配置信息是写在一个属性文件里面的，这里配置的时候也可以指定属性文件里面的配置信息，如下所示：

```
<datasource src="config.properties">
    <driver>${driverClassName}</driver>
    <url>${url}</url>
    <username>${username}</username>
    <password>${password}</password>
</datasource>
```

### 添加存储过程

数据源添加完毕之后，就可以开始添加相应的存储过程了。数据库中如果有这样的一个存储过程定义：

```
create or replace procedure pro_test(
       p_str in varchar2,
       p_num in number,
       p_strDate in date,
       p_date in date,
       p_info out varchar2,
       p_cursor out sys_refcursor
)is
begin
  p_info := p_str||p_num||to_char(p_date,'yyyy-mm-dd');
  open p_cursor for select * from(
       select 1 id,sysdate age,'CUG' school, '张三' name from dual
       union
       select 2 id,sysdate age,'HUST' school, '李四' name from dual
  );
end pro_test;
```
![](https://raw.githubusercontent.com/DistChen/distprocedure/master/docs/images/3.png)

那么在存储过程配置文件中如下配置即可：

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
可以看到，基本上两者是对应的关系。如此配置好之后，在程序中如下调用即可：
```
//通过id属性找到配置文件中所配置的存储过程
ProcedureModel pro = ProcedureRepository.getProcedure("testPro");
//调用存储过程并方法结果
//第一个参数代码存储过程模型，由DistProcedure根据配置文件构造出来
//第二个参数是一个可变长参数，代表传入存储过程的参数值，按照配置的顺序添加即可
Object result = ProcedureCaller.call(pro,"字符串数据",100,new Date(),new Date());
//返回的结果是map类型的，其中key为配置的name属性值，value 为相应的类型
Map<String,Object> values=(Map<String,Object>)result;
System.out.println(values.get("p_info"));
List<Person> persons = values.get("p_cursor");
persons.stream().forEach(person->System.out.println(perso.getName()));
```

上面的参数从字面上来看很好理解：
> 1. procedure 节点；代表一个存储过程，只有一个id属性，用来唯一标识此存储过程。ProcedureRepository 类会根据此id找到对应的存储过程信息(rocedureModel)。
> 2. proName 节点:procedure 子节点，必须。数据库中此存储过程的名称。
> 3. desc 节点，procedure 子节点，可选。描述信息
> 4. executeClass 节点，procedure 子节点，可选。用于数据二次业务处理用，下章介绍。
> 5. executeMethod 节点，procedure 子节点，可选，executeClass 配置后，此节点需要配置。用于数据二次业务处理用，下章介绍。
> 6. parameters 节点:procedure 子节点，必须。存储过程所对应的in、out 参数
> 7. parameter 节点:parameters 子节点,代表存储过程的参数信息.
> > name属性，必须。存储过程参数名称。
> >
> > type属性，必须。存储过程参数类型，可取值：in、out。在[XML Schema](https://distchen.gitbooks.io/distprocedure/content/docs/distfeatures_xsd.html) 已限定。
> >
> > dataTyep属性，必须。存储过程参数数据类型，可取值：varchar、number、blob、clob、cursor、integer、date，在[XML Schema](https://distchen.gitbooks.io/distprocedure/content/docs/distfeatures_xsd.html) 已限定。
> >
> > format 属性，可选。当dataType属性为date，指定的格式化形式。
> >
> > vo 属性，当dataType属性为cursor时，必须指定的值。此值代表返回的记录所对应的model类。
> >
> 8. rule 节点，parameter 子节点，用于指定规则文件路径，也可以用绝对路径，下一章介绍。

上面就是添加一个存储过程后，配置文件中针对此配置文件需要配置的内容，配置内容不是很多，在IDE中还有 [XML Schema](https://distchen.gitbooks.io/distprocedure/content/docs/distfeatures_xsd.html) 来帮你智能提示和校验。如果从数据库返回来的数据不再需要处理，那么上面的配置就足够了，如果还需要处理，那就需要用到 executeClass 、executeMethod 、rule 配置信息来进行[数据的二次业务处理](https://distchen.gitbooks.io/distprocedure/content/docs/ResultHandler.html)了。

参考：[存储过程配置示例文件](https://distchen.gitbooks.io/distprocedure/content/docs/distfeatures.html)

参考:[存储过程配置文件编写规范文档 XML schema](https://distchen.gitbooks.io/distprocedure/content/docs/distfeatures_xsd.html)