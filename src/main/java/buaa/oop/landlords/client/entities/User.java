package buaa.oop.landlords.client.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    public static User INSTANCE = new User();

    private String nickname;
    private int id;

    private User() {
        this.nickname = "Player";
        this.id = -1;
    }

}
