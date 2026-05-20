package domain;

import java.io.Serial;
import java.io.Serializable;

public class Gamesave implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private TheDOPOHardestGame game;
    private int currentLevel;
    private GameMode mode;
    private Skin skin;

    public Gamesave(TheDOPOHardestGame game, int currentLevel, GameMode mode, Skin skin) {
        this.game = game;
        this.currentLevel = currentLevel;
        this.mode = mode;
        this.skin = skin;
    }

    public TheDOPOHardestGame game() {
        return game;
    }

    public int currentLevel() {
        return currentLevel;
    }

    public GameMode mode() {
        return mode;
    }

    public Skin skin() {
        return skin;
    }
}