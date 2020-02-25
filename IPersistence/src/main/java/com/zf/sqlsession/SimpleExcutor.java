package com.zf.sqlsession;

import com.zf.pojo.BoundSql;
import com.zf.pojo.Configuration;
import com.zf.pojo.MappedStatement;
import com.zf.utils.GenericTokenParser;
import com.zf.utils.ParameterMapping;
import com.zf.utils.ParameterMappingTokenHandler;
import com.zf.utils.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/23 20:05
 * @description
 */
public class SimpleExcutor implements  Excutor {

    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... args) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        String parameterType = mappedStatement.getParameterType();
        String resultType = mappedStatement.getResultType();
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        //设置参数
        setParameters(preparedStatement,parameterMappingList,parameterType,args);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Object> list = new LinkedList<Object>();
        Class<?> resultClazz = getClazz(resultType);
        while (resultSet.next()){
            Object o = resultClazz.newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int count = metaData.getColumnCount();
            for(int i = 1;i<=count;i++){
                String columnName = metaData.getColumnName(i);
                Object value = resultSet.getObject(columnName);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName,resultClazz);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);
            }
            list.add(o);
        }
        preparedStatement.close();
        connection.close();
        return (List<E>) list;
    }

    public Integer execute(Configuration configuration, MappedStatement mappedStatement, Object... args) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        String parameterType = mappedStatement.getParameterType();
        String resultType = mappedStatement.getResultType();
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        //设置参数
        setParameters(preparedStatement,parameterMappingList,parameterType,args);

        //执行insert update delete 操作，返回成功条数
        int update = preparedStatement.executeUpdate();

        //执行sql语句，返回true--失败  false--成功
//        boolean execute = preparedStatement.execute();
        preparedStatement.close();
        connection.close();
        return update;
    }


    /**
     *  设置查询sql 参数
     * @param preparedStatement
     * @param parameterMappingList
     * @param parameterType
     * @param arg
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SQLException
     * @throws IllegalAccessException
     */
    private void setParameters(PreparedStatement preparedStatement, List<ParameterMapping> parameterMappingList, String parameterType, Object... args) throws ClassNotFoundException, NoSuchFieldException, SQLException, IllegalAccessException {
        Class<?> paramClazz = getClazz(parameterType);
        if(paramClazz !=null){
            for(int i = 0;i<parameterMappingList.size();i++){
                ParameterMapping parameterMapping = parameterMappingList.get(i);
                String content = parameterMapping.getContent();

                Field declaredField = paramClazz.getDeclaredField(content);
                declaredField.setAccessible(true);
                Object o = declaredField.get(args[0]);
                preparedStatement.setObject(i+1,o);
            }
        }
    }

    /**
     *  获取class
     * @param classType
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> getClazz(String classType) throws ClassNotFoundException {
        if(StringUtils.isNotEmpty(classType)){
            Class<?> aClass = Class.forName(classType);
            return aClass;
        }
        return null;
    }

    /**
     *  解析sql
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{","}",parameterMappingTokenHandler);
        String sqlTest = genericTokenParser.parse(sql);
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql();
        boundSql.setSqlText(sqlTest);
        boundSql.setParameterMappingList(parameterMappings);
        return boundSql;
    }

}
