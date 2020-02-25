package com.zf.sqlsession;

import com.zf.pojo.Configuration;
import com.zf.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/23 20:04
 * @description
 */
public interface Excutor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement,Object...args) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException;

    Integer execute(Configuration configuration, MappedStatement mappedStatement,Object...args) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException;

}
