package domain;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Fábrica de enemigos.
 * Para agregar un nuevo tipo de enemigo al juego, solo se agrega
 * un nuevo caso aquí. No se modifica ConfigLoader ni ninguna otra clase.
 */
public class EnemyFactory {

    /**
     * Crea un enemigo según el tipo indicado en el archivo de nivel.
     * Para PATROL, parts contiene los waypoints adicionales.
     *
     * @param type         String del tipo: "BASIC_BLUE", "TYPE_V", "TYPE_A", "PATROL"
     * @param position     Posición inicial del enemigo
     * @param isHorizontal true si se mueve horizontalmente (ignorado en PATROL y TYPE_V)
     * @param parts        Fragmentos completos de la línea (para parsear waypoints en PATROL)
     * @return El enemigo construido
     * @throws GameException Si el tipo no está registrado
     */
    public static Enemy create(String type, Position position, boolean isHorizontal, String[] parts) throws GameException {
        switch (type) {
            case "BASIC_BLUE":
                return new BasicBlueEnemy(position, isHorizontal, Color.BLUE);
            case "TYPE_V":
                return new VerticalSliderEnemy(position);
            case "TYPE_A":
                return new AcceleratedEnemy(position, isHorizontal);
            case "PATROL":
                return createPatrol(parts);
            default:
                throw new GameException("Tipo de enemigo desconocido: " + type);
        }
    }

    /**
     * Parsea los waypoints de una línea PATROL.
     * Formato: ENEMY PATROL r1 c1 r2 c2 r3 c3 ...
     */
    private static PatrolEnemy createPatrol(String[] parts) throws GameException {
        // parts[0]=ENEMY parts[1]=PATROL parts[2]=r1 parts[3]=c1 parts[4]=r2 ...
        if (parts.length < 6 || (parts.length - 2) % 2 != 0) {
            throw new GameException("PATROL necesita al menos 2 waypoints: ENEMY PATROL r1 c1 r2 c2 ...");
        }
        List<Position> waypoints = new ArrayList<>();
        for (int i = 2; i < parts.length - 1; i += 2) {
            int r = Integer.parseInt(parts[i]);
            int c = Integer.parseInt(parts[i + 1]);
            waypoints.add(new Position(r, c));
        }
        return new PatrolEnemy(waypoints);
    }
}