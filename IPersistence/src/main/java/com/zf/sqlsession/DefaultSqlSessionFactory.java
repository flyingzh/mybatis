package com.zf.sqlsession;

import com.zf.pojo.Configuration;

/**
 * @author admin
 * @date 2020/2/23 19:50
 * @description
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public SqlSession openSqlSession() {
        return new DefaultSqlSession(configuration);
    }
}
