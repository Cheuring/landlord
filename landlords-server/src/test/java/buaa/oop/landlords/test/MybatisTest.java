package buaa.oop.landlords.test;

import buaa.oop.landlords.common.utils.MD5Util;
import buaa.oop.landlords.server.mapper.UserMapper;
import buaa.oop.landlords.server.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {
    private SqlSessionFactory sessionFactory;
    private SqlSession session;

    @BeforeEach
    public void init() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        session = sessionFactory.openSession();
    }
    @AfterEach
    public void close() {
        session.commit();
        session.close();
    }

    @Test
    public void testSelectAll() {
        UserMapper userMapper = session.getMapper(UserMapper.class);

        List<User> users = userMapper.selectAll();
        System.out.println(users);
    }

    @Test
    public void testInsertUser() {
        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user = new User();
        user.setName("Test");
        user.setPassword(MD5Util.encrypt("123456"));

        int result = userMapper.insertUser(user);
        System.out.println("result = " + result);
    }

    @Test
    public void testUpdateUser() {
        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user = new User();
        user.setName("Astaky");
        user.setScore(150);

        int result = userMapper.updateUser(user);
        System.out.println("result = " + result);
    }

    @Test
    public void testDeleteUser() {
        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user = new User();
        user.setName("Test");

        int result = userMapper.deleteUser(user);
        System.out.println("result = " + result);
    }

}
