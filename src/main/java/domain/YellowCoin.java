package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Moneda amarilla estándar del juego.
 * Al ser recogida no hace nada especial: solo desaparece del mapa.
 * El motor del juego la elimina de la lista cuando detecta la colisión.
 */
public class YellowCoin extends Coin {
    private static final long serialVersionUID = 1L;

    public YellowCoin(Position position) {
        super(position);
    }

    @Override
    public void onCollected(Player player, TheDOPOHardestGame game) {
        // La moneda amarilla no tiene efecto especial.
        // Solo desaparece, lo cual maneja TheDOPOHardestGame al removerla de la lista.
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        g.setColor(Color.YELLOW);
        g.fillOval(
                offsetX + getPosition().getCol() * cellSize + 10,
                offsetY + getPosition().getRow() * cellSize + 10,
                20, 20
        );

        g.setColor(Color.BLACK);
        g.drawOval(
                offsetX + getPosition().getCol() * cellSize + 10,
                offsetY + getPosition().getRow() * cellSize + 10,
                20, 20
        );
    }
}
