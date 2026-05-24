package domain;

import java.awt.Graphics;
import java.io.Serializable;

/**
 * Clase abstracta que representa un elemento especial del tablero.
 * Para agregar un nuevo elemento especial, solo se crea una nueva subclase.
 * No se modifica TheDOPOHardestGame ni ConfigLoader.
 */
public abstract class SpecialElement implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Position position;
    protected boolean active; // false = ya fue usado, no se dibuja ni aplica

    public SpecialElement(Position position) {
        this.position = new Position(position.getRow(), position.getCol());
        this.active = true;
    }

    /**
     * Define qué ocurre cuando un jugador pisa este elemento.
     */
    public abstract void onPlayerContact(Player player, TheDOPOHardestGame game);

    /**
     * Define qué ocurre cuando un enemigo pisa este elemento.
     */
    public abstract void onEnemyContact(Enemy enemy, TheDOPOHardestGame game);

    /**
     * Define cómo se dibuja este elemento en pantalla.
     */
    public abstract void draw(Graphics g, int offsetX, int offsetY, int cellSize);

    public Position getPosition() { return position; }
    public boolean isActive()     { return active; }
    public void deactivate()      { active = false; }
}