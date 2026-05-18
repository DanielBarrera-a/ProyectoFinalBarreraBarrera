package domain;

import java.util.List;

public class TheDOPOHardestGame {
    private CellType[][] board;
    private Player player;
    private Player player2;
    private List<Enemy> enemies;
    private List<Coin> coins;
    private int timeRemaining;
    private boolean isGameOver;
    private boolean isVictory;
    private GameMode mode;

    public TheDOPOHardestGame(CellType[][] board, Position startPos, List<Enemy> enemies, List<Coin> coins,
            int timeLimit, GameMode mode, Skin skin) {
        this.board = board;
        if (mode == GameMode.PVP) {
            this.player = new Player(startPos, Skin.RED);
            Position endPos = startPos;
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[0].length; c++) {
                    if (board[r][c] == CellType.SAFE_END) {
                        endPos = new Position(r, c);
                        break;
                    }
                }
            }
            this.player2 = new Player(endPos, Skin.RED);
        } else {
            this.player = new Player(startPos, skin);
        }
        this.enemies = enemies;
        this.coins = coins;
        this.timeRemaining = timeLimit;
        this.isGameOver = false;
        this.isVictory = false;
        this.mode = mode;
    }

    public void tickTime() {
        if (isGameOver || isVictory)
            return;
        timeRemaining--;
        if (timeRemaining <= 0) {
            isGameOver = true;
        }
    }

    public void moveEnemies() {
        if (isGameOver || isVictory)
            return;
        for (Enemy enemy : enemies) {
            enemy.move(this);
        }
        checkCollisions();
    }

    public void movePlayer(int dRow, int dCol) {
        if (isGameOver || isVictory)
            return;
        int nextRow = player.getPosition().getRow() + dRow;
        int nextCol = player.getPosition().getCol() + dCol;

        if (player2 != null && nextRow == player2.getPosition().getRow() && nextCol == player2.getPosition().getCol()) {
            return;
        }

        // Validar movimientos diagonales también si no chocan con pared
        if (isValidPosition(nextRow, nextCol) && board[nextRow][nextCol] != CellType.WALL) {
            player.getPosition().setRow(nextRow);
            player.getPosition().setCol(nextCol);
            checkCollisions();
            checkZone();
        }
    }

    public void movePlayer2(int dRow, int dCol) {
        if (isGameOver || isVictory || player2 == null)
            return;
        int nextRow = player2.getPosition().getRow() + dRow;
        int nextCol = player2.getPosition().getCol() + dCol;

        if (nextRow == player.getPosition().getRow() && nextCol == player.getPosition().getCol()) {
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
        coins.removeIf(coin -> coin.getPosition().equals(player.getPosition())
                || (player2 != null && coin.getPosition().equals(player2.getPosition())));

        // Chocar con enemigos
        for (Enemy enemy : enemies) {
            if (enemy.getPosition().equals(player.getPosition())) {
                player.addDeath();
                player.getPosition().setRow(player.getRespawnPosition().getRow());
                player.getPosition().setCol(player.getRespawnPosition().getCol());
            }
            if (player2 != null && enemy.getPosition().equals(player2.getPosition())) {
                player2.addDeath();
                player2.getPosition().setRow(player2.getRespawnPosition().getRow());
                player2.getPosition().setCol(player2.getRespawnPosition().getCol());
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

        if (player2 != null) {
            CellType currentCell2 = board[player2.getPosition().getRow()][player2.getPosition().getCol()];
            if (currentCell2 == CellType.SAFE_MID) {
                player2.setRespawnPosition(
                        new Position(player2.getPosition().getRow(), player2.getPosition().getCol()));
            } else if (currentCell2 == CellType.SAFE_START) {
                if (coins.isEmpty()) {
                    isVictory = true;
                }
            }
        }
    }

    public boolean isValidPosition(int r, int c) {
        return r >= 0 && r < board.length && c >= 0 && c < board[0].length;
    }

    public CellType getCell(int r, int c) {
        return board[r][c];
    }

    public int getRows() {
        return board.length;
    }

    public int getCols() {
        return board[0].length;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getPlayer2() {
        return player2;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isVictory() {
        return isVictory;
    }

    public GameMode getMode() {
        return mode;
    }

}
