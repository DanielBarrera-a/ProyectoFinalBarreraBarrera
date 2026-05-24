package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Acelerado Tipo A.
 * Se desplaza en línea recta (horizontal o vertical) al doble de velocidad.
 * Rebota en paredes. Dificultad: Alta.
 * Para definirlo en el .txt se usa:
 * ENEMY TYPE_A r c HORIZONTAL
 * ENEMY TYPE_A r c VERTICAL
 */
public class AcceleratedEnemy extends Enemy {
    private static final long serialVersionUID = 1L;

    private boolean isHorizontal;
    private int direction; // 1 = derecha/abajo, -1 = izquierda/arriba

    public AcceleratedEnemy(Position position, boolean isHorizontal) {
        super(position);
        this.isHorizontal = isHorizontal;
        this.direction = 1;
    }

    @Override
    public void move(TheDOPOHardestGame game) {
        // Se mueve 2 celdas por tick (doble de velocidad)
        for (int step = 0; step < 2; step++) {
            int nextRow = position.getRow() + (isHorizontal ? 0 : direction);
            int nextCol = position.getCol() + (isHorizontal ? direction : 0);

            if (game.isValidPosition(nextRow, nextCol)
                    && game.getCell(nextRow, nextCol) != CellType.WALL) {
                position.setRow(nextRow);
                position.setCol(nextCol);
            } else {
                direction *= -1; // rebotar
                nextRow = position.getRow() + (isHorizontal ? 0 : direction);
                nextCol = position.getCol() + (isHorizontal ? direction : 0);
                if (game.isValidPosition(nextRow, nextCol)
                        && game.getCell(nextRow, nextCol) != CellType.WALL) {
                    position.setRow(nextRow);
                    position.setCol(nextCol);
                }
                break; // tras rebotar termina el tick
            }
        }
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int x = offsetX + position.getCol() * cellSize + 5;
        int y = offsetY + position.getRow() * cellSize + 5;

        // Rojo oscuro para indicar peligro alto
        g.setColor(new Color(180, 0, 0));
        g.fillOval(x, y, 30, 30);

        // Contorno naranja
        g.setColor(Color.ORANGE);
        g.drawOval(x, y, 30, 30);

        // Letra A
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 11));
        g.drawString("A", x + 10, y + 20);
    }
}