package domain;

import java.io.Serial;
import java.io.Serializable;

public record Gamesave(TheDOPOHardestGame game, int currentLevel, GameMode mode, Skin skin) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

}