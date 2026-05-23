package domain;

/**
 * Esta clase representa al jugador del juego.
 * Hereda de Entity.
 */
public class Player extends Entity {
    private static final long serialVersionUID = 1L;

    private int deaths;
    private Position respawnPosition;
    private Skin skin;

    // Estos son los atributos que va a usar el verde
    private boolean shield;
    private boolean slowedDown;

    public Player(Position position, Skin skin) {
        super(new Position(position.getRow(), position.getCol()));
        this.skin = skin;
        this.deaths = 0;
        this.respawnPosition = new Position(position.getRow(), position.getCol());
        this.shield = (skin == Skin.GREEN);
        this.slowedDown = false;
    }

    public int getSpeed() {
        if (skin == Skin.BLUE) return 2;
        return 1;
    }

    public boolean applyEnemyHit() {
        if (skin == Skin.GREEN && shield) {
            shield = false;
            slowedDown = true;
            return false;
        }
        addDeath();
        return true;
    }

    public void resetShield() {
        if (skin == Skin.GREEN) {
            shield = true;
            slowedDown = false;
        }
    }

    public boolean isShielded() {
        return shield;
    }

    public boolean isSlowedDown() {
        return slowedDown;
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
