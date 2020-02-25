package com.zf.io;


import com.zf.dao.IUserDao;
import com.zf.pojo.User;
import com.zf.sqlsession.SqlSession;
import com.zf.sqlsession.SqlSessionFactory;
import com.zf.sqlsession.SqlSessionFactoryBuilder;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author admin
 * @date 2020/2/22 19:44
 * @description
 */
public class IPersistenceTest {


    @Test
    public void test() throws PropertyVetoException, DocumentException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, SQLException, InvocationTargetException, ClassNotFoundException {
        InputStream resourceAsStream = Resource.getResourceAsStream("sqlMapperConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = build.openSqlSession();

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
        List<User> all = userDao.findAll();
        System.out.println(all);

    }

    @Test
    public void test2() throws Exception {
        InputStream resourceAsStream = Resource.getResourceAsStream("sqlMapperConfig.xml");
        SqlSessionFactory build = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = build.openSqlSession();

        IUserDao userDao = sqlSession.getMapper(IUserDao.class);
       /*
       //测试更新
       User user1 = new User();
        user1.setId(4);
        user1.setUsername("zhaoliu2");
        Integer integer = userDao.updateById(user1);
        System.out.println(integer);*/
/*
        //测试删除
        User user2 = new User();
        user2.setId(4);
        Integer integer = userDao.deleteById(user2);
        System.out.println(integer);*/
        //测试新增
        User user3 = new User();
        user3.setId(5);
        user3.setUsername("testinsert");
        Integer integer = userDao.insert(user3);
        System.out.println(integer);

    }


}
