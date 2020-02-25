package com.zf.sqlsession;

import com.zf.pojo.Configuration;
import com.zf.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author admin
 * @date 2020/2/23 19:47
 * @description
 */
public class DefaultSqlSession implements SqlSession{

    private Configuration configuration;
    private Excutor simpleExcutor;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
        simpleExcutor = new SimpleExcutor();
    }

    public <E> List<E> selectList(String statmentId, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
//        Excutor simpleExcutor = new SimpleExcutor();
        MappedStatement statement = configuration.getMappedStatementMap().get(statmentId);
        List<Object> query = simpleExcutor.query(configuration, statement, args);
        return (List<E>) query;
    }

    public <T> T selectOne(String statementId, Object... args) throws IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementId, args);
        if(objects!=null && objects.size() == 1){
            return (T) objects.get(0);
        }else if(objects!=null && objects.size() >1){
            throw new RuntimeException("查询多条结果集异常");
        }
        return null;
    }

    public <T> T getMapper(Class<?> clazz) {
        Object instance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className+"."+methodName;

                Type genericReturnType = method.getGenericReturnType();
                if(genericReturnType instanceof ParameterizedType){
                    //若返回参数为泛型类型
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }
                return executeType(statementId,args);
//                return selectOne(statementId,args);
            }
        });
        return (T) instance;
    }

    private <T> T executeType(String statmentId, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
        Map<String, MappedStatement> mappedStatementMap = configuration.getMappedStatementMap();
        MappedStatement statement = mappedStatementMap.get(statmentId);
        Object o = null;
        switch (statement.getSqlType()){
            case DELETE:
                o = execute(configuration,statement,args);
                break;
            case INSERT:
                o = execute(configuration,statement,args);
                break;
            case UPDATE:
                o = execute(configuration,statement,args);
                break;
            case SELECT:
                o = selectOne(statmentId, args);
                break;
        }
        return (T) o;
    }

    private Integer execute(Configuration configuration,MappedStatement statement, Object... args) throws IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
        return simpleExcutor.execute(configuration,statement,args);
    }

}
