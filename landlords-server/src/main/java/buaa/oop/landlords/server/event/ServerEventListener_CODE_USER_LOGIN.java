package buaa.oop.landlords.server.event;

import buaa.oop.landlords.common.entities.ClientEnd;
import buaa.oop.landlords.common.enums.ClientEventCode;
import buaa.oop.landlords.common.utils.ChannelUtil;
import buaa.oop.landlords.common.utils.MD5Util;
import buaa.oop.landlords.common.utils.MapUtil;
import buaa.oop.landlords.server.ServerContainer;
import buaa.oop.landlords.server.mapper.UserMapper;
import buaa.oop.landlords.server.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.util.Map;

/**
 * 0 成功 1 登录密码错误 2 登陆的时候用户名不存在 3 注册的时候用户名已存在
 * 成功 showOption
 * 失败 nicknameSet
 */
@Slf4j
public class ServerEventListener_CODE_USER_LOGIN extends ServerEventListener{
    @Override
    public void call(ClientEnd clientEnd, String data) {
        Map<String, Object> map = MapUtil.parse(data);
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        // 0 for register, 1 for login
        int op = (int) map.get("operation");
        MapUtil result = MapUtil.newInstance();

        SqlSession session = null;
        try{
            session = ServerContainer.getSession();
            UserMapper userMapper = session.getMapper(UserMapper.class);
            User user = userMapper.selectUserByName(username);

            if(op == 0){
                if(user != null){
                    result.put("code", 3);
                }else{
                    user = new User();
                    user.setName(username);
                    user.setPassword(MD5Util.encrypt(password));
                    userMapper.insertUser(user);
                    session.commit();
                    log.debug("User {} | {} registered", user.getId(), username);
                    setSucceed(result, user, clientEnd);
                }
            }else if(op == 1){
                if(user == null){
                    result.put("code", 2);
                }else if(!user.getPassword().equals(MD5Util.encrypt(password))){
                    result.put("code", 1);
                }else{
                    setSucceed(result, user, clientEnd);
                }
            }
        }catch (Exception e){
            log.error("Failed to login", e);
            result.put("code", 4);
            session.rollback();
        }finally {
            session.close();
        }

        ChannelUtil.pushToClient(
                clientEnd.getChannel(),
                ((int)result.map().get("code")) == 0 ? ClientEventCode.CODE_SHOW_OPTIONS : ClientEventCode.CODE_CLIENT_NICKNAME_SET,
                result.json()
        );
    }

    private static void setSucceed(MapUtil result, User user, ClientEnd clientEnd){
        result.put("code", 0);
        result.put("score", user.getScore());
        result.put("username", user.getName());
        clientEnd.setNickName(user.getName());
        ServerContainer.addClient(clientEnd);
        log.debug("User {} logged in", user.getName());
    }
}
