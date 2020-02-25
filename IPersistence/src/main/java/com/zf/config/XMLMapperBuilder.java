package com.zf.config;

import com.zf.pojo.Configuration;
import com.zf.pojo.MappedStatement;
import com.zf.sqlsession.SqlType;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/23 19:41
 * @description 解析mapper文件
 */
public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration){
        this.configuration = configuration;
    }

    /**
     *  解析mapper文件
     * @param inputStream
     * @throws DocumentException
     */
    public void parseMapperXml(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");
        //解析select
        attrSelectNodes(namespace,rootElement);
        //解析insert
        attrInsertNodes(namespace,rootElement);
        //解析delete
        attrDeleteNodes(namespace,rootElement);
        //解析update
        attrUpdateNodes(namespace,rootElement);
    }

    private void attrUpdateNodes(String namespace, Element rootElement) {
        List<Element> list = rootElement.selectNodes("//update");
        operateNodes(namespace,list, SqlType.UPDATE);
    }

    private void attrDeleteNodes(String namespace, Element rootElement) {
        List<Element> list = rootElement.selectNodes("//delete");
        operateNodes(namespace,list, SqlType.DELETE);
    }

    private void attrInsertNodes(String namespace, Element rootElement) {
        List<Element> list = rootElement.selectNodes("//insert");
        operateNodes(namespace,list, SqlType.INSERT);
    }

    /**
     *  解析select node
     * @param rootElement
     */
    private void attrSelectNodes(String namespace,Element rootElement) {
        List<Element> list = rootElement.selectNodes("//select");
        operateNodes(namespace,list, SqlType.SELECT);
    }

    private void operateNodes(String namespace,List<Element> list,SqlType sqlType){
        for(Element element:list){
            String id = element.attributeValue("id");
            String parameterType = element.attributeValue("parameterType");
            String resultType = element.attributeValue("resultType");
            String sql = element.getText();
            MappedStatement statement = new MappedStatement();
            statement.setId(id);
            statement.setParameterType(parameterType);
            statement.setResultType(resultType);
            statement.setSql(sql);
            statement.setSqlType(sqlType);
            String statmentId = namespace+"."+id;
            configuration.getMappedStatementMap().put(statmentId,statement);
        }
    }

}
