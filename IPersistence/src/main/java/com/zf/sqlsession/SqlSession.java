package com.zf.sqlsession;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/23 19:47
 * @description
 */
public interface SqlSession {

    <E> List<E> selectList(String statmentId, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException;

     <T> T selectOne(String statementId,Object... args) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException;

     <T> T getMapper(Class<?> clazz);

}
