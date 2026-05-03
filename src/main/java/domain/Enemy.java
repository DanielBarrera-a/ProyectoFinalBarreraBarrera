package domain;

public abstract class Enemy extends Entity {
    public Enemy(Position position) {
        super(new Position(position.getRow(), position.getCol()));
    }

    public abstract void move(TheDOPOHardestGame game);
}
