整体功能说明:用于后台调用存储过程的配置式实现，从业务代码中脱离出来。
整体配置说明:1、根节点为procedures
             2、每一个存储过程对应一个 procedure 节点，procedure节点为procedure子节点
             3、procedure 节点共有name、desc、proName、executeClass、excuteMethod、parameters六种子节点，其中desc、executeClass、excuteMethod三种为可选项，其余为必选项
		name：必选项，功能名，前端调用需要传此值
		desc：必选项，该功能的描述信息
		proName：必选项，存储过程名
		executeClass：可选项，存储过程的执行类，默认的执行类为dist.common.procedure.define.ProcedureExecutor，
			      该执行类无法对从数据库中返回的数据做二次业务处理。如有需要对从数据库返回的数据做
			      二次处理，请继承该类，然后重写 handerResult 方法，该方法的入参即为从数据库返回
			      的数据。
		executeMethod：可选项执行类的默认执行方法，默认方法为 execute，也是为方便二次开发设置
		parmeters：必选项，存储过程参数，参数顺序要保持对应，包含了0个或多个子节点 parameter 
	     4、paramters 节点包含 parameter 节点，parameter 节点的属性有：
	        name：参数名，与存储过程对应，必选
	        type：取值 in 或者 out，表明该参数为传入还是传出，必选
	        dataType：参数类型，比如varchar代表字符串，date 代表日期类型参数,cursor 代表游标，也可以传具体值，比如varchar=4  date=91
		vo：当 type="out" dataType="cursor" 时，需要设置该值
	        format：当dataType="date" 时，可以设置改值，默认为 yyyy/MM/dd ,倘若前端传入值为 2010-08-22 ，则必须设置format="yyyy-MM-dd"




版本更新说明

-------------------1.0.1-replease--------------------
1、支持注释节点的解析

2、支持配置多个配置文件，比如对应的控制器类中的features属性设置如下:
    p:features="features1.xml features2.xml features3.xml"
3、name、desc、proName、executeClass、excuteMethod、parameters 忽略大小写影响


-------------------1.0.2-replease--------------------
1、消除了在配置文件中的空格影响，比如如下的配置:
   <proName>importCatalog</proName>
   与
   <proName>   importCatalog    </proName>
   上述两种配置是等效的。在1.0.2-replease版本之前后者配置方式无效

2、支持新增 format 属性，自动处理存储过程中日期类型的参数传字符串格式的数据的情况，当前端传一个字符串的日期数据时，设置 format 属性值为前端值格式，默认值为 yyyy/MM/dd
   比如值为“2010-08-22”时，设置format=“yyyy-MM-dd”,而值为“2010/08/22”,可以不用设置 format 属性
   注意:只有 dataType="date" 类型的参数才需要 format 属性

3、支持新增 desc 节点，desc 节点用来对存储过程进行描述

4、配置文件路径问题，以前只能加载类路径下的配置文件，现在可以同时加载类路径和文件系统下的配置文件，而且可以以空格和逗号分隔，如下:
   p:features="features1.xml,F:\MyEclipseWorkSpace\SZMDServer\src\features.xml features3.xml"


-------------------1.1.0-replease--------------------
1、解除对 spring 的依赖
2、新增 datasource 节点