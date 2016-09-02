# 5、调用方式

调用的方式很简单。当 DistProcedure 加载完所有的 存储过程配置文件后，会将所有的配置信息存到 dist.common.procedure.define.ProcedureRepository 类的静态属性 procedures 中，该属性声明如下：

```
private static Map<String,ProcedureModel> procedures;
```
此属性的初始化由 DistProcedure 完成。加载完成后，通过 ProcedureRepository 提供的如下方法去获取指定的存储过程模型类dist.common.procedure.define.ProcedureModel，此类的实例是调用存储过程时的必需参数：

```
 public static ProcedureModel getProcedure(String id)
```

调用存储过程就一个方法，dist.common.procedure.define.ProcedureCaller 类的 call 方法，其定义如下：

```
/**
 * 调用存储过程
 *
 * @param procedureModel 某个id对应的存储过程model
 * @param values         参数数组，顺序与存储过程保持一致
 * @return Map<String,Object> 
 */
public static Object call(ProcedureModel procedureModel,Object...values)
```

可以看到调用很简单，而且所有的ProcedureModel的实例化也是DistProcedure自动完成的，通过id就能到ProcedureRepository中取到，并不需要手动构造。返回结果是一个 Map 类型的数据，其中的成员就是对应存储过程配置部分中 type 为 out 的 parameter。

下面是我的一个测试用例：

```
ProcedureRepository.setProcedures(ProcedureFile.loadFile(new String[]{"distfeatures.xml"}));
ProcedureModel model = ProcedureRepository.getProcedure("testPro");
Map<String,Object> obj = (Map<String,Object>)ProcedureCaller.call(model,
                                                     "dataType配置为varchar",
                                                      100,
                                                      new java.sql.Date(new Date().getTime()),
                                                      new Date());

Object result=obj.get("p_cursor");
((List<Person>)result).stream().forEach(person->System.out.println(person.getName()));
```

这里用到了另一种加载存储过程配置文件的方式。如果是非web程序，可以通过下面的加载方式去加载存储过程配置文件：

```
ProcedureRepository.setProcedures(ProcedureFile.loadFile(new String[]{"distfeatures.xml"}));
```