package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Motor principal del juego la clase mas dura del sistema :D
 * Trabaja con las clases abstractas, por lo tanto agregar nuevos tipos
 * no requiere modificar esta clase para lo que vaya a implementar, NO LA TOQUE
 */
public class TheDOPOHardestGame implements Serializable {
    private static final long serialVersionUID = 1L;

    private CellType[][] board;
    private Player player;
    private Player player2;
    private MachinePlayer machine; // null si no es modo PvM
    private List<Enemy> enemies;
    private List<Coin> coins;
    private List<SpecialElement> specialElements;
    private int timeRemaining;
    private boolean isGameOver;
    private boolean isVictory;
    private GameMode mode;

    public TheDOPOHardestGame(CellType[][] board, Position startPos, List<Enemy> enemies, List<Coin> coins,
                              int timeLimit, GameMode mode, Skin skin) {
        this(board, startPos, enemies, coins, new ArrayList<>(), timeLimit, mode, skin);
    }

    public TheDOPOHardestGame(CellType[][] board, Position startPos, List<Enemy> enemies, List<Coin> coins,
                              List<SpecialElement> specialElements, int timeLimit, GameMode mode, Skin skin) {
        this.board = board;

        if (mode == GameMode.PVP) {
            this.player = new Player(startPos, Skin.RED);
            Position endPos = findSafeEnd(startPos);
            this.player2 = new Player(endPos, Skin.RED);
        } else if (mode == GameMode.PVM) {
            this.player = new Player(startPos, skin);
            Position endPos = findSafeEnd(startPos);
            this.player2 = new Player(endPos, Skin.RED);
            this.machine = new RandomMachine(); // perfil por defecto
        } else {
            this.player = new Player(startPos, skin);
        }

        this.enemies = enemies;
        this.coins = coins;
        this.specialElements = specialElements;
        this.timeRemaining = timeLimit;
        this.isGameOver = false;
        this.isVictory = false;
        this.mode = mode;
    }

    /**
     * Permite definir el perfil de la máquina antes de iniciar la partida.
     */
    public void setMachine(MachinePlayer machine) {
        this.machine = machine;
    }

    /**
     * Busca la posición de SAFE_END en el tablero para colocar al jugador 2.
     */
    private Position findSafeEnd(Position fallback) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (board[r][c] == CellType.SAFE_END) {
                    return new Position(r, c);
                }
            }
        }
        return fallback;
    }

    public void tickTime() {
        if (isGameOver || isVictory) return;
        timeRemaining--;
        if (timeRemaining <= 0) {
            isGameOver = true;
        }
    }

    public void moveEnemies() {
        if (isGameOver || isVictory) return;
        for (Enemy enemy : enemies) {
            enemy.move(this);
        }
        // Si hay máquina en modo PvM, la movemos en este mismo tick
        if (mode == GameMode.PVM && machine != null && player2 != null) {
            int[] move = machine.nextMove(this, player2);
            movePlayer2(move[0], move[1]);
        }
        checkCollisions();
    }

    public void movePlayer(int dRow, int dCol) {
        if (isGameOver || isVictory) return;
        int nextRow = player.getPosition().getRow() + dRow;
        int nextCol = player.getPosition().getCol() + dCol;

        if (player2 != null
                && nextRow == player2.getPosition().getRow()
                && nextCol == player2.getPosition().getCol()) {
            return;
        }

        if (isValidPosition(nextRow, nextCol) && board[nextRow][nextCol] != CellType.WALL) {
            player.getPosition().setRow(nextRow);
            player.getPosition().setCol(nextCol);
            checkCollisions();
            checkZone();
        }
    }

    public void movePlayer2(int dRow, int dCol) {
        if (isGameOver || isVictory || player2 == null) return;
        int nextRow = player2.getPosition().getRow() + dRow;
        int nextCol = player2.getPosition().getCol() + dCol;

        if (nextRow == player.getPosition().getRow()
                && nextCol == player.getPosition().getCol()) {
            return;
        }

        if (isValidPosition(nextRow, nextCol) && board[nextRow][nextCol] != CellType.WALL) {
            player2.getPosition().setRow(nextRow);
            player2.getPosition().setCol(nextCol);
            checkCollisions();
            checkZone();
        }
    }

    private void checkCollisions() {
        // Recoger monedas
        List<Coin> collected = new ArrayList<>();
        for (Coin coin : coins) {
            if (coin.getPosition().equals(player.getPosition())) {
                coin.onCollected(player, this);
                collected.add(coin);
            } else if (player2 != null && coin.getPosition().equals(player2.getPosition())) {
                coin.onCollected(player2, this);
                collected.add(coin);
            }
        }
        coins.removeAll(collected);

        // Elementos especiales con jugadores
        for (SpecialElement se : specialElements) {
            if (!se.isActive()) continue;
            if (se.getPosition().equals(player.getPosition())) {
                se.onPlayerContact(player, this);
            } else if (player2 != null && se.getPosition().equals(player2.getPosition())) {
                se.onPlayerContact(player2, this);
            }
        }
        specialElements.removeIf(se -> !se.isActive());

        // Elementos especiales con enemigos
        List<Enemy> enemiesToCheck = new ArrayList<>(enemies);
        for (Enemy enemy : enemiesToCheck) {
            for (SpecialElement se : specialElements) {
                if (!se.isActive()) continue;
                if (se.getPosition().equals(enemy.getPosition())) {
                    se.onEnemyContact(enemy, this);
                }
            }
        }
        specialElements.removeIf(se -> !se.isActive());

        // Colisiones jugadores con enemigos
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(player.getPosition())) {
                boolean died = player.applyEnemyHit();
                if (died) {
                    player.getPosition().setRow(player.getRespawnPosition().getRow());
                    player.getPosition().setCol(player.getRespawnPosition().getCol());
                    player.resetShield();
                }
            }
            if (player2 != null && enemy.getPosition().equals(player2.getPosition())) {
                boolean died = player2.applyEnemyHit();
                if (died) {
                    player2.getPosition().setRow(player2.getRespawnPosition().getRow());
                    player2.getPosition().setCol(player2.getRespawnPosition().getCol());
                    player2.resetShield();
                }
            }
        }
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    private void checkZone() {
        CellType currentCell = board[player.getPosition().getRow()][player.getPosition().getCol()];
        if (currentCell == CellType.SAFE_MID) {
            player.setRespawnPosition(new Position(player.getPosition().getRow(), player.getPosition().getCol()));
        } else if (currentCell == CellType.SAFE_END) {
            if (coins.isEmpty()) isVictory = true;
        }

        if (player2 != null) {
            CellType currentCell2 = board[player2.getPosition().getRow()][player2.getPosition().getCol()];
            if (currentCell2 == CellType.SAFE_MID) {
                player2.setRespawnPosition(new Position(player2.getPosition().getRow(), player2.getPosition().getCol()));
            } else if (currentCell2 == CellType.SAFE_START) {
                if (coins.isEmpty()) isVictory = true;
            }
        }
    }

    public boolean isValidPosition(int r, int c) {
        return r >= 0 && r < board.length && c >= 0 && c < board[0].length;
    }

    public CellType getCell(int r, int c) {
        return board[r][c]; }

    public int getRows()                   {
        return board.length; }

    public int getCols()                   {
        return board[0].length; }

    public Player getPlayer()              {
        return player; }

    public Player getPlayer2()             {
        return player2; }

    public List<Enemy> getEnemies()        {
        return enemies; }

    public List<Coin> getCoins()           {
        return coins; }

    public int getTimeRemaining()          {
        return timeRemaining; }

    public boolean isGameOver()            {
        return isGameOver; }

    public boolean isVictory()             {
        return isVictory; }

    public GameMode getMode()              {
        return mode; }

    public List<SpecialElement> getSpecialElements() {
        return specialElements; }
}