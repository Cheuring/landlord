package buaa.oop.landlords.common.entities;

import buaa.oop.landlords.common.enums.PokerLevel;
import buaa.oop.landlords.common.enums.PokerType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Poker {
    @Getter
    private PokerLevel level;

    @Getter
    private PokerType type;

    public Poker(PokerLevel level, PokerType type) {
        this.level = level;
        this.type = type;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Poker other = (Poker) obj;
        if (level != other.level)
            return false;
        return type == other.type;
    }

    @Override
    public String toString() {
        return level.getLevel() + " ";
    }
}
