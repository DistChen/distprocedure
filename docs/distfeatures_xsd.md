# 参考：存储过程配置规范文档


[demo](./distfeature.xsd)
```
<?xml version="1.0"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
           targetNamespace="http://www.dist.com.cn"
           xmlns="http://www.dist.com.cn"
           xmlns:dist="http://www.dist.com.cn"
           elementFormDefault="qualified">
    <xs:element name="procedures">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="datasource" minOccurs="0" maxOccurs="1">
                    <xs:complexType>
                        <xs:all>
                            <xs:element name="driver" type="DistString" />
                            <xs:element name="url" type="DistString" />
                            <xs:element name="username" type="DistString" />
                            <xs:element name="password" type="DistString" />
                        </xs:all>
                        <xs:attribute name="src" type="DistString" use="optional" />
                    </xs:complexType>
                </xs:element>
                <xs:element name="procedure" type="procedure" minOccurs="0" maxOccurs="unbounded"/>
            </xs:sequence>
        </xs:complexType>
        <xs:unique name="UNIQUEID">
            <xs:selector xpath="dist:procedure" />
            <xs:field xpath="@id" />
        </xs:unique>
    </xs:element>
    <xs:complexType name="procedure">
        <xs:all>
            <xs:element name="desc" type="DistString" minOccurs="0"/>
            <xs:element name="proName" type="DistString"/>
            <xs:element name="executeClass" type="DistString" minOccurs="0"/>
            <xs:element name="executeMethod" type="DistString" minOccurs="0"/>
            <xs:element name="parameters" type="parameters"/>
        </xs:all>
        <xs:attribute name="id" use="required" type="DistString" />
    </xs:complexType>
    <xs:complexType name="parameters">
        <xs:sequence>
            <xs:element name="parameter" type="parameter" maxOccurs="unbounded" minOccurs="1"/>
        </xs:sequence>
    </xs:complexType>
    <xs:complexType name="parameter">
        <xs:sequence>
           <xs:element name="rule" type="RuleType" minOccurs="0" maxOccurs="1"/>
        </xs:sequence>
        <xs:attribute name="name" type="DistString" use="required"/>
        <xs:attribute name="type" type="InOutType" use="required"/>
        <xs:attribute name="dataType" type="DataType" use="required" />
        <xs:attribute name="format" type="DistString" use="optional" />
        <xs:attribute name="vo" type="DistString" use="optional" />
    </xs:complexType>
    <xs:complexType name="RuleType">
        <xs:attribute name="ruleFile" type="DistString" use="required" />
        <xs:attribute name="group" type="DistString" use="optional" />
        <xs:attribute name="filterKey" type="DistString" use="optional" />
        <xs:attribute name="filterType" type="FilterType" use="optional" />
    </xs:complexType>
    <xs:simpleType name="DistString">
        <xs:restriction base="xs:string">
            <xs:whiteSpace value="replace" />
        </xs:restriction>
    </xs:simpleType>
    <xs:simpleType name="InOutType">
        <xs:restriction base="DistString">
            <xs:enumeration value="in" />
            <xs:enumeration value="out" />
        </xs:restriction>
    </xs:simpleType>
    <xs:simpleType name="DataType">
        <xs:restriction base="DistString">
            <xs:enumeration value="varchar" />
            <xs:enumeration value="number" />
            <xs:enumeration value="date" />
            <xs:enumeration value="cursor" />
            <xs:enumeration value="clob" />
            <xs:enumeration value="blob" />
            <xs:enumeration value="integer" />
        </xs:restriction>
    </xs:simpleType>
    <xs:simpleType name="FilterType">
        <xs:restriction base="DistString">
            <xs:enumeration value="START" />
            <xs:enumeration value="END" />
            <xs:enumeration value="CONTAIN" />
            <xs:enumeration value="EQUAL" />
            <xs:enumeration value="EQUALIGNORECASE" />
            <xs:enumeration value="REGEX" />
        </xs:restriction>
    </xs:simpleType>
</xs:schema>
```