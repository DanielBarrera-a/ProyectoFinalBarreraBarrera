package test;

import domain.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testPlayerMovementValid() {
        CellType[][] board = createEmptyBoard(5, 5);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2), new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(1, 0); // Move down
        assertEquals(3, game.getPlayer().getPosition().getRow());
        assertEquals(2, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testPlayerMovementWall() {
        CellType[][] board = createEmptyBoard(5, 5);
        board[3][2] = CellType.WALL;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2), new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(1, 0); // Try to move down to wall
        assertEquals(2, game.getPlayer().getPosition().getRow()); // Position unchanged
        assertEquals(2, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testEnemyCollisionResetsPlayer() {
        CellType[][] board = createEmptyBoard(5, 5);
        ArrayList<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 3), true)); // Enemy to the right
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2), enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        
        game.movePlayer(0, 1); // Move right, colliding with enemy
        
        assertEquals(1, game.getPlayer().getDeaths());
        assertEquals(2, game.getPlayer().getPosition().getRow()); // Back to original respawn pos (2,2)
        assertEquals(2, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testCoinCollection() {
        CellType[][] board = createEmptyBoard(5, 5);
        ArrayList<Coin> coins = new ArrayList<>();
        coins.add(new Coin(new Position(2, 3), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2), new ArrayList<>(), coins, 60, GameMode.PLAYER, Skin.RED);
        
        assertEquals(1, game.getCoins().size());
        game.movePlayer(0, 1); // Move to coin
        assertEquals(0, game.getCoins().size());
    }

    @Test
    public void testVictoryCondition() {
        CellType[][] board = createEmptyBoard(5, 5);
        board[2][3] = CellType.SAFE_END;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2), new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        
        game.movePlayer(0, 1); // Move to END
        assertTrue(game.isVictory());
    }

    private CellType[][] createEmptyBoard(int rows, int cols) {
        CellType[][] board = new CellType[rows][cols];
        for(int i=0; i<rows; i++) {
            for(int j=0; j<cols; j++) {
                board[i][j] = CellType.EMPTY;
            }
        }
        return board;
    }
}
