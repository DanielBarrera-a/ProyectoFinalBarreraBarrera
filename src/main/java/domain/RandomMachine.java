package domain;

import java.util.Random;

/**
 * Perfil de maquina aleatoria para el modo PvM.
 * En cada tick elige una dirección al azar entre las 8 posibles (incluyendo diagonal).
 * No tiene ninguna estrategia: puede quedarse quieta o moverse hacia atrás.
 */
public class RandomMachine implements MachinePlayer {
    private static final long serialVersionUID = 1L;

    private final Random random = new Random();

    // Las 8 direcciones posibles (incluyendo diagonal y quieto)
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1},
            {0, 0}
    };

    @Override
    public int[] nextMove(TheDOPOHardestGame game, Player machine) {
        return DIRECTIONS[random.nextInt(DIRECTIONS.length)];
    }
}