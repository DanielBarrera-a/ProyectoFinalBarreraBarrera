package domain;

public class Coin extends Entity {
    private boolean isYellow;

    public Coin(Position position, boolean isYellow) {
        super(new Position(position.getRow(), position.getCol()));
        this.isYellow = isYellow;
    }

    public boolean isYellow() {
        return isYellow;
    }
}
