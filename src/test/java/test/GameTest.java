package test;

import domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Suite de pruebas unitarias para The DOPO Hardest Game.
 * Nomenclatura: should + comportamiento esperado + condición.
 * Orientada a cobertura de ramas con JaCoCo sobre el paquete domain.
 */
class GameTests {

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
                enemies, coins, 60, GameMode.PLAYER, Skin.RED
        );
    }

    private TheDOPOHardestGame makeGameWithTime(int seconds) {
        return new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(),
                seconds, GameMode.PLAYER, Skin.RED
        );
    }

    // Player

    @Test
    void shouldHaveZeroDeathsWhenPlayerIsCreated() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        assertEquals(0, player.getDeaths());
    }

    @Test
    void shouldIncrementDeathsWhenAddDeathIsCalled() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.addDeath();
        player.addDeath();
        assertEquals(2, player.getDeaths());
    }

    @Test
    void shouldDecrementDeathsWhenReduceDeathIsCalledWithDeathsAboveZero() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.addDeath();
        player.addDeath();
        player.reduceDeath();
        assertEquals(1, player.getDeaths());
    }

    @Test
    void shouldNotGoBelowZeroWhenReduceDeathIsCalledWithZeroDeaths() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.reduceDeath();
        assertEquals(0, player.getDeaths());
    }

    @Test
    void shouldReturnSpeedTwoWhenSkinIsBlue() {
        Player player = new Player(new Position(1, 1), Skin.BLUE);
        assertEquals(2, player.getSpeed());
    }

    @Test
    void shouldReturnSpeedOneWhenSkinIsRed() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        assertEquals(1, player.getSpeed());
    }

    @Test
    void shouldAbsorbHitAndNotDieWhenGreenPlayerHasShield() {
        Player player = new Player(new Position(1, 1), Skin.GREEN);
        boolean died = player.applyEnemyHit();
        assertFalse(died);
        assertEquals(0, player.getDeaths());
    }

    @Test
    void shouldDieWhenGreenPlayerHasNoShieldAndTakesHit() {
        Player player = new Player(new Position(1, 1), Skin.GREEN);
        player.applyEnemyHit();
        boolean died = player.applyEnemyHit();
        assertTrue(died);
        assertEquals(1, player.getDeaths());
    }

    @Test
    void shouldDieWhenRedPlayerTakesHit() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        boolean died = player.applyEnemyHit();
        assertTrue(died);
        assertEquals(1, player.getDeaths());
    }

    @Test
    void shouldApplyTemporarySkinWhenSkinCoinIsCollected() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.applySkin(Skin.BLUE);
        assertEquals(Skin.BLUE, player.getActiveSkin());
    }

    @Test
    void shouldRestoreOriginalSkinWhenResetSkinIsCalled() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.applySkin(Skin.GREEN);
        player.resetSkin();
        assertEquals(Skin.RED, player.getActiveSkin());
    }

    // CoinFactory

    @Test
    void shouldCreateYellowCoinWhenTypeIsYellow() throws GameException {
        Coin coin = CoinFactory.create("YELLOW", new Position(1, 1));
        assertInstanceOf(YellowCoin.class, coin);
    }

    @Test
    void shouldCreateSkinCoinWhenTypeIsRedSkin() throws GameException {
        Coin coin = CoinFactory.create("RED_SKIN", new Position(1, 1));
        assertInstanceOf(SkinCoin.class, coin);
    }

    @Test
    void shouldThrowGameExceptionWhenCoinTypeIsUnknown() {
        assertThrows(GameException.class,
                () -> CoinFactory.create("UNKNOWN", new Position(1, 1)));
    }

    // EnemyFactory

    @Test
    void shouldCreatePatrolEnemyWhenTypeIsPatrol() throws GameException {
        String[] parts = {"ENEMY", "PATROL", "1", "1", "1", "3", "3", "3", "3", "1"};
        Enemy enemy = EnemyFactory.create("PATROL", new Position(1, 1), false, parts);
        assertInstanceOf(PatrolEnemy.class, enemy);
    }

    @Test
    void shouldThrowGameExceptionWhenEnemyTypeIsUnknown() {
        assertThrows(GameException.class,
                () -> EnemyFactory.create("GHOST", new Position(1, 1), false, new String[]{}));
    }

    // MachineFactory

    @Test
    void shouldCreateRandomMachineWhenTypeIsRandom() throws GameException {
        MachinePlayer machine = MachineFactory.create("RANDOM");
        assertInstanceOf(RandomMachine.class, machine);
    }

    @Test
    void shouldCreateExpertMachineWhenTypeIsExpert() throws GameException {
        MachinePlayer machine = MachineFactory.create("EXPERT");
        assertInstanceOf(ExpertMachine.class, machine);
    }

    @Test
    void shouldThrowGameExceptionWhenMachineTypeIsUnknown() {
        assertThrows(GameException.class, () -> MachineFactory.create("GODMODE"));
    }

    // TheDOPOHardestGame

    @Test
    void shouldNotMovePlayerWhenDestinationIsAWall() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        game.movePlayer(-1, 0);
        assertEquals(new Position(1, 1), game.getPlayer().getPosition());
    }

    @Test
    void shouldActivateGameOverWhenTimeReachesZero() {
        TheDOPOHardestGame game = makeGameWithTime(1);
        game.tickTime();
        assertTrue(game.isGameOver());
    }

    @Test
    void shouldActivateVictoryWhenPlayerReachesSafeEndWithNoCoins() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        game.movePlayer(1, 0);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertTrue(game.isVictory());
    }

    @Test
    void shouldRemoveCoinWhenPlayerCollectsIt() {
        List<Coin> coins = new ArrayList<>();
        coins.add(new YellowCoin(new Position(1, 2)));
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), coins);
        game.movePlayer(0, 1);
        assertTrue(game.getCoins().isEmpty());
    }

    @Test
    void shouldTeleportPlayerToRespawnWhenCollidingWithEnemy() throws GameException {
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(EnemyFactory.create("BASIC_BLUE", new Position(1, 2), true,
                new String[]{"ENEMY", "BASIC_BLUE", "1", "2", "HORIZONTAL"}));
        TheDOPOHardestGame game = makeGame(enemies, new ArrayList<>());
        game.movePlayer(0, 1);
        assertEquals(new Position(1, 1), game.getPlayer().getPosition());
    }

    @Test
    void shouldReturnFalseWhenPositionIsOutsideBoardBounds() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        assertFalse(game.isValidPosition(-1, 0));
        assertFalse(game.isValidPosition(0, -1));
        assertFalse(game.isValidPosition(10, 0));
        assertFalse(game.isValidPosition(0, 10));
    }

    @Test
    void shouldUpdateRespawnWhenPlayerStepsOnSafeMidZone() {
        CellType[][] board = makeBoard();
        board[2][2] = CellType.SAFE_MID;
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                board, new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(),
                60, GameMode.PLAYER, Skin.RED
        );
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        assertEquals(new Position(2, 2), game.getPlayer().getRespawnPosition());
    }

    @Test
    void shouldReturnMoveWhenRandomMachineIsAsked() throws GameException {
        MachinePlayer machine = MachineFactory.create("RANDOM");
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), 60, GameMode.PVM, Skin.RED
        );
        int[] move = machine.nextMove(game, game.getPlayer());
        assertNotNull(move);
        assertEquals(2, move.length);
    }

    @Test
    void shouldPatrolEnemyFollowWaypointsInOrder() throws GameException {
        String[] parts = {"ENEMY", "PATROL", "1", "1", "1", "2", "1", "3"};
        PatrolEnemy patrol = (PatrolEnemy) EnemyFactory.create("PATROL",
                new Position(1, 1), false, parts);
        TheDOPOHardestGame game = makeGame(new ArrayList<>(List.of(patrol)), new ArrayList<>());
        Position before = new Position(patrol.getPosition().getRow(), patrol.getPosition().getCol());
        patrol.move(game);
        assertNotEquals(before, patrol.getPosition());
    }
}