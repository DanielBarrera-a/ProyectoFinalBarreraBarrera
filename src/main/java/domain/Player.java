package domain;

public class Player extends Entity {
    private Skin skin;
    private int deaths;
    private Position respawnPosition;

    public Player(Position position, Skin skin) {
        super(new Position(position.getRow(), position.getCol()));
        this.skin = skin;
        this.deaths = 0;
        this.respawnPosition = new Position(position.getRow(), position.getCol());
    }

    public int getDeaths() {
        return deaths; }

    public void addDeath() {
        deaths++; }

    public Skin getSkin() {
        return skin; }
    
    public Position getRespawnPosition() {
        return respawnPosition; }

    public void setRespawnPosition(Position pos) {
        this.respawnPosition = new Position(pos.getRow(), pos.getCol());
    }
}
