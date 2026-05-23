package domain;

import java.awt.Graphics;

/**
 * Clase abstracta que representa una moneda.
 * Hereda de Entity
 * Nota : Para agregar una nueva moneda, solo se crea una nueva subclase
 * No se modifica esta clase ni el motor del juego.
 */
public abstract class Coin extends Entity {
    private static final long serialVersionUID = 1L;

    public Coin(Position position) {
        super(new Position(position.getRow(), position.getCol()));
    }

    /**
     * Define que ocurre cuando el jugador recoge esta moneda
     * Cada subclase implementa su propio comportamiento
     */
    public abstract void onCollected(Player player, TheDOPOHardestGame game);

    /**
     * Define como se dibuja esta moneda en pantalla
     */

    public abstract void draw(Graphics g, int offsetX, int offsetY, int cellSize);
}
