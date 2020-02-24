package com.zf.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zf.io.Resource;
import com.zf.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author admin
 * @date 2020/2/22 19:55
 * @description
 */
public class XMLCofingBuilder {

    private Configuration configuration;

    public XMLCofingBuilder() {
        this.configuration = new Configuration();
    }

    /**
     *  解析核心数据源配置文件
     * @param inputStream
     * @throws DocumentException
     * @throws PropertyVetoException
     */
    public Configuration parseXml(InputStream inputStream) throws DocumentException, PropertyVetoException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> list = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        for(Element element:list){
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name,value);
        }
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("url"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(comboPooledDataSource);

        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for(Element mapper:mapperList){
            String path = mapper.attributeValue("value");
            InputStream resourceAsStream = Resource.getResourceAsStream(path);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parseMapperXml(resourceAsStream);
        }
        return configuration;
    }



}
