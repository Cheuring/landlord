package buaa.oop.landlords.test;

import buaa.oop.landlords.common.utils.MD5Util;
import buaa.oop.landlords.server.mapper.UserMapper;
import buaa.oop.landlords.server.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MybatisTest {
    private SqlSession session;
    private UserMapper userMapper;

    @BeforeEach
    public void init() throws IOException {
//        String resource = "mybatis-config.xml";
//        InputStream inputStream = Resources.getResourceAsStream(resource);
        InputStream inputStream = MybatisTest.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
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
    @Order(1)
    public void testSelectAll() {
        List<User> users = userMapper.selectAll();
        Assertions.assertNotNull(users, "Users should not be null");
    }

    @Test
    @Order(2)
    public void testInsertUser() {
        User user = new User();
        user.setName("oop");
        user.setPassword(MD5Util.encrypt("123456"));

        int result = userMapper.insertUser(user);
        Assertions.assertEquals(1, result, "User should be inserted");
    }

    @Test
    @Order(3)
    public void testUpdateUser() {
        User user = new User();
        user.setName("TT");
        user.setScore(150);

        int result = userMapper.updateUser(user);
        Assertions.assertEquals(1, result, "User should be updated");
    }

    @Test
    @Order(4)
    public void testSelectByName(){
        User notExit = userMapper.selectUserByName("tt");
        Assertions.assertNull(notExit, "User should not exist");

        User user = userMapper.selectUserByName("TT");
        Assertions.assertNotNull(user, "User should exist");
        Assertions.assertEquals("TT", user.getName(), "User name should be TT");
    }

    @Test
    @Order(5)
    public void testDeleteUser() {
        User user = new User();
        user.setName("tt");
        int result = userMapper.deleteUser(user);
        Assertions.assertEquals(0, result, "User should not exist");

        user.setName("TT");
        result = userMapper.deleteUser(user);
        Assertions.assertEquals(1, result, "User should be deleted");
    }
}
