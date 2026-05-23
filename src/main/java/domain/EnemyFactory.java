package domain;

import java.awt.Color;

/**
 * Fábrica de enemigos.
 * Nota Para agregar un nuevo tipo de enemigo al juego, solo se agrega
 * un nuevo caso aqui. No se modifica ConfigLoader ni ninguna otra clase.
 * Esta clse tiene como uniuca responsabilidad es saber como construir cada tipo de enemigo.
 */
public class EnemyFactory {

    /**
     * Crea un enemigo segun el tipo indicado en el archivo de nivel.
     *
     * @param type         String del tipo, ej: "BASIC_BLUE", "BASIC_RED"
     * @param position     Posición inicial del enemigo
     * @param isHorizontal true si se mueve horizontalmente
     * @return El enemigo construido
     * @throws GameException Si el tipo no está registrado
     */
    public static Enemy create(String type, Position position, boolean isHorizontal) throws GameException {
        switch (type) {
            case "BASIC_BLUE":
                return new BasicBlueEnemy(position, isHorizontal, Color.BLUE);
            default:
                throw new GameException("Tipo de enemigo desconocido: " + type);
        }
    }
}
