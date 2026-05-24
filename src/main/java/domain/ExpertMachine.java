package domain;

import java.util.List;

/**
 * Perfil de maquina experta para el modo PvM.
 * Su estrategia es:
 * 1. Si hay monedas pendientes, se mueve hacia la moneda más cercana.
 * 2. Si no hay monedas, se mueve hacia la celda SAFE_END.
 * 3. Evita paredes calculando el paso que más reduce la distancia Manhattan.
 */
public class ExpertMachine implements MachinePlayer {
    private static final long serialVersionUID = 1L;

    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    @Override
    public int[] nextMove(TheDOPOHardestGame game, Player machine) {
        Position target = findTarget(game);
        if (target == null) return new int[]{0, 0};
        return bestMoveToward(game, machine.getPosition(), target);
    }

    /**
     * Encuentra el objetivo más prioritario: la moneda más cercana o el SAFE_END.
     */
    private Position findTarget(TheDOPOHardestGame game) {
        Position current = game.getPlayer2().getPosition();
        List<Coin> coins = game.getCoins();

        if (!coins.isEmpty()) {
            Position nearest = null;
            int minDist = Integer.MAX_VALUE;
            for (Coin coin : coins) {
                int dist = manhattan(current, coin.getPosition());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = coin.getPosition();
                }
            }
            return nearest;
        }

        // Sin monedas: busca la celda SAFE_END en el tablero
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                if (game.getCell(r, c) == CellType.SAFE_END) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    /**
     * Elige la direccion que más reduce la distancia Manhattan al objetivo,
     * ignorando movimientos hacia paredes o fuera del tablero.
     */
    private int[] bestMoveToward(TheDOPOHardestGame game, Position from, Position target) {
        int[] best = {0, 0};
        int bestDist = manhattan(from, target);

        for (int[] dir : DIRECTIONS) {
            int nr = from.getRow() + dir[0];
            int nc = from.getCol() + dir[1];
            if (!game.isValidPosition(nr, nc)) continue;
            if (game.getCell(nr, nc) == CellType.WALL) continue;
            int dist = manhattan(new Position(nr, nc), target);
            if (dist < bestDist) {
                bestDist = dist;
                best = dir;
            }
        }
        return best;
    }

    private int manhattan(Position a, Position b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }
}