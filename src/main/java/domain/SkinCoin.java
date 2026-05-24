package domain;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Moneda skin — al ser recogida, aplica temporalmente una skin al jugador.
 * El efecto dura hasta que recoge otra moneda o muere.
 * El jugador recupera su skin original en ambos casos.
 */
public class SkinCoin extends Coin {
    private static final long serialVersionUID = 1L;

    private final Skin targetSkin;
    private final Color displayColor;

    public SkinCoin(Position position, Skin targetSkin) {
        super(position);
        this.targetSkin = targetSkin;
        switch (targetSkin) {
            case RED:   this.displayColor = Color.RED;   break;
            case BLUE:  this.displayColor = Color.BLUE;  break;
            case GREEN: this.displayColor = Color.GREEN; break;
            default:    this.displayColor = Color.WHITE; break;
        }
    }

    @Override
    public void onCollected(Player player, TheDOPOHardestGame game) {
        player.resetSkin();       // cancela cualquier skin previa
        player.applySkin(targetSkin);
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int x = offsetX + getPosition().getCol() * cellSize;
        int y = offsetY + getPosition().getRow() * cellSize;

        // Moneda con borde blanco y relleno del color de la skin
        g.setColor(displayColor);
        g.fillOval(x + 8, y + 8, 24, 24);

        g.setColor(Color.WHITE);
        g.drawOval(x + 8, y + 8, 24, 24);

        // S de "skin" en el centro
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        g.drawString("S", x + 16, y + 25);
    }
}