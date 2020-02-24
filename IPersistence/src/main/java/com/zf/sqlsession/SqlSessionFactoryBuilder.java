package com.zf.sqlsession;

import com.zf.config.XMLCofingBuilder;
import com.zf.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @author admin
 * @date 2020/2/22 19:52
 * @description
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) throws PropertyVetoException, DocumentException {
        XMLCofingBuilder xmlCofingBuilder = new XMLCofingBuilder();
        Configuration configuration = xmlCofingBuilder.parseXml(inputStream);
        SqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return defaultSqlSessionFactory;
    }

}
