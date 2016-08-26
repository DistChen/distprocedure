package dist.common.procedure.define;
import dist.common.rules.define.EnumFilterType;
import dist.common.rules.define.RuleInfo;
import org.apache.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.Types;
import java.util.*;

/**
 * Created by dist on 14-12-30.
 */
public class ProcedureFile {

    private static Logger log=Logger.getLogger(ProcedureFile.class);

    private static Map<String, Integer> sqlTypes = new HashMap<String, Integer>();
    /**
     * 初始化SQL数据类型字典
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
     static{
        try{
            ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(Types.class.getFields()));
            fields.addAll(Arrays.asList(oracle.jdbc.OracleTypes.class.getFields()));
            for (Field field : fields) {
                if (field.getType() == int.class) {
                    sqlTypes.put(field.getName(), (Integer) field.get(null));
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据配置的SQL数据类型返回对应的值
     *
     * @param dataType 可以传入真实值或者对应名称，忽略大小写差异
     * @return
     */
    private static Integer getSQLType(String dataType) {
        if (dataType.matches("^-?\\d+$")) {
            return Integer.valueOf(dataType);
        } else {
            Integer sqlType = sqlTypes.get(dataType.toUpperCase());
            if (sqlType != null) {
                return sqlType;
            } else {
                log.error("参数设置有误,未找到类型为[" + dataType + "]的参数，请检查并修改配置项！");
                throw new IllegalArgumentException("参数设置有误,未找到类型为【" + dataType + "】的参数，请检查并修改配置项！");
            }
        }
    }

    /**
     * 将配置的 element 转换为对应的存储过程模型
     *
     * @param node
     * @param procedureModel
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws org.w3c.dom.DOMException
     */
    private static void setParamValue(Node node, ProcedureModel procedureModel) throws InstantiationException, IllegalAccessException, ClassNotFoundException, DOMException {
        if (node.getNodeName().equalsIgnoreCase("proName")) {
            procedureModel.setProcedureName(node.getTextContent().trim());
        } else if (node.getNodeName().equalsIgnoreCase("executeClass")) {
            if (!node.getTextContent().trim().equalsIgnoreCase("default")) {
                procedureModel.setExecuteClass(node.getTextContent().trim());
            }
        } else if (node.getNodeName().equalsIgnoreCase("executeMethod")) {
            if (!node.getTextContent().trim().equalsIgnoreCase("default")) {
                procedureModel.setExecuteMethod(node.getTextContent().trim());
            }
        } else if (node.getNodeName().equalsIgnoreCase("parameters")) {
            paramatersTransfer(node, procedureModel);
        } else if (node.getNodeName().equalsIgnoreCase("desc")){
            procedureModel.setDesc(node.getTextContent().trim());
        }
    }

    /**
     * 转换并设置存储过程对应的参数信息
     *
     * @param node
     * @param procedureModel
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private static void paramatersTransfer(Node node, ProcedureModel procedureModel) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        NodeList nodeList = node.getChildNodes();
        NamedNodeMap attrs;
        List<ProcedureParameter> paras = new ArrayList<ProcedureParameter>();
        ProcedureInParameter inParas;
        ProcedureOutPrameter outParas;
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == 1) {
                attrs = nodeList.item(i).getAttributes();
                if (attrs.getNamedItem("type").getNodeValue().trim().equalsIgnoreCase("in")) {
                    inParas = new ProcedureInParameter(attrs.getNamedItem("name").getNodeValue().trim(), getSQLType(attrs.getNamedItem("dataType").getNodeValue().trim()));
                    if (attrs.getNamedItem("format")!=null){
                        inParas.setDateFormat(attrs.getNamedItem("format").getNodeValue().trim());
                    }
                    paras.add(inParas);
                    log.debug("解析[" + inParas.getParameterName() + "]参数.");
                } else if (attrs.getNamedItem("type").getNodeValue().trim().equalsIgnoreCase("out")) {
                    outParas = new ProcedureOutPrameter(attrs.getNamedItem("name").getNodeValue().trim(), getSQLType(attrs.getNamedItem("dataType").getNodeValue().trim()));
                    if (attrs.getNamedItem("vo") != null) {
                        outParas.setVoClass(Class.forName(attrs.getNamedItem("vo").getNodeValue().trim()).newInstance().getClass());
                    }
                    log.debug("解析["+outParas.getParameterName()+"]参数.");
                    paramRuleHandler(outParas,nodeList.item(i));
                    paras.add(outParas);
                }
            }
        }
        procedureModel.setProcedureParameters(paras);
    }


    private static void paramRuleHandler(ProcedureOutPrameter param,Node node){
        NodeList childs=node.getChildNodes();
        Node rule;
        NamedNodeMap attrs;
        for (int i=0;i<childs.getLength();i++){
            rule=childs.item(i);
            if (rule.getNodeType()==1){
                attrs = rule.getAttributes();
                RuleInfo ruleInfo=new RuleInfo();
                log.debug("解析参数["+param.getParameterName()+"]的规则");
                if (attrs.getNamedItem("ruleFile")==null){
                    log.debug("参数[" + param.getParameterName() + "]设置的规则未指定规则文件，规则未生效");
                }else{
                    ruleInfo.setRuleFilePath(attrs.getNamedItem("ruleFile").getNodeValue().trim());
                    if (attrs.getNamedItem("group")!=null){
                        ruleInfo.setRuleGroup(attrs.getNamedItem("group").getNodeValue().trim());
                    }
                    if (attrs.getNamedItem("filterKey")!=null){
                        ruleInfo.setFilterKey(attrs.getNamedItem("filterKey").getNodeValue().trim());
                    }
                    if (attrs.getNamedItem("filterType")!=null){
                        EnumFilterType type=EnumFilterType.valueOf(attrs.getNamedItem("filterType").getNodeValue().trim().toUpperCase(Locale.ENGLISH));
                        ruleInfo.setFilterType(type);
                    }
                    param.setRuleInfo(ruleInfo);
                }
            }
        }
    }

    /**
     * init the datasource
     * @param document
     */
    private static void initDatasource(Document document){
        if(document.getElementsByTagName("datasource").getLength()>0){
            log.debug("解析数据源........");
            Node node = document.getElementsByTagName("datasource").item(0);
            String driver="",url="",username="",password="";
            for (int i = 0 ;i<node.getChildNodes().getLength();i++){
                Node childNode=node.getChildNodes().item(i);
                if (childNode.getNodeName().equalsIgnoreCase("driver")){
                    driver=childNode.getTextContent().trim();
                }else if(childNode.getNodeName().equalsIgnoreCase("url")){
                    url=childNode.getTextContent().trim();
                }else if(childNode.getNodeName().equalsIgnoreCase("username")){
                    username=childNode.getTextContent().trim();
                } else if(childNode.getNodeName().equalsIgnoreCase("password")){
                    password=childNode.getTextContent().trim();
                }
            }
            if(driver.startsWith("${")){
                try {
                    log.debug("解析占位符........");
                    String fileName=node.getAttributes().getNamedItem("src").getNodeValue().trim();
                    Properties pros=new Properties();
                    if (fileName.matches("^[a-zA-Z]:.*properties$")){
                        FileInputStream fis = new FileInputStream(fileName);
                        pros.load(fis);
                    }else{
                        InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                        pros.load(is);
                    }
                    driver=pros.getProperty(driver.substring(2, driver.length() - 1));
                    url=pros.getProperty(url.substring(2,url.length()-1));
                    username=pros.getProperty(username.substring(2,username.length()-1));
                    password=pros.getProperty(password.substring(2,password.length()-1));
                } catch (FileNotFoundException e) {
                    log.error("未找到数据源属性配置文件"+e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    log.error("文件打开异常:"+e.getMessage());
                    e.printStackTrace();
                }
            }
            DBConnector.conn(driver,url,username,password);
        }
    }

    public static Map<String,ProcedureModel> loadFile(String[]resources){
        Map<String, ProcedureModel> features = new HashMap<String, ProcedureModel>();
        for (String fileName:resources){
            if (!fileName.isEmpty()){
                log.debug("---------加载配置文件["+fileName+"]---------");
                features.putAll(loadFile(fileName));
                log.debug("---------文件["+fileName+"]加载完毕---------");
            }
        }
        return features;
    }

    public static Map<String,ProcedureModel> loadFile(String resource){
        try {
            Map<String, ProcedureModel> features = new HashMap<String, ProcedureModel>();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setValidating(false);
            Document document;
            File file;
            if (resource.trim().matches("^.*[a-zA-Z]:.*xml$")){
                file=new File(resource.trim());
                document = docFactory.newDocumentBuilder().parse(file);
            }else{
                InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(resource.trim());
                document = docFactory.newDocumentBuilder().parse(is);
            }
            initDatasource(document);
            NodeList featureList = document.getElementsByTagName("procedure");
            if (featureList.getLength()==0){
                log.debug("文件["+resource+"]中未配置存储过程模型");
                return null;
            }else {
                NodeList featureInfos;
                ProcedureModel procedureModel;
                Node featureInfo;
                String id;
                for (int i = 0; i < featureList.getLength(); i++) {
                    procedureModel = new ProcedureModel();
                    id=featureList.item(i).getAttributes().getNamedItem("id").getNodeValue().trim();
                    log.debug("解析[id="+id+"]的存储过程");
                    features.put(id, procedureModel);
                    featureInfos = featureList.item(i).getChildNodes();
                    for (int j = 0; j < featureInfos.getLength(); j++) {
                        featureInfo = featureInfos.item(j);
                        if (featureInfo.getNodeType() == 1) {
                            setParamValue(featureInfo, procedureModel);
                        }
                    }
                }
                return features;
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
