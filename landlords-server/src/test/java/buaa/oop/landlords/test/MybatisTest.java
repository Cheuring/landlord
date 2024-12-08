package buaa.oop.landlords.test;

import buaa.oop.landlords.common.utils.MD5Util;
import buaa.oop.landlords.server.mapper.UserMapper;
import buaa.oop.landlords.server.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {
    private SqlSession session;
    private UserMapper userMapper;

    @BeforeEach
    public void init() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        session = new SqlSessionFactoryBuilder()
                .build(inputStream)
                .openSession();
        userMapper = session.getMapper(UserMapper.class);
    }
    @AfterEach
    public void close() {
        session.commit();
        session.close();
    }

    @Test
    public void testSelectAll() {
        List<User> users = userMapper.selectAll();
        System.out.println(users);

        Assertions.assertNotNull(users, "Users should not be null");
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setName("TT");
        user.setPassword(MD5Util.encrypt("123456"));

        int result = userMapper.insertUser(user);
        Assertions.assertEquals(1, result, "User should be inserted");
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setName("TT");
        user.setScore(150);

        int result = userMapper.updateUser(user);
        Assertions.assertEquals(1, result, "User should be updated");
    }

    @Test
    public void testDeleteUser() {
        User user = new User();
        user.setName("TT");

        int result = userMapper.deleteUser(user);
        Assertions.assertEquals(1, result, "User should be deleted");
    }

    @Test
    public void testSelectByName(){
        User notExit = userMapper.selectUserByName("NO");
        Assertions.assertNull(notExit, "User should not exist");

        User user = userMapper.selectUserByName("TT");
        Assertions.assertNotNull(user, "User should exist");
        Assertions.assertEquals("TT", user.getName(), "User name should be TT");
    }

}
