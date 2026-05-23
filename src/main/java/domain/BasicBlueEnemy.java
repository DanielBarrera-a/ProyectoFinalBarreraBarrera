package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Enemigo básico que se mueve horizontal o verticalmente y rebota en las paredes.
 * Responde tanto a BASIC_BLUE  archivo de nivel txt
 *
 */
public class BasicBlueEnemy extends Enemy {
    private static final long serialVersionUID = 1L;

    private boolean isHorizontal;
    private int direction = 1; // 1 = derecha/abajo, -1 = izquierda/arriba
    private Color color;

    public BasicBlueEnemy(Position position, boolean isHorizontal, Color color) {
        super(position);
        this.isHorizontal = isHorizontal;
        this.color = color;
    }

    @Override
    public void move(TheDOPOHardestGame game) {
        int nextRow = position.getRow() + (isHorizontal ? 0 : direction);
        int nextCol = position.getCol() + (isHorizontal ? direction : 0);

        if (game.isValidPosition(nextRow, nextCol) && game.getCell(nextRow, nextCol) != CellType.WALL) {
            position.setRow(nextRow);
            position.setCol(nextCol);
        } else {
            direction *= -1; // Rebotar
            nextRow = position.getRow() + (isHorizontal ? 0 : direction);
            nextCol = position.getCol() + (isHorizontal ? direction : 0);
            if (game.isValidPosition(nextRow, nextCol) && game.getCell(nextRow, nextCol) != CellType.WALL) {
                position.setRow(nextRow);
                position.setCol(nextCol);
            }
        }
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        g.setColor(color);
        g.fillOval(
                offsetX + position.getCol() * cellSize + 5,
                offsetY + position.getRow() * cellSize + 5,
                30, 30
        );
    }
}
