package buaa.oop.landlords.client.entities;

import lombok.Data;

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
}
