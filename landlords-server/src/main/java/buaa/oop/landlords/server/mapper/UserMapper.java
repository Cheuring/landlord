package buaa.oop.landlords.server.mapper;

import buaa.oop.landlords.server.pojo.User;

import java.util.List;

public interface UserMapper {
    List<User> selectAll();

    int insertUser(User user);

    int updateUser(User user);

    int deleteUser(User user);

    User selectUserByName(String name);

    int updateUserScore(int id, int score);
}
