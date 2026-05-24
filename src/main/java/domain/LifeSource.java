package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Fuente de vida estática del tablero.
 * Al ser pisada por un jugador: reduce en 1 su contador de muertes (vida extra).
 * Los enemigos la ignoran completamente.
 * Se desactiva tras ser usada una vez.
 */
public class LifeSource extends SpecialElement {
    private static final long serialVersionUID = 1L;

    public LifeSource(Position position) {
        super(position);
    }

    @Override
    public void onPlayerContact(Player player, TheDOPOHardestGame game) {
        deactivate();
        player.reduceDeath(); // ya existe en Player
    }

    @Override
    public void onEnemyContact(Enemy enemy, TheDOPOHardestGame game) {
        // Los enemigos no interactúan con fuentes de vida
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        if (!active) return;
        int x = offsetX + position.getCol() * cellSize;
        int y = offsetY + position.getRow() * cellSize;

        // Cruz de primeros auxilios (fondo blanco, cruz roja)
        g.setColor(new Color(220, 255, 220));
        g.fillRoundRect(x + 5, y + 5, cellSize - 10, cellSize - 10, 6, 6);

        g.setColor(new Color(0, 180, 0));
        g.drawRoundRect(x + 5, y + 5, cellSize - 10, cellSize - 10, 6, 6);

        // Cruz
        g.setColor(new Color(0, 180, 0));
        int cx = x + cellSize / 2;
        int cy = y + cellSize / 2;
        g.fillRect(cx - 2, cy - 8, 4, 16); // vertical
        g.fillRect(cx - 8, cy - 2, 16, 4); // horizontal
    }
}
