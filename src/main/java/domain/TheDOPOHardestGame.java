package domain;

import java.util.List;

public class TheDOPOHardestGame {
    private CellType[][] board;
    private Player player;
    private List<Enemy> enemies;
    private List<Coin> coins;
    private int timeRemaining;
    private boolean isGameOver;
    private boolean isVictory;
    private GameMode mode;

    public TheDOPOHardestGame(CellType[][] board, Position startPos, List<Enemy> enemies, List<Coin> coins, int timeLimit, GameMode mode, Skin skin) {
        this.board = board;
        this.player = new Player(startPos, skin);
        this.enemies = enemies;
        this.coins = coins;
        this.timeRemaining = timeLimit;
        this.isGameOver = false;
        this.isVictory = false;
        this.mode = mode;
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
        checkCollisions();
    }

    public void movePlayer(int dRow, int dCol) {
        if (isGameOver || isVictory) return;
        int nextRow = player.getPosition().getRow() + dRow;
        int nextCol = player.getPosition().getCol() + dCol;

        // Validar movimientos diagonales también si no chocan con pared
        if (isValidPosition(nextRow, nextCol) && board[nextRow][nextCol] != CellType.WALL) {
            player.getPosition().setRow(nextRow);
            player.getPosition().setCol(nextCol);
            checkCollisions();
            checkZone();
        }
    }

    private void checkCollisions() {
        // Recoger monedas
        coins.removeIf(coin -> coin.getPosition().equals(player.getPosition()));

        // Chocar con enemigos
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(player.getPosition())) {
                player.addDeath();
                player.getPosition().setRow(player.getRespawnPosition().getRow());
                player.getPosition().setCol(player.getRespawnPosition().getCol());
                break; // Solo muere una vez en este tick
            }
        }
    }

    private void checkZone() {
        CellType currentCell = board[player.getPosition().getRow()][player.getPosition().getCol()];
        if (currentCell == CellType.SAFE_MID) {
            player.setRespawnPosition(new Position(player.getPosition().getRow(), player.getPosition().getCol()));
        } else if (currentCell == CellType.SAFE_END) {
            if (coins.isEmpty()) {
                isVictory = true;
            }
        }
    }

    public boolean isValidPosition(int r, int c) {
        return r >= 0 && r < board.length && c >= 0 && c < board[0].length;
    }

    public CellType getCell(int r, int c) {
        return board[r][c]; }

    public int getRows() {
        return board.length; }

    public int getCols() {
        return board[0].length; }

    public Player getPlayer() {
        return player; }

    public List<Enemy> getEnemies() {
        return enemies; }

    public List<Coin> getCoins() {
        return coins; }

    public int getTimeRemaining() {
        return timeRemaining; }

    public boolean isGameOver() {
        return isGameOver; }

    public boolean isVictory() {
        return isVictory; }

    public GameMode getMode() {
        return mode; }

}
