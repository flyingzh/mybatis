package com.zf.pojo;

import com.zf.sqlsession.SqlType;

/**
 * @author admin
 * @date 2020/2/22 19:47
 * @description mapper文件描述对象
 */
public class MappedStatement {

    private String id;
    private String parameterType;
    private String resultType;
    private String sql;
    private SqlType sqlType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }
}
