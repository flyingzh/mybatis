package com.zf.dao;

import com.zf.io.Resource;
import com.zf.pojo.User;
import com.zf.sqlsession.SqlSession;
import com.zf.sqlsession.SqlSessionFactory;
import com.zf.sqlsession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;

import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/24 11:36
 * @description
 */
public class UserDaoImpl implements IUserDao {

    public List<User> findAll() throws PropertyVetoException, DocumentException, IllegalAccessException, ClassNotFoundException, IntrospectionException, InstantiationException, SQLException, InvocationTargetException, NoSuchFieldException {
        InputStream resourceAsStream = Resource.getResourceAsStream("sqlMapperConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = build.openSqlSession();
        List<User> objects = sqlSession.selectList("user.selectList");
        System.out.println(objects);
        return objects;
    }

    public User findByCondition(User user) throws PropertyVetoException, DocumentException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
        InputStream resourceAsStream = Resource.getResourceAsStream("sqlMapperConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = build.openSqlSession();
        User userDto = new User();
        userDto.setId(1);
        userDto.setUsername("zhangsan");
        User o = sqlSession.selectOne("user.selectOne", userDto);
        System.out.println(o);
        return o;
    }

    public Integer updateById(User user) {
        return null;
    }

    public Integer deleteById(User user) {
        return null;
    }

    public Integer insert(User user) {
        return null;
    }


}
