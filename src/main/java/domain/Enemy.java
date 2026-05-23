package domain;

import java.awt.Graphics;

/**
 * Clase abstracta que representa un enemigo.
 * Hereda de Entity
 * Nota Para agregar un nuevo enemigo, solo se crea una nueva subclase
 */
public abstract class Enemy extends Entity {
    private static final long serialVersionUID = 1L;

    public Enemy(Position position) {
        super(new Position(position.getRow(), position.getCol()));
    }

    /**
     * Define como se mueve este enemigo en cada tick del juego
     */
    public abstract void move(TheDOPOHardestGame game);

    /**
     * Define como se dibuja este enemigo en pantalla
     */
    public abstract void draw(Graphics g, int offsetX, int offsetY, int cellSize);
}
