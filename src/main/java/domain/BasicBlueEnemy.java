package domain;

public class BasicBlueEnemy extends Enemy {
    private boolean isHorizontal;
    private int direction = 1; // 1 for right/down, -1 for left/up

    public BasicBlueEnemy(Position position, boolean isHorizontal) {
        super(position);
        this.isHorizontal = isHorizontal;
    }

    @Override
    public void move(TheDOPOHardestGame game) {
        int nextRow = position.getRow() + (isHorizontal ? 0 : direction);
        int nextCol = position.getCol() + (isHorizontal ? direction : 0);

        if (game.isValidPosition(nextRow, nextCol) && game.getCell(nextRow, nextCol) != CellType.WALL) {
            position.setRow(nextRow);
            position.setCol(nextCol);
        } else {
            direction *= -1; // Bounce
            nextRow = position.getRow() + (isHorizontal ? 0 : direction);
            nextCol = position.getCol() + (isHorizontal ? direction : 0);
            if (game.isValidPosition(nextRow, nextCol) && game.getCell(nextRow, nextCol) != CellType.WALL) {
                position.setRow(nextRow);
                position.setCol(nextCol);
            }
        }
    }
}
