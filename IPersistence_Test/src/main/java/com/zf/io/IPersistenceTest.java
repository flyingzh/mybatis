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

        User users = new User();
        users.setId(2);
        users.setUsername("lisi");
        User byCondition = userDao.findByCondition(users);
        System.out.println(byCondition);

    }

}
