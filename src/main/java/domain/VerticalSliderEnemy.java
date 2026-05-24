package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Deslizador vertical Tipo V.
 * Se desplaza exclusivamente en línea recta vertical.
 * Rebota al chocar con paredes superior e inferior.
 * Dificultad: Baja.
 * Para definirlo en el .txt se usa:
 * ENEMY TYPE_V r c
 */
public class VerticalSliderEnemy extends Enemy {
    private static final long serialVersionUID = 1L;

    private int direction; // 1 = abajo, -1 = arriba

    public VerticalSliderEnemy(Position position) {
        super(position);
        this.direction = 1;
    }

    @Override
    public void move(TheDOPOHardestGame game) {
        int nextRow = position.getRow() + direction;
        int nextCol = position.getCol();

        if (game.isValidPosition(nextRow, nextCol)
                && game.getCell(nextRow, nextCol) != CellType.WALL) {
            position.setRow(nextRow);
        } else {
            direction *= -1; // rebotar
            int reboundRow = position.getRow() + direction;
            if (game.isValidPosition(reboundRow, nextCol)
                    && game.getCell(reboundRow, nextCol) != CellType.WALL) {
                position.setRow(reboundRow);
            }
        }
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int x = offsetX + position.getCol() * cellSize;
        int y = offsetY + position.getRow() * cellSize;

        // Rombo azul claro para distinguirlo visualmente
        g.setColor(new Color(100, 180, 255));
        int[] xs = { x + cellSize / 2, x + cellSize - 6, x + cellSize / 2, x + 6 };
        int[] ys = { y + 4, y + cellSize / 2, y + cellSize - 4, y + cellSize / 2 };
        g.fillPolygon(xs, ys, 4);

        // Contorno
        g.setColor(Color.BLUE);
        g.drawPolygon(xs, ys, 4);

        // Letra V
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        g.drawString("V", x + cellSize / 2 - 4, y + cellSize / 2 + 5);
    }
}