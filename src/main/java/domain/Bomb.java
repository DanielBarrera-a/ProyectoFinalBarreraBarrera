package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Bomba estática del tablero.
 * Al ser pisada por un jugador: lo mata y reaparece en su respawn.
 * Al ser pisada por un enemigo: lo elimina del juego.
 * Se desactiva al primer contacto con cualquier entidad.
 */
public class Bomb extends SpecialElement {
    private static final long serialVersionUID = 1L;

    public Bomb(Position position) {
        super(position);
    }

    @Override
    public void onPlayerContact(Player player, TheDOPOHardestGame game) {
        deactivate();
        player.addDeath();
        player.getPosition().setRow(player.getRespawnPosition().getRow());
        player.getPosition().setCol(player.getRespawnPosition().getCol());
        player.resetShield();
    }

    @Override
    public void onEnemyContact(Enemy enemy, TheDOPOHardestGame game) {
        deactivate();
        game.removeEnemy(enemy);
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        if (!active) return;
        int x = offsetX + position.getCol() * cellSize;
        int y = offsetY + position.getRow() * cellSize;

        // Cuerpo negro de la bomba
        g.setColor(Color.BLACK);
        g.fillOval(x + 8, y + 10, 22, 22);

        // Mecha roja
        g.setColor(Color.RED);
        g.drawLine(x + 18, y + 10, x + 24, y + 4);

        // Chispa naranja
        g.setColor(Color.ORANGE);
        g.fillOval(x + 22, y + 2, 6, 6);
    }
}