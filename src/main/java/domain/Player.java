package domain;

/**
 * Esta clse representa al jugador del juego
 * Hereda de Entity
 */
public class Player extends Entity {
    private static final long serialVersionUID = 1L;

    private int deaths;
    private Position respawnPosition;
    private Skin skin;

    public Player(Position position, Skin skin) {
        super(new Position(position.getRow(), position.getCol()));
        this.skin = skin;
        this.deaths = 0;
        this.respawnPosition = new Position(position.getRow(), position.getCol());
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
    }

    public void reduceDeath() {
        if (deaths > 0) deaths--;
    }

    public Skin getSkin() {
        return skin;
    }

    public Position getRespawnPosition() {
        return respawnPosition;
    }

    public void setRespawnPosition(Position pos) {
        this.respawnPosition = new Position(pos.getRow(), pos.getCol());
    }
}

