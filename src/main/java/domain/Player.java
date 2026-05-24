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
    private Skin originalSkin;       // skin original del jugador
    private Skin temporarySkin;      // skin temporal por moneda skin (null = sin efecto)

    // Estos son los atributos que va a usar el verde
    private boolean shield;
    private boolean slowedDown;

    public Player(Position position, Skin skin) {
        super(new Position(position.getRow(), position.getCol()));
        this.skin = skin;
        this.originalSkin = skin;
        this.temporarySkin = null;
        this.deaths = 0;
        this.respawnPosition = new Position(position.getRow(), position.getCol());
        this.shield = (skin == Skin.GREEN);
        this.slowedDown = false;
    }

    public int getSpeed() {
        Skin active = (temporarySkin != null) ? temporarySkin : skin;
        if (active == Skin.BLUE) return 2;
        if (active == Skin.GREEN && slowedDown) return 1;
        return 1;
    }

    /** Aplica una skin temporal por moneda skin. */
    public void applySkin(Skin newSkin) {
        this.temporarySkin = newSkin;
        this.shield = (newSkin == Skin.GREEN);
        this.slowedDown = false;
    }

    /** Recupera la skin original (al morir o al recoger otra moneda). */
    public void resetSkin() {
        this.temporarySkin = null;
        this.shield = (originalSkin == Skin.GREEN);
        this.slowedDown = false;
    }

    /** Devuelve la skin activa (temporal si hay, si no la original). */
    public Skin getActiveSkin() {
        return (temporarySkin != null) ? temporarySkin : skin;
    }

    public boolean applyEnemyHit() {
        if (getActiveSkin() == Skin.GREEN && shield) {
            shield = false;
            slowedDown = true;
            return false;
        }
        addDeath();
        resetSkin(); // recupera skin original al morir
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