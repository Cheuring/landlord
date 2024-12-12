package buaa.oop.landlords.client.entities;

import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

@Data
public class User {
    public static User INSTANCE = new User();

    private int score;
    private String nickname ;
    private int id;

    private User() {
        this.nickname = "Player";
        this.id = -1;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {this.nickname = nickname;}
    public int getId() {
        return id;
    }
    public int getScore(){
        return score;
    }
    public static User getINSTANCE() {
        return INSTANCE;
    }
}
