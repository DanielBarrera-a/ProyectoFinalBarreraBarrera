package domain;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

/**
 * Enemigo patrullero.
 * Sigue una ruta circular o geométrica predefinida de waypoints.
 * Para definirlo en el .txt se usa:
 * ENEMY PATROL r1 c1 r2 c2 r3 c3 ... (mínimo 2 waypoints)
 */
public class PatrolEnemy extends Enemy {
    private static final long serialVersionUID = 1L;

    private List<Position> waypoints;
    private int currentIndex;

    public PatrolEnemy(List<Position> waypoints) {
        super(new Position(waypoints.get(0).getRow(), waypoints.get(0).getCol()));
        this.waypoints = waypoints;
        this.currentIndex = 0;
    }

    @Override
    public void move(TheDOPOHardestGame game) {
        int nextIndex = (currentIndex + 1) % waypoints.size();
        Position next = waypoints.get(nextIndex);

        // Solo se mueve si la siguiente posición es válida y no es pared
        if (game.isValidPosition(next.getRow(), next.getCol())
                && game.getCell(next.getRow(), next.getCol()) != CellType.WALL) {
            position.setRow(next.getRow());
            position.setCol(next.getCol());
            currentIndex = nextIndex;
        }
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        g.setColor(new Color(0, 100, 255)); // azul oscuro para distinguirlo
        int x = offsetX + position.getCol() * cellSize + 5;
        int y = offsetY + position.getRow() * cellSize + 5;
        g.fillOval(x, y, 30, 30);

        // Marca visual: círculo interior blanco
        g.setColor(Color.WHITE);
        g.fillOval(x + 9, y + 9, 12, 12);
    }
}