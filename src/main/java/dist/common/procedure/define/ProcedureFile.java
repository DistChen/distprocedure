package dist.common.procedure.define;
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
    private static Map<String, Integer> sqlTypes = new HashMap<String, Integer>();
    /**
     * 初始化SQL数据类型字典
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private static void initSQLType() throws IllegalArgumentException, IllegalAccessException {
        ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(Types.class.getFields()));
        fields.addAll(Arrays.asList(oracle.jdbc.OracleTypes.class.getFields()));
        for (Field field : fields) {
            if (field.getType() == int.class) {
                sqlTypes.put(field.getName(), (Integer) field.get(null));
            }
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
                } else if (attrs.getNamedItem("type").getNodeValue().trim().equalsIgnoreCase("out")) {
                    outParas = new ProcedureOutPrameter(attrs.getNamedItem("name").getNodeValue().trim(), getSQLType(attrs.getNamedItem("dataType").getNodeValue().trim()));
                    if (attrs.getNamedItem("vo") != null) {
                        outParas.setVoClass(Class.forName(attrs.getNamedItem("vo").getNodeValue().trim()).newInstance().getClass());
                    }
                    paras.add(outParas);
                }
            }
        }
        procedureModel.setProcedureParameters(paras);
    }

    /**
     * init the datasource
     * @param document
     */
    private static void initDatasource(Document document){
        if(document.getElementsByTagName("datasource")!=null){
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
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            DBConnector.conn(driver,url,username,password);
        }
    }

    public static Map<String,ProcedureModel> loadFile(String[]resources){
        if (resources.length>0){
            try {
                initSQLType();
                Map<String, ProcedureModel> features = new HashMap<String, ProcedureModel>();
                for(String fileName:resources){
                    if (!fileName.isEmpty()){
                        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                        docFactory.setValidating(false);
                        Document document;
                        File file;
                        if (fileName.trim().matches("^[a-zA-Z]:.*xml$")){
                            file=new File(fileName.trim());
                            document = docFactory.newDocumentBuilder().parse(file);
                        }else{

                            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName.trim());
                            document = docFactory.newDocumentBuilder().parse(is);
                        }
                        initDatasource(document);
                        NodeList featureList = document.getElementsByTagName("procedure");
                        NodeList featureInfos;
                        ProcedureModel procedureModel;
                        Node featureInfo;
                        String id;
                        for (int i = 0; i < featureList.getLength(); i++) {
                            procedureModel = new ProcedureModel();
                            id=featureList.item(i).getAttributes().getNamedItem("id").getNodeValue().trim();
                            features.put(id,procedureModel);
                            featureInfos = featureList.item(i).getChildNodes();
                            for (int j = 0; j < featureInfos.getLength(); j++) {
                                featureInfo = featureInfos.item(j);
                                if (featureInfo.getNodeType() == 1) {
                                    setParamValue(featureInfo, procedureModel);
                                }
                            }
                        }
                    }
                }
                return features;
            }catch (Exception e){
                e.printStackTrace();
               // System.out.println("配置文件有误，配置必须遵循 distprocedure.xsd 规范");
                return null;
            }
        }
        return null;
    }
}
