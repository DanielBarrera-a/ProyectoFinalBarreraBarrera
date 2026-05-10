package test;

import domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    // HELPER

    private CellType[][] createEmptyBoard() {
        CellType[][] board = new CellType[5][5];
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                board[i][j] = CellType.EMPTY;
        return board;
    }

    private TheDOPOHardestGame basicGame(int startRow, int startCol) {
        return new TheDOPOHardestGame(
                createEmptyBoard(),
                new Position(startRow, startCol),
                new ArrayList<>(),
                new ArrayList<>(),
                60, GameMode.PLAYER, Skin.RED);
    }

    // 1. MOVIMIENTO DEL JUGADOR

    @Test
    public void testPlayerMovementValid() {
        TheDOPOHardestGame game = basicGame(2, 2);
        game.movePlayer(1, 0);
        assertEquals(3, game.getPlayer().getPosition().getRow());
        assertEquals(2, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testPlayerMovementWall() {
        CellType[][] board = createEmptyBoard();
        board[3][2] = CellType.WALL;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(1, 0);
        assertEquals(2, game.getPlayer().getPosition().getRow());
        assertEquals(2, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testPlayerMovementOutOfBounds() {
        TheDOPOHardestGame game = basicGame(0, 0);
        game.movePlayer(-1, 0); // intenta salir por arriba
        assertEquals(0, game.getPlayer().getPosition().getRow());
        assertEquals(0, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testPlayerDiagonalMovement() {
        TheDOPOHardestGame game = basicGame(2, 2);
        game.movePlayer(1, 1); // diagonal
        assertEquals(3, game.getPlayer().getPosition().getRow());
        assertEquals(3, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testPlayerCannotMoveWhenGameOver() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                createEmptyBoard(), new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(),
                1, GameMode.PLAYER, Skin.RED);
        game.tickTime(); // tiempo se agota → isGameOver = true
        game.movePlayer(1, 0);
        assertEquals(2, game.getPlayer().getPosition().getRow()); // no se movió
    }

    @Test
    public void testPlayerCannotMoveAfterVictory() {
        CellType[][] board = createEmptyBoard();
        board[2][3] = CellType.SAFE_END;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1); // llega al END → victoria
        assertTrue(game.isVictory());
        game.movePlayer(0, 1); // intenta seguir moviéndose
        assertEquals(3, game.getPlayer().getPosition().getCol()); // se quedó en la celda END
    }

    // 2. TIEMPO Y GAME OVER

    @Test
    public void testTimeDecreases() {
        TheDOPOHardestGame game = basicGame(2, 2);
        int initial = game.getTimeRemaining();
        game.tickTime();
        assertEquals(initial - 1, game.getTimeRemaining());
    }

    @Test
    public void testGameOverWhenTimeRunsOut() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                createEmptyBoard(), new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(),
                1, GameMode.PLAYER, Skin.RED);
        assertFalse(game.isGameOver());
        game.tickTime();
        assertTrue(game.isGameOver());
    }

    @Test
    public void testTimeDoesNotDecreaseWhenGameOver() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                createEmptyBoard(), new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(),
                1, GameMode.PLAYER, Skin.RED);
        game.tickTime(); // llega a 0 → game over
        game.tickTime(); // no debería bajar de 0
        assertTrue(game.getTimeRemaining() <= 0);
        assertTrue(game.isGameOver());
    }

    // 3. MONEDAS

    @Test
    public void testCoinCollection() {
        CellType[][] board = createEmptyBoard();
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(new Position(2, 3), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), coins, 60, GameMode.PLAYER, Skin.RED);
        assertEquals(1, game.getCoins().size());
        game.movePlayer(0, 1);
        assertEquals(0, game.getCoins().size());
    }

    @Test
    public void testMultipleCoinsCollectedOneByOne() {
        CellType[][] board = createEmptyBoard();
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(new Position(2, 3), true));
        coins.add(new Coin(new Position(2, 4), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), coins, 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1);
        assertEquals(1, game.getCoins().size());
        game.movePlayer(0, 1);
        assertEquals(0, game.getCoins().size());
    }

    @Test
    public void testNoVictoryWithCoinsRemaining() {
        CellType[][] board = createEmptyBoard();
        board[2][3] = CellType.SAFE_END;
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(new Position(4, 4), true)); // moneda sin recoger
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), coins, 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1); // llega a SAFE_END pero hay monedas
        assertFalse(game.isVictory());
    }

    // 4. COLISIONES CON ENEMIGOS

    @Test
    public void testEnemyCollisionResetsPlayer() {
        CellType[][] board = createEmptyBoard();
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 3), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1);
        assertEquals(1, game.getPlayer().getDeaths());
        assertEquals(2, game.getPlayer().getPosition().getRow());
        assertEquals(2, game.getPlayer().getPosition().getCol());
    }

    @Test
    public void testMultipleDeathsAccumulate() {
        CellType[][] board = createEmptyBoard();
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 3), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1); // muerte 1
        game.movePlayer(0, 1); // muerte 2
        assertEquals(2, game.getPlayer().getDeaths());
    }

    @Test
    public void testEnemyDoesNotCollectCoins() {
        CellType[][] board = createEmptyBoard();
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(new Position(3, 5), true));
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 5), false)); // vertical, baja a (3,5)
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                enemies, coins, 60, GameMode.PLAYER, Skin.RED);
        game.moveEnemies();
        assertEquals(1, game.getCoins().size()); // moneda sigue ahí
    }

    // 5. ZONA SEGURA INTERMEDIA (RESPAWN)

    @Test
    public void testSafeMidUpdatesRespawnPoint() {
        CellType[][] board = createEmptyBoard();
        board[2][3] = CellType.SAFE_MID;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1); // entra a SAFE_MID
        assertEquals(2, game.getPlayer().getRespawnPosition().getRow());
        assertEquals(3, game.getPlayer().getRespawnPosition().getCol());
    }

    @Test
    public void testPlayerRespawnsAtMidZoneAfterDeath() {
        CellType[][] board = createEmptyBoard();
        board[2][3] = CellType.SAFE_MID;
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 1), true)); // enemigo a la izquierda
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1); // llega a SAFE_MID → nuevo respawn en (2,3)
        game.movePlayer(0, -2); // se mueve hacia el enemigo → muerte
        // debe regresar a (2,3), no a (2,2)
        assertEquals(2, game.getPlayer().getPosition().getRow());
        assertEquals(3, game.getPlayer().getPosition().getCol());
    }

    // 6. VICTORIA

    @Test
    public void testVictoryCondition() {
        CellType[][] board = createEmptyBoard();
        board[2][3] = CellType.SAFE_END;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1);
        assertTrue(game.isVictory());
    }

    @Test
    public void testVictoryAfterCollectingAllCoins() {
        CellType[][] board = createEmptyBoard();
        board[2][4] = CellType.SAFE_END;
        List<Coin> coins = new ArrayList<>();
        coins.add(new Coin(new Position(2, 3), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(2, 2),
                new ArrayList<>(), coins, 60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(0, 1); // recoge la moneda
        assertFalse(game.isVictory()); // aún no llegó al END
        game.movePlayer(0, 1); // llega al END con 0 monedas
        assertTrue(game.isVictory());
    }

    // 7. MOVIMIENTO DE ENEMIGOS (BasicBlueEnemy)

    @Test
    public void testBasicBlueEnemyMovesHorizontally() {
        CellType[][] board = createEmptyBoard();
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 2), true));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(0, 0),
                enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.moveEnemies();
        assertEquals(2, game.getEnemies().get(0).getPosition().getRow());
        assertEquals(3, game.getEnemies().get(0).getPosition().getCol());
    }

    @Test
    public void testBasicBlueEnemyMovesVertically() {
        CellType[][] board = createEmptyBoard();
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 2), false));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(0, 0),
                enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.moveEnemies();
        assertEquals(3, game.getEnemies().get(0).getPosition().getRow());
        assertEquals(2, game.getEnemies().get(0).getPosition().getCol());
    }

    @Test
    public void testBasicBlueEnemyBouncesOffWall() {
        CellType[][] board = createEmptyBoard();
        board[2][4] = CellType.WALL;
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new BasicBlueEnemy(new Position(2, 3), true)); // junto a la pared → rebota a la izquierda
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(0, 0),
                enemies, new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        game.moveEnemies();
        assertEquals(2, game.getEnemies().get(0).getPosition().getCol());
    }

    // 8. POSICIÓN y EQUALS

    @Test
    public void testPositionEquals() {
        Position p1 = new Position(3, 4);
        Position p2 = new Position(3, 4);
        assertEquals(p1, p2);
    }

    @Test
    public void testPositionNotEquals() {
        Position p1 = new Position(3, 4);
        Position p2 = new Position(3, 5);
        assertNotEquals(p1, p2);
    }

    // 9. CONFIGLOADER

    @Test
    public void testConfigLoaderLoadsLevel1() {
        try {
            TheDOPOHardestGame game = ConfigLoader.loadConfig("level1.txt", GameMode.PLAYER, Skin.RED);
            assertNotNull(game);
            assertEquals(10, game.getRows());
            assertEquals(15, game.getCols());
            assertEquals(2, game.getCoins().size());
            assertEquals(2, game.getEnemies().size());
        } catch (GameException e) {
            fail("No debería lanzar excepción con level1.txt válido: " + e.getMessage());
        }
    }

    @Test
    public void testConfigLoaderThrowsOnInvalidFile() {
        assertThrows(GameException.class,
                () -> ConfigLoader.loadConfig("nonexistent_file.txt", GameMode.PLAYER, Skin.RED));
    }

    // 10. TABLERO (isValidPosition / getCell)

    @Test
    public void testIsValidPositionInsideBoard() {
        TheDOPOHardestGame game = basicGame(0, 0);
        assertTrue(game.isValidPosition(0, 0));
        assertTrue(game.isValidPosition(4, 4));
    }

    @Test
    public void testIsValidPositionOutsideBoard() {
        TheDOPOHardestGame game = basicGame(0, 0);
        assertFalse(game.isValidPosition(-1, 0));
        assertFalse(game.isValidPosition(5, 0));
        assertFalse(game.isValidPosition(0, 5));
    }

    @Test
    public void testGetCellReturnsCorrectType() {
        CellType[][] board = createEmptyBoard();
        board[2][2] = CellType.WALL;
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(0, 0),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        assertEquals(CellType.WALL, game.getCell(2, 2));
        assertEquals(CellType.EMPTY, game.getCell(0, 0));
    }
}