-----------------------------------
![github](http://3.su.bdimg.com/icon/9898.png "")[我的新浪微博](http://weibo.com/ooqingkuangoo/)
![github](http://4.su.bdimg.com/icon/7083.png "")[我的QQ空间](http://user.qzone.qq.com/365061362/main/)
![github](http://3.su.bdimg.com/icon/2602.png?1 "")[我的腾讯微博](http://t.qq.com/cyp365061362/)

    【Instroduce】  
    整体功能说明:用于后台调用存储过程的配置式实现，从业务代码中脱离出来。
    1、根节点为procedures
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
       format：当dataType="date" 时，可以设置改值，默认为 yyyy/MM/dd ,倘若前端传入值为 2010-08-22，则必须设置format="yyyy-MM-dd"
    5、datasource 节点用于配置数据源

