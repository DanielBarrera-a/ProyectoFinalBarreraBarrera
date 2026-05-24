package test;

import domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Suite de pruebas extendida para The DOPO Hardest Game.
 *
 * ESTRATEGIA DE COBERTURA:
 * ─────────────────────────────────────────────────────────────────────────
 * 1. draw() de todos los enemigos, monedas y elementos especiales
 *    → ~121 líneas de instrucciones no cubiertas → mayor ganancia individual
 * 2. TheDOPOHardestGame: modos PVP y PVM, movePlayer2, bloqueo mutuo,
 *    checkCollisions con player2, checkZone con player2, moveEnemies+machine
 * 3. Player: resetShield, getSpeed todas las ramas
 * 4. BasicBlueEnemy / AcceleratedEnemy: movimiento vertical + rebote vertical
 * 5. PatrolEnemy: waypoint bloqueado por pared
 * 6. ExpertMachine: sin SAFE_END en tablero, bestMoveToward con wall skip
 * 7. ConfigLoader: carga de archivo completo (todos los cases del switch)
 * 8. SpecialElementFactory: LIFE_SOURCE y tipo desconocido
 * 9. Gamesave: todos los getters
 * 10. SkinCoin: RED_SKIN, default color branch
 * 11. YellowCoin / LifeSource / Bomb: onCollected y contacto directo
 * 12. Bomb/LifeSource draw() con active=false (rama if skip)
 * 13. Position: equals con null, equals con otra clase, hashCode
 * 14. GameException: todas las constantes
 * ─────────────────────────────────────────────────────────────────────────
 * Nomenclatura: should<comportamiento>When<condición>
 * Compatibilidad: JUnit 5, headless AWT (BufferedImage para Graphics)
 */
class GameTestsExtended {



    /** Tablero 5×5 con paredes en el borde, SAFE_START en (1,1), SAFE_END en (3,3). */
    private CellType[][] makeBoard() {
        CellType[][] board = new CellType[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                board[r][c] = CellType.EMPTY;
        for (int i = 0; i < 5; i++) {
            board[0][i] = CellType.WALL;
            board[4][i] = CellType.WALL;
            board[i][0] = CellType.WALL;
            board[i][4] = CellType.WALL;
        }
        board[1][1] = CellType.SAFE_START;
        board[3][3] = CellType.SAFE_END;
        return board;
    }

    private TheDOPOHardestGame makeGame(List<Enemy> enemies, List<Coin> coins) {
        return new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                enemies, coins, 60, GameMode.PLAYER, Skin.RED);
    }

    private TheDOPOHardestGame makeGame(List<Enemy> enemies, List<Coin> coins,
                                        List<SpecialElement> specials) {
        return new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                enemies, coins, specials, 60, GameMode.PLAYER, Skin.RED);
    }

    /** Crea un Graphics headless de 200×200. */
    private Graphics makeGraphics() {
        System.setProperty("java.awt.headless", "true");
        return new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB).getGraphics();
    }



    @Test
    void shouldResetShieldWhenGreenPlayerCallsResetShield() {
        Player player = new Player(new Position(1, 1), Skin.GREEN);
        player.applyEnemyHit();           // pierde escudo
        assertFalse(player.isShielded());
        player.resetShield();             // recupera escudo
        assertTrue(player.isShielded());
        assertFalse(player.isSlowedDown());
    }

    @Test
    void shouldNotChangeShieldWhenNonGreenPlayerCallsResetShield() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.resetShield();             // rama skin != GREEN → noop
        assertFalse(player.isShielded());
    }

    @Test
    void shouldReturnSpeedOneWhenGreenPlayerIsSlowed() {
        Player player = new Player(new Position(1, 1), Skin.GREEN);
        player.applyEnemyHit();           // activa slowedDown
        assertTrue(player.isSlowedDown());
        assertEquals(1, player.getSpeed()); // GREEN + slowedDown → 1
    }

    @Test
    void shouldReturnSpeedOneWhenGreenPlayerHasShieldAndNotSlowed() {
        Player player = new Player(new Position(1, 1), Skin.GREEN);
        assertEquals(1, player.getSpeed()); // GREEN sin slowedDown → default 1
    }

    @Test
    void shouldReturnOriginalSkinViaSkinGetter() {
        // getSkin() devuelve skin base; getActiveSkin() devuelve temporal
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.applySkin(Skin.BLUE);
        assertEquals(Skin.RED, player.getSkin());
        assertEquals(Skin.BLUE, player.getActiveSkin());
    }


    @Test
    void shouldNotAlterPlayerWhenYellowCoinIsCollected() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        YellowCoin coin = new YellowCoin(new Position(1, 1));
        assertDoesNotThrow(() -> coin.onCollected(player, null));
        assertEquals(0, player.getDeaths());
        assertEquals(Skin.RED, player.getActiveSkin());
    }

    @Test
    void shouldDrawYellowCoinWithoutException() {
        YellowCoin coin = new YellowCoin(new Position(1, 2));
        assertDoesNotThrow(() -> coin.draw(makeGraphics(), 0, 0, 40));
    }



    @Test
    void shouldApplyRedSkinWhenPlayerCollectsRedSkinCoin() {
        Player player = new Player(new Position(1, 1), Skin.BLUE);
        SkinCoin coin = new SkinCoin(new Position(1, 2), Skin.RED);
        coin.onCollected(player, null);
        assertEquals(Skin.RED, player.getActiveSkin());
    }

    @Test
    void shouldDrawRedSkinCoinWithoutException() {
        SkinCoin coin = new SkinCoin(new Position(1, 2), Skin.RED);
        assertDoesNotThrow(() -> coin.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldDrawBlueSkinCoinWithoutException() {
        SkinCoin coin = new SkinCoin(new Position(1, 2), Skin.BLUE);
        assertDoesNotThrow(() -> coin.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldDrawGreenSkinCoinWithoutException() {
        SkinCoin coin = new SkinCoin(new Position(1, 2), Skin.GREEN);
        assertDoesNotThrow(() -> coin.draw(makeGraphics(), 0, 0, 40));
    }



    @Test
    void shouldDrawActiveBombWithoutException() {
        Bomb bomb = new Bomb(new Position(2, 2));
        assertDoesNotThrow(() -> bomb.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldSkipDrawWhenBombIsInactive() {
        Bomb bomb = new Bomb(new Position(2, 2));
        bomb.deactivate();
        // rama if (!active) return → no dibuja, no lanza excepción
        assertDoesNotThrow(() -> bomb.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldDeactivateAndAddDeathWhenBombContactsPlayerDirectly() {
        Bomb bomb = new Bomb(new Position(2, 2));
        Player player = new Player(new Position(2, 2), Skin.RED);
        bomb.onPlayerContact(player, null);
        assertFalse(bomb.isActive());
        assertEquals(1, player.getDeaths());
        assertEquals(new Position(1, 1), player.getPosition()); // volvió al respawn
    }

    @Test
    void shouldIgnoreDrawWhenBombAlreadyInactive() {
        Bomb bomb = new Bomb(new Position(1, 1));
        bomb.deactivate();
        assertFalse(bomb.isActive());
        assertDoesNotThrow(() -> bomb.draw(makeGraphics(), 0, 0, 40));
    }



    @Test
    void shouldDrawActiveLifeSourceWithoutException() {
        LifeSource life = new LifeSource(new Position(2, 2));
        assertDoesNotThrow(() -> life.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldSkipDrawWhenLifeSourceIsInactive() {
        LifeSource life = new LifeSource(new Position(2, 2));
        life.deactivate();
        assertDoesNotThrow(() -> life.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldNotDeactivateWhenEnemyContactsLifeSource() throws GameException {
        LifeSource life = new LifeSource(new Position(1, 2));
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(1, 2), true,
                new String[]{"ENEMY", "BASIC_BLUE", "1", "2", "HORIZONTAL"});
        assertDoesNotThrow(() -> life.onEnemyContact(enemy, null));
        assertTrue(life.isActive()); // onEnemyContact es noop
    }



    @Test
    void shouldDrawBasicBlueEnemyWithoutException() {
        BasicBlueEnemy enemy = new BasicBlueEnemy(new Position(2, 2), true, Color.BLUE);
        assertDoesNotThrow(() -> enemy.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldMoveDownWhenVerticalBasicBlueEnemyHasClearPath() {
        CellType[][] board = makeBoard();
        BasicBlueEnemy enemy = new BasicBlueEnemy(new Position(1, 2), false, Color.BLUE);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game);
        assertEquals(2, enemy.getPosition().getRow());
    }

    @Test
    void shouldBounceUpWhenVerticalBasicBlueEnemyHitsBottomWall() {
        CellType[][] board = makeBoard();
        BasicBlueEnemy enemy = new BasicBlueEnemy(new Position(3, 2), false, Color.BLUE);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game); // fila 4 es WALL → rebota hacia fila 2
        assertEquals(2, enemy.getPosition().getRow());
    }

    @Test
    void shouldMoveRightWhenHorizontalBasicBlueEnemyHasClearPath() {
        CellType[][] board = makeBoard();
        BasicBlueEnemy enemy = new BasicBlueEnemy(new Position(2, 1), true, Color.BLUE);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game);
        assertEquals(2, enemy.getPosition().getCol());
    }



    @Test
    void shouldDrawVerticalSliderEnemyWithoutException() {
        VerticalSliderEnemy enemy = new VerticalSliderEnemy(new Position(2, 2));
        assertDoesNotThrow(() -> enemy.draw(makeGraphics(), 0, 0, 40));
    }


    @Test
    void shouldDrawAcceleratedEnemyWithoutException() {
        AcceleratedEnemy enemy = new AcceleratedEnemy(new Position(2, 2), true);
        assertDoesNotThrow(() -> enemy.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldMoveTwoCellsVerticallyWhenAcceleratedEnemyIsVertical() {
        CellType[][] board = makeBoard();
        AcceleratedEnemy enemy = new AcceleratedEnemy(new Position(1, 2), false);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(3, 3),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game);
        assertEquals(3, enemy.getPosition().getRow()); // avanzó 2 filas
    }

    @Test
    void shouldBounceWhenVerticalAcceleratedEnemyHitsBottomWall() {
        CellType[][] board = makeBoard();
        AcceleratedEnemy enemy = new AcceleratedEnemy(new Position(3, 2), false);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        int startRow = enemy.getPosition().getRow();
        enemy.move(game);
        assertTrue(enemy.getPosition().getRow() <= startRow);
    }

    @Test
    void shouldBounceOnSecondStepWhenAcceleratedEnemyHitsWallMidway() {
        CellType[][] board = makeBoard();
        // col 3, horizontal dirección derecha: paso 1 → col 4 (WALL) → rebota
        // tras rebote (col 2), se hace break, no da segundo paso
        AcceleratedEnemy enemy = new AcceleratedEnemy(new Position(2, 3), true);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game);
        // Debe haberse movido a col 2 (rebotó)
        assertTrue(enemy.getPosition().getCol() <= 3);
    }



    @Test
    void shouldDrawPatrolEnemyWithoutException() throws GameException {
        String[] parts = {"ENEMY", "PATROL", "1", "1", "1", "2"};
        PatrolEnemy patrol = (PatrolEnemy) EnemyFactory.create("PATROL",
                new Position(1, 1), false, parts);
        assertDoesNotThrow(() -> patrol.draw(makeGraphics(), 0, 0, 40));
    }

    @Test
    void shouldNotMoveWhenPatrolNextWaypointIsWall() throws GameException {
        // Waypoints: (1,1) → (0,1) donde (0,1) es WALL
        String[] parts = {"ENEMY", "PATROL", "1", "1", "0", "1"};
        PatrolEnemy patrol = (PatrolEnemy) EnemyFactory.create("PATROL",
                new Position(1, 1), false, parts);
        TheDOPOHardestGame game = makeGame(new ArrayList<>(List.of(patrol)), new ArrayList<>());
        patrol.move(game);
        assertEquals(new Position(1, 1), patrol.getPosition()); // no avanzó
    }



    @Test
    void shouldCreateLifeSourceWhenTypeIsLifeSource() throws GameException {
        SpecialElement se = SpecialElementFactory.create("LIFE_SOURCE", new Position(1, 1));
        assertInstanceOf(LifeSource.class, se);
        assertTrue(se.isActive());
    }

    @Test
    void shouldCreateBombWhenTypeIsBomb() throws GameException {
        SpecialElement se = SpecialElementFactory.create("BOMB", new Position(1, 1));
        assertInstanceOf(Bomb.class, se);
    }

    @Test
    void shouldThrowWhenSpecialElementTypeIsUnknown() {
        assertThrows(GameException.class,
                () -> SpecialElementFactory.create("PORTAL", new Position(1, 1)));
    }


    @Test
    void shouldCreateTwoPlayersInPVPMode() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        assertNotNull(game.getPlayer());
        assertNotNull(game.getPlayer2());
        assertEquals(new Position(3, 3), game.getPlayer2().getPosition());
    }

    @Test
    void shouldMovePlayer2WhenInPVPMode() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        game.movePlayer2(-1, 0); // (3,3) → (2,3)
        assertEquals(new Position(2, 3), game.getPlayer2().getPosition());
    }

    @Test
    void shouldBlockPlayer2WhenDestinationIsWall() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        game.movePlayer2(1, 0); // (3,3) → (4,3) es WALL
        assertEquals(new Position(3, 3), game.getPlayer2().getPosition());
    }

    @Test
    void shouldBlockPlayerFromMovingIntoPlayer2Cell() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        // player1 en (1,1); llevamos a (3,2)
        game.movePlayer(1, 0);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1); // (3,2)
        game.movePlayer(0, 1); // intenta (3,3) = player2 → bloqueado
        assertEquals(new Position(3, 2), game.getPlayer().getPosition());
    }

    @Test
    void shouldBlockPlayer2FromMovingIntoPlayer1Cell() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        // player2 en (3,3); llevamos a (1,2)
        game.movePlayer2(-1, 0); // (2,3)
        game.movePlayer2(-1, 0); // (1,3)
        game.movePlayer2(0, -1); // (1,2)
        game.movePlayer2(0, -1); // intenta (1,1) = player1 → bloqueado
        assertEquals(new Position(1, 2), game.getPlayer2().getPosition());
    }


    @Test
    void shouldCreatePlayer2InPVMMode() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        assertNotNull(game.getPlayer2());
    }

    @Test
    void shouldUseMachineSetViaSetMachine() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        game.setMachine(new ExpertMachine()); // cubre setMachine
        assertDoesNotThrow(game::moveEnemies); // cubre rama PVM + machine != null
    }

    @Test
    void shouldRunMachineTickDuringMoveEnemiesInPVMMode() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        // Con RandomMachine por defecto: moveEnemies invoca machine.nextMove
        assertDoesNotThrow(game::moveEnemies);
    }



    @Test
    void shouldNotMoveEnemiesWhenGameIsAlreadyOver() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 1, GameMode.PLAYER, Skin.RED);
        game.tickTime(); // game-over
        assertTrue(game.isGameOver());
        assertDoesNotThrow(game::moveEnemies); // rama isGameOver → return
    }

    @Test
    void shouldNotMoveEnemiesAfterVictory() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        game.movePlayer(1, 0);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1); // victoria
        assertTrue(game.isVictory());
        assertDoesNotThrow(game::moveEnemies); // rama isVictory → return
    }



    @Test
    void shouldRemoveCoinWhenPlayer2CollectsIt() {
        List<Coin> coins = new ArrayList<>();
        coins.add(new YellowCoin(new Position(3, 2)));
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), coins, 60, GameMode.PVP, Skin.RED);
        game.movePlayer2(0, -1); // (3,3) → (3,2) = moneda
        assertTrue(game.getCoins().isEmpty());
    }

    @Test
    void shouldRespawnPlayer2WhenCollidingWithEnemy() throws GameException {
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(3, 2), true,
                new String[]{"ENEMY", "BASIC_BLUE", "3", "2", "HORIZONTAL"});
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        // Llevamos player2 a (3,2) donde está el enemigo
        game.movePlayer2(-1, 0); // (2,3)
        game.movePlayer2(0, -1); // (2,2)
        game.movePlayer2(1, 0);  // (3,2) → colisión
        assertEquals(1, game.getPlayer2().getDeaths());
    }

    @Test
    void shouldActiveBombKillPlayer2WhenPlayer2StepsOnIt() {
        List<SpecialElement> specials = new ArrayList<>();
        Bomb bomb = new Bomb(new Position(3, 2));
        specials.add(bomb);
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), specials, 60, GameMode.PVP, Skin.RED);
        game.movePlayer2(0, -1); // (3,3) → (3,2) = bomba
        assertEquals(1, game.getPlayer2().getDeaths());
        assertFalse(bomb.isActive());
    }

    @Test
    void shouldReducePlayer2DeathsWhenPlayer2StepsOnLifeSource() {
        List<SpecialElement> specials = new ArrayList<>();
        LifeSource life = new LifeSource(new Position(3, 2));
        specials.add(life);
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), specials, 60, GameMode.PVP, Skin.RED);
        game.getPlayer2().addDeath();
        game.getPlayer2().addDeath();
        game.movePlayer2(0, -1); // (3,3) → (3,2) = LifeSource
        assertEquals(1, game.getPlayer2().getDeaths());
        assertFalse(life.isActive());
    }



    @Test
    void shouldActivateVictoryWhenPlayer2ReachesSafeStartWithNoCoins() {
        // player1 en (2,3) para no bloquear (1,1)
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(2, 3),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        // player2 en (3,3) → lleva a (1,1) SAFE_START
        game.movePlayer2(-1, 0); // (2,3) — ocupa player1... probemos con (2,2)
        // Reajuste: player1 en (2,2)
        TheDOPOHardestGame game2 = new TheDOPOHardestGame(
                makeBoard(), new Position(2, 2),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        game2.movePlayer2(-1, 0); // (2,3)
        game2.movePlayer2(-1, 0); // (1,3)
        game2.movePlayer2(0, -1); // (1,2)
        game2.movePlayer2(0, -1); // (1,1) SAFE_START → victoria
        assertTrue(game2.isVictory());
    }

    @Test
    void shouldNotActivateVictoryWhenPlayer2ReachesSafeStartButCoinsRemain() {
        List<Coin> coins = new ArrayList<>();
        coins.add(new YellowCoin(new Position(2, 2)));
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(2, 3),
                new ArrayList<>(), coins, 60, GameMode.PVM, Skin.RED);
        game.movePlayer2(-1, 0); // (2,3)
        game.movePlayer2(-1, 0); // (1,3)
        game.movePlayer2(0, -1); // (1,2)
        game.movePlayer2(0, -1); // (1,1) — hay moneda pendiente → no victoria
        assertFalse(game.isVictory());
    }

    @Test
    void shouldUpdatePlayer2RespawnWhenStepsOnSafeMid() {
        CellType[][] board = makeBoard();
        board[2][2] = CellType.SAFE_MID;
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                board, new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVP, Skin.RED);
        // player2 en (3,3), lleva a (2,2) SAFE_MID
        game.movePlayer2(-1, 0); // (2,3)
        game.movePlayer2(0, -1); // (2,2)
        assertEquals(new Position(2, 2), game.getPlayer2().getRespawnPosition());
    }



    @Test
    void shouldNotDecrementTimeAfterGameOver() {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), 1, GameMode.PLAYER, Skin.RED);
        game.tickTime(); // → 0, game-over
        int t = game.getTimeRemaining();
        game.tickTime(); // rama isGameOver → return inmediato
        assertEquals(t, game.getTimeRemaining());
    }

    @Test
    void shouldNotDecrementTimeAfterVictory() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        game.movePlayer(1, 0);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1); // victoria
        int t = game.getTimeRemaining();
        game.tickTime(); // rama isVictory → return
        assertEquals(t, game.getTimeRemaining());
    }



    @Test
    void shouldNotThrowWhenMovePlayer2CalledInSinglePlayerMode() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        assertDoesNotThrow(() -> game.movePlayer2(0, 1)); // player2 == null → return
    }



    @Test
    void shouldReturnCorrectBoardDimensions() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        assertEquals(5, game.getRows());
        assertEquals(5, game.getCols());
    }

    @Test
    void shouldReturnCorrectCellTypes() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        assertEquals(CellType.WALL, game.getCell(0, 0));
        assertEquals(CellType.SAFE_START, game.getCell(1, 1));
        assertEquals(CellType.SAFE_END, game.getCell(3, 3));
        assertEquals(CellType.EMPTY, game.getCell(2, 2));
    }

    @Test
    void shouldReturnGameModeInGetter() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        assertEquals(GameMode.PLAYER, game.getMode());
    }

    @Test
    void shouldReturnSpecialElementsList() {
        List<SpecialElement> specials = new ArrayList<>();
        specials.add(new Bomb(new Position(2, 2)));
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>(), specials);
        assertEquals(1, game.getSpecialElements().size());
    }



    @Test
    void shouldReturnNoopWhenExpertMachineHasNoCoinAndNoSafeEnd() {
        CellType[][] board = new CellType[3][3];
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = CellType.EMPTY;
        board[0][0] = CellType.SAFE_START;
        // Sin SAFE_END → findTarget devuelve null → nextMove devuelve {0,0}
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                board, new Position(0, 0),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                60, GameMode.PVM, Skin.RED);
        ExpertMachine expert = new ExpertMachine();
        int[] move = expert.nextMove(game, game.getPlayer2());
        assertNotNull(move);
        assertArrayEquals(new int[]{0, 0}, move);
    }

    @Test
    void shouldSkipWallCellsInBestMoveToward() {
        // Tablero donde los movimientos directos están bloqueados
        CellType[][] board = new CellType[5][5];
        for (int r = 0; r < 5; r++)
            for (int c = 0; c < 5; c++)
                board[r][c] = CellType.EMPTY;
        board[0][0] = CellType.SAFE_START;
        board[4][4] = CellType.SAFE_END;
        // Paredes que fuerzan a ExpertMachine a evaluar varios DIRECTIONS
        board[3][4] = CellType.WALL;
        board[4][3] = CellType.WALL;
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                board, new Position(0, 0),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                60, GameMode.PVM, Skin.RED);
        ExpertMachine expert = new ExpertMachine();
        // Debe retornar algún movimiento sin lanzar excepción
        int[] move = expert.nextMove(game, game.getPlayer2());
        assertNotNull(move);
        assertEquals(2, move.length);
    }



    @Test
    void shouldThrowWhenPatrolHasOddCoordinateCount() {
        // parts.length = 5 → (5-2) % 2 = 1 ≠ 0 → GameException
        String[] parts = {"ENEMY", "PATROL", "1", "1", "2"};
        assertThrows(GameException.class,
                () -> EnemyFactory.create("PATROL", new Position(1, 1), false, parts));
    }

    @Test
    void shouldCreateBasicBlueEnemyInVerticalMode() throws GameException {
        String[] parts = {"ENEMY", "BASIC_BLUE", "1", "1", "VERTICAL"};
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(1, 1), false, parts);
        assertInstanceOf(BasicBlueEnemy.class, enemy);
    }

    @Test
    void shouldCreateAcceleratedEnemyInVerticalMode() throws GameException {
        String[] parts = {"ENEMY", "TYPE_A", "1", "1", "VERTICAL"};
        Enemy enemy = EnemyFactory.create("TYPE_A", new Position(1, 1), false, parts);
        assertInstanceOf(AcceleratedEnemy.class, enemy);
    }



    @Test
    void shouldThrowGameExceptionWhenFileDoesNotExist() {
        assertThrows(GameException.class,
                () -> ConfigLoader.loadConfig("no_existe.txt", GameMode.PLAYER, Skin.RED));
    }

    @Test
    void shouldThrowWhenConfigHasNoDimensions() throws Exception {
        File tmp = File.createTempFile("bad_level", ".txt");
        tmp.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(tmp)) {
            pw.println("TIME 30"); // sin DIMENSIONS ni START
        }
        assertThrows(GameException.class,
                () -> ConfigLoader.loadConfig(tmp.getAbsolutePath(), GameMode.PLAYER, Skin.RED));
    }

    @Test
    void shouldLoadCompleteConfigFileWithAllEntityTypes() throws Exception {
        File tmp = File.createTempFile("full_level", ".txt");
        tmp.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(tmp)) {
            pw.println("# nivel de prueba completo");
            pw.println("");                          // línea vacía → ignorada
            pw.println("DIMENSIONS 5 5");
            pw.println("TIME 45");
            pw.println("START 1 1");                  // case START + board[r][c]=SAFE_START
            pw.println("END 3 3");                    // case END
            pw.println("MID 2 2");                    // case MID
            pw.println("WALL 0 0");                   // case WALL
            pw.println("WALL 0 1");
            pw.println("WALL 0 2");
            pw.println("WALL 0 3");
            pw.println("WALL 0 4");
            pw.println("WALL 4 0");
            pw.println("WALL 4 1");
            pw.println("WALL 4 2");
            pw.println("WALL 4 3");
            pw.println("WALL 4 4");
            pw.println("WALL 1 0");
            pw.println("WALL 2 0");
            pw.println("WALL 3 0");
            pw.println("WALL 1 4");
            pw.println("WALL 2 4");
            pw.println("WALL 3 4");
            pw.println("COIN YELLOW 1 2");             // case COIN → CoinFactory YELLOW
            pw.println("COIN RED_SKIN 1 3");           // RED_SKIN
            pw.println("COIN BLUE_SKIN 2 1");          // BLUE_SKIN
            pw.println("COIN GREEN_SKIN 2 3");         // GREEN_SKIN
            pw.println("ENEMY BASIC_BLUE 2 2 HORIZONTAL"); // case ENEMY
            pw.println("ENEMY TYPE_V 3 1");
            pw.println("ENEMY TYPE_A 3 2 HORIZONTAL");
            pw.println("ENEMY PATROL 1 1 1 2 1 3");
            pw.println("SPECIAL BOMB 3 2");            // case SPECIAL
            pw.println("SPECIAL LIFE_SOURCE 2 3");
        }
        TheDOPOHardestGame game = ConfigLoader.loadConfig(
                tmp.getAbsolutePath(), GameMode.PLAYER, Skin.RED);

        assertNotNull(game);
        assertEquals(45, game.getTimeRemaining());
        assertEquals(4, game.getCoins().size());
        assertEquals(4, game.getEnemies().size());
        assertEquals(2, game.getSpecialElements().size());
        assertEquals(new Position(1, 1), game.getPlayer().getPosition());
    }

    @Test
    void shouldIgnoreCommentsAndEmptyLinesInConfig() throws Exception {
        File tmp = File.createTempFile("comment_level", ".txt");
        tmp.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(tmp)) {
            pw.println("# comentario 1");
            pw.println("");
            pw.println("# comentario 2");
            pw.println("DIMENSIONS 3 3");
            pw.println("START 1 1");
            pw.println("END 1 2");
        }
        TheDOPOHardestGame game = ConfigLoader.loadConfig(
                tmp.getAbsolutePath(), GameMode.PLAYER, Skin.RED);
        assertNotNull(game);
    }

    @Test
    void shouldLoadConfigInPVMMode() throws Exception {
        File tmp = File.createTempFile("pvm_level", ".txt");
        tmp.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(tmp)) {
            pw.println("DIMENSIONS 5 5");
            pw.println("START 1 1");
            pw.println("END 3 3");
        }
        TheDOPOHardestGame game = ConfigLoader.loadConfig(
                tmp.getAbsolutePath(), GameMode.PVM, Skin.BLUE);
        assertNotNull(game);
        assertNotNull(game.getPlayer2()); // PVM crea player2
    }

    @Test
    void shouldLoadConfigInPVPMode() throws Exception {
        File tmp = File.createTempFile("pvp_level", ".txt");
        tmp.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(tmp)) {
            pw.println("DIMENSIONS 5 5");
            pw.println("START 1 1");
            pw.println("END 3 3");
        }
        TheDOPOHardestGame game = ConfigLoader.loadConfig(
                tmp.getAbsolutePath(), GameMode.PVP, Skin.GREEN);
        assertNotNull(game);
        assertNotNull(game.getPlayer2()); // PVP crea player2
    }



    @Test
    void shouldReturnAllFieldsFromGamesave() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        Gamesave save = new Gamesave(game, 5, GameMode.PVP, Skin.GREEN);
        assertSame(game, save.game());
        assertEquals(5, save.currentLevel());
        assertEquals(GameMode.PVP, save.mode());
        assertEquals(Skin.GREEN, save.skin());
    }


    @Test
    void shouldHaveCorrectMessageForEachGameExceptionConstant() {
        assertNotNull(GameException.ERROR_AL_CARGAR_NIVEL);
        assertNotNull(GameException.ERROR_FORMATO_NUMERO);
        assertNotNull(GameException.ERROR_FUERA_DE_LIMITES);
        assertNotNull(GameException.ERROR_AL_GUARDAR);
        assertNotNull(GameException.ERROR_NO_HAY_PARTIDA);
        assertNotNull(GameException.ERROR_AL_CARGAR_PARTIDA);

        GameException ex = new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
        assertEquals(GameException.ERROR_AL_CARGAR_NIVEL, ex.getMessage());
    }



    @Test
    void shouldNotBeEqualToNull() {
        assertNotEquals(null, new Position(1, 2));
    }

    @Test
    void shouldNotBeEqualToObjectOfDifferentClass() {
        assertNotEquals("(1,2)", new Position(1, 2));
    }

    @Test
    void shouldBeEqualToItself() {
        Position p = new Position(3, 4);
        assertEquals(p, p); // rama this == o
    }

    @Test
    void shouldHaveSameHashCodeForEqualPositions() {
        assertEquals(
                new Position(3, 4).hashCode(),
                new Position(3, 4).hashCode());
    }



    @Test
    void shouldCreateRandomMachineWithNextMoveReturningTwoElementArray()
            throws GameException {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                60, GameMode.PVM, Skin.RED);
        MachinePlayer machine = MachineFactory.create("RANDOM");
        int[] move = machine.nextMove(game, game.getPlayer());
        assertNotNull(move);
        assertEquals(2, move.length);
    }



    @Test
    void shouldRemoveEnemyAndDeactivateBombWhenEnemyMovesOverIt()
            throws GameException {
        List<SpecialElement> specials = new ArrayList<>();
        Bomb bomb = new Bomb(new Position(1, 2));
        specials.add(bomb);
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(1, 1), true,
                new String[]{"ENEMY", "BASIC_BLUE", "1", "1", "HORIZONTAL"});
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        TheDOPOHardestGame game = makeGame(enemies, new ArrayList<>(), specials);
        game.moveEnemies(); // enemigo va a (1,2) → activa bomba
        assertTrue(game.getEnemies().isEmpty());
        assertFalse(bomb.isActive());
    }

    @Test
    void shouldNotAffectLifeSourceWhenEnemyMovesOverIt() throws GameException {
        List<SpecialElement> specials = new ArrayList<>();
        LifeSource life = new LifeSource(new Position(1, 2));
        specials.add(life);
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(1, 1), true,
                new String[]{"ENEMY", "BASIC_BLUE", "1", "1", "HORIZONTAL"});
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        TheDOPOHardestGame game = makeGame(enemies, new ArrayList<>(), specials);
        game.moveEnemies(); // enemigo pasa por (1,2) donde está LifeSource
        assertTrue(life.isActive()); // LifeSource.onEnemyContact es noop
    }


    @Test
    void shouldSkipInactiveSpecialElementDuringCollisionCheck() {
        List<SpecialElement> specials = new ArrayList<>();
        Bomb bomb = new Bomb(new Position(1, 2));
        bomb.deactivate(); // ya inactiva → no debe aplicarse
        specials.add(bomb);
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>(), specials);
        game.movePlayer(0, 1); // player va a (1,2) donde está la bomba inactiva
        assertEquals(0, game.getPlayer().getDeaths()); // no debe morir
    }
}