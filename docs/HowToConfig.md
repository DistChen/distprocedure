# 1、如何配置

DistProcedure 提供了一个监听类(dist.common.procedure.define.listener.ProcedureListener)，用于在程序启动时，加载存储过程配置文件(后面的文档会介绍此部分)中所定义的存储过程。一个最基本的配置如下：
```
<context-param>
    <param-name>ProcedureFiles</param-name>
    <param-value>distfeatures.xml</param-value>
</context-param>
<listener>
    <listener-class>dist.common.procedure.define.listener.ProcedureListener</listener-class>
</listener>
```

> 参数说明:
>
> > procedureFiles：用于指定 DistProcedure 需要加载的存储过程配置文件。
> 
> >监听类：在程序启动时，会根据 procedureFiles 的参数去加载相应的配置文件。

如果存储过程文件未在程序根目录下，也可以指定文件的绝对路径，如下所示：
```
<context-param>
    <param-name>ProcedureFiles</param-name>
    <param-value>D:/distfeatures.xml</param-value>
</context-param>
```
如果存储过程很多，可以写在不同的文件中，多个存储过程文件的配置如下所示：
```
<context-param>
    <param-name>ProcedureFiles</param-name>
    <param-value>distfeatures1.xml,distfeatures2.xml,D:/distfeatures3.xml</param-value>
</context-param>
```
默认情况下，DistProcedure关闭了热部署。但是大多数时候，我们希望存储过程修改或者新增后，程序不需要重启。DistProcedure 支持热部署，需要添加如下参数：

```
<context-param>
    <param-name>ProcedureFileAutoupdate</param-name>
    <param-value>True</param-value>
</context-param>
```
开启热部署后，默认的检测时间间隔是一秒，可以自行更改此间隔时间，在web.xml中添加如下参数：

```
<context-param>
    <param-name>ProcedureFileInterval</param-name>
    <param-value>3000</param-value>
</context-param>
```
上面的这些配置就是 DistProcedure 需要配置在 web.xml 中的所有配置项，这里总结下：
> 1. 监听类；必须，用于加载存储过程的配置文件。
> 2. ProcedureFiles；必须，指定存储过程配置文件路径。
> 3. ProcedureFileAutoupdate；可选，决定是否开启热部署，默认是false。
> 4. ProcedureFileInterval；可选，开启热部署之后的检测间隔时间。

一个完整的配置示例如下：

```
<context-param>
    <param-name>ProcedureFiles</param-name>
    <param-value>distfeatures1.xml,distfeatures2.xml</param-value>
</context-param>
<context-param>
    <param-name>ProcedureFileAutoupdate</param-name>
    <param-value>True</param-value>
</context-param>
<context-param>
    <param-name>ProcedureFileInterval</param-name>
    <param-value>3000</param-value>
</context-param>
<listener>
    <listener-class>dist.common.procedure.define.listener.ProcedureListener</listener-class>
</listener>
```