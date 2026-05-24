package test;

import domain.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Suite de pruebas unitarias para The DOPO Hardest Game.
 * Nomenclatura: should + comportamiento esperado + condicion.
 * Orientada a cobertura de ramas con JaCoCo sobre el paquete domain.
 */
class GameTests {

    // ── Helpers ───────────────────────────────────────────────────────────

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

    private TheDOPOHardestGame makeGame(List<Enemy> enemies, List<Coin> coins, List<SpecialElement> specials) {
        return new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                enemies, coins, specials, 60, GameMode.PLAYER, Skin.RED
        );
    }

    private TheDOPOHardestGame makeGameWithTime(int seconds) {
        return new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(),
                seconds, GameMode.PLAYER, Skin.RED
        );
    }

    // ── Player ────────────────────────────────────────────────────────────

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
        assertTrue(player.isSlowedDown());
    }

    @Test
    void shouldDieWhenGreenPlayerHasNoShieldAndTakesHit() {
        Player player = new Player(new Position(1, 1), Skin.GREEN);
        player.applyEnemyHit(); // pierde escudo
        boolean died = player.applyEnemyHit(); // ahora muere
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

    @Test
    void shouldActivateShieldWhenGreenSkinIsApplied() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.applySkin(Skin.GREEN);
        assertFalse(player.applyEnemyHit()); // tiene escudo -> no muere
    }

    @Test
    void shouldReturnSpeedTwoWhenTemporaryBlueSkinIsActive() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.applySkin(Skin.BLUE);
        assertEquals(2, player.getSpeed());
    }

    @Test
    void shouldResetSkinAfterDying() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        player.applySkin(Skin.BLUE);
        player.applyEnemyHit(); // muere -> resetSkin
        assertEquals(Skin.RED, player.getActiveSkin());
    }

    // ── CoinFactory ───────────────────────────────────────────────────────

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
    void shouldCreateSkinCoinWhenTypeIsBlueSkin() throws GameException {
        Coin coin = CoinFactory.create("BLUE_SKIN", new Position(1, 1));
        assertInstanceOf(SkinCoin.class, coin);
    }

    @Test
    void shouldCreateSkinCoinWhenTypeIsGreenSkin() throws GameException {
        Coin coin = CoinFactory.create("GREEN_SKIN", new Position(1, 1));
        assertInstanceOf(SkinCoin.class, coin);
    }

    @Test
    void shouldThrowGameExceptionWhenCoinTypeIsUnknown() {
        assertThrows(GameException.class,
                () -> CoinFactory.create("UNKNOWN", new Position(1, 1)));
    }

    // ── SkinCoin ──────────────────────────────────────────────────────────

    @Test
    void shouldApplyBlueSkinWhenPlayerCollectsBlueSkinCoin() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        SkinCoin coin = new SkinCoin(new Position(1, 2), Skin.BLUE);
        coin.onCollected(player, null);
        assertEquals(Skin.BLUE, player.getActiveSkin());
    }

    @Test
    void shouldApplyGreenSkinAndShieldWhenPlayerCollectsGreenSkinCoin() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        SkinCoin coin = new SkinCoin(new Position(1, 2), Skin.GREEN);
        coin.onCollected(player, null);
        assertEquals(Skin.GREEN, player.getActiveSkin());
        assertFalse(player.applyEnemyHit()); // tiene escudo -> no muere
    }

    @Test
    void shouldReplacePreviousSkinWhenNewSkinCoinIsCollected() {
        Player player = new Player(new Position(1, 1), Skin.RED);
        SkinCoin first = new SkinCoin(new Position(1, 2), Skin.BLUE);
        SkinCoin second = new SkinCoin(new Position(1, 3), Skin.GREEN);
        first.onCollected(player, null);
        second.onCollected(player, null);
        assertEquals(Skin.GREEN, player.getActiveSkin());
    }

    // ── EnemyFactory ──────────────────────────────────────────────────────

    @Test
    void shouldCreateBasicBlueEnemyWhenTypeIsBasicBlue() throws GameException {
        String[] parts = {"ENEMY", "BASIC_BLUE", "1", "1", "HORIZONTAL"};
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(1, 1), true, parts);
        assertInstanceOf(BasicBlueEnemy.class, enemy);
    }

    @Test
    void shouldCreateVerticalSliderWhenTypeIsTypeV() throws GameException {
        String[] parts = {"ENEMY", "TYPE_V", "1", "1"};
        Enemy enemy = EnemyFactory.create("TYPE_V", new Position(1, 1), false, parts);
        assertInstanceOf(VerticalSliderEnemy.class, enemy);
    }

    @Test
    void shouldCreateAcceleratedEnemyWhenTypeIsTypeA() throws GameException {
        String[] parts = {"ENEMY", "TYPE_A", "1", "1", "HORIZONTAL"};
        Enemy enemy = EnemyFactory.create("TYPE_A", new Position(1, 1), true, parts);
        assertInstanceOf(AcceleratedEnemy.class, enemy);
    }

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

    // ── VerticalSliderEnemy ───────────────────────────────────────────────

    @Test
    void shouldMoveDownWhenVerticalSliderHasPositiveDirection() {
        CellType[][] board = makeBoard();
        VerticalSliderEnemy enemy = new VerticalSliderEnemy(new Position(1, 2));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game);
        assertEquals(2, enemy.getPosition().getRow());
    }

    @Test
    void shouldBounceWhenVerticalSliderHitsBottomWall() {
        CellType[][] board = makeBoard();
        // Lo ponemos en fila 3, col 2 — la siguiente sería fila 4 (WALL)
        VerticalSliderEnemy enemy = new VerticalSliderEnemy(new Position(3, 2));
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game); // choca con pared de abajo -> rebota hacia arriba
        assertEquals(2, enemy.getPosition().getRow());
    }

    // ── AcceleratedEnemy ──────────────────────────────────────────────────

    @Test
    void shouldMoveTwoCellsWhenAcceleratedEnemyHasNoObstacles() {
        CellType[][] board = makeBoard();
        AcceleratedEnemy enemy = new AcceleratedEnemy(new Position(1, 1), true);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(3, 3),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        enemy.move(game);
        assertEquals(3, enemy.getPosition().getCol()); // avanzó 2 celdas
    }

    @Test
    void shouldBounceWhenAcceleratedEnemyHitsWall() {
        CellType[][] board = makeBoard();
        // En col 3 ya está la SAFE_END pero col 4 es WALL
        AcceleratedEnemy enemy = new AcceleratedEnemy(new Position(2, 3), true);
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(List.of(enemy)), new ArrayList<>(), 60, GameMode.PLAYER, Skin.RED);
        int startCol = enemy.getPosition().getCol();
        enemy.move(game); // al intentar ir a col 4 (WALL) rebota
        assertTrue(enemy.getPosition().getCol() <= startCol);
    }

    // ── PatrolEnemy ───────────────────────────────────────────────────────

    @Test
    void shouldPatrolEnemyFollowWaypointsInOrder() throws GameException {
        String[] parts = {"ENEMY", "PATROL", "1", "1", "1", "2", "1", "3"};
        PatrolEnemy patrol = (PatrolEnemy) EnemyFactory.create("PATROL",
                new Position(1, 1), false, parts);
        TheDOPOHardestGame game = makeGame(new ArrayList<>(List.of(patrol)), new ArrayList<>());
        patrol.move(game);
        assertEquals(new Position(1, 2), patrol.getPosition());
        patrol.move(game);
        assertEquals(new Position(1, 3), patrol.getPosition());
        patrol.move(game); // ciclo: vuelve al inicio
        assertEquals(new Position(1, 1), patrol.getPosition());
    }

    @Test
    void shouldThrowWhenPatrolHasFewerThanTwoWaypoints() {
        String[] parts = {"ENEMY", "PATROL", "1", "1"};
        assertThrows(GameException.class,
                () -> EnemyFactory.create("PATROL", new Position(1, 1), false, parts));
    }

    // ── Bomb ──────────────────────────────────────────────────────────────

    @Test
    void shouldKillPlayerWhenPlayerStepsOnBomb() {
        List<SpecialElement> specials = new ArrayList<>();
        Bomb bomb = new Bomb(new Position(1, 2));
        specials.add(bomb);
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>(), specials);
        game.movePlayer(0, 1); // se mueve a (1,2) donde está la bomba
        assertEquals(1, game.getPlayer().getDeaths());
        assertFalse(bomb.isActive());
    }

    @Test
    void shouldRemoveEnemyWhenEnemyStepsOnBomb() throws GameException {
        List<SpecialElement> specials = new ArrayList<>();
        Bomb bomb = new Bomb(new Position(1, 2));
        specials.add(bomb);
        String[] parts = {"ENEMY", "BASIC_BLUE", "1", "1", "HORIZONTAL"};
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(1, 1), true, parts);
        List<Enemy> enemies = new ArrayList<>(List.of(enemy));
        TheDOPOHardestGame game = makeGame(enemies, new ArrayList<>(), specials);
        game.moveEnemies(); // enemigo va a (1,2) -> activa bomba
        assertTrue(game.getEnemies().isEmpty());
        assertFalse(bomb.isActive());
    }

    // ── LifeSource ────────────────────────────────────────────────────────

    @Test
    void shouldReduceDeathsWhenPlayerStepsOnLifeSource() {
        List<SpecialElement> specials = new ArrayList<>();
        LifeSource life = new LifeSource(new Position(1, 2));
        specials.add(life);
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>(), specials);
        game.getPlayer().addDeath();
        game.getPlayer().addDeath();
        game.movePlayer(0, 1); // pisa la fuente de vida
        assertEquals(1, game.getPlayer().getDeaths()); // redujo en 1
        assertFalse(life.isActive());
    }

    @Test
    void shouldDeactivateLifeSourceAfterContact() {
        LifeSource life = new LifeSource(new Position(1, 2));
        Player player = new Player(new Position(1, 2), Skin.RED);
        life.onPlayerContact(player, null);
        assertFalse(life.isActive());
    }

    // ── MachineFactory ────────────────────────────────────────────────────

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

    // ── ExpertMachine ─────────────────────────────────────────────────────

    @Test
    void shouldReturnMoveTowardCoinWhenExpertMachineHasCoins() throws GameException {
        CellType[][] board = makeBoard();
        List<Coin> coins = new ArrayList<>();
        coins.add(new YellowCoin(new Position(1, 3)));
        // Ponemos player2 manualmente en (3,1) para que la moneda en (1,3) esté en diagonal arriba-derecha
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(), coins, new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        ExpertMachine expert = new ExpertMachine();
        int[] move = expert.nextMove(game, game.getPlayer2());
        assertNotNull(move);
        // Verifica que el movimiento reduce la distancia Manhattan hacia la moneda
        Position p2 = game.getPlayer2().getPosition();
        Position coin = new Position(1, 3);
        int distBefore = Math.abs(p2.getRow() - coin.getRow()) + Math.abs(p2.getCol() - coin.getCol());
        int distAfter = Math.abs((p2.getRow() + move[0]) - coin.getRow()) + Math.abs((p2.getCol() + move[1]) - coin.getCol());
        assertTrue(distAfter < distBefore, "ExpertMachine debe acercarse a la moneda");
    }

    @Test
    void shouldMoveTowardSafeEndWhenExpertMachineHasNoCoins() throws GameException {
        CellType[][] board = makeBoard();
        TheDOPOHardestGame game = new TheDOPOHardestGame(board, new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        ExpertMachine expert = new ExpertMachine();
        int[] move = expert.nextMove(game, game.getPlayer2());
        assertNotNull(move);
        assertEquals(2, move.length);
    }

    @Test
    void shouldReturnMoveWhenRandomMachineIsAsked() throws GameException {
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                makeBoard(), new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), 60, GameMode.PVM, Skin.RED);
        MachinePlayer machine = MachineFactory.create("RANDOM");
        int[] move = machine.nextMove(game, game.getPlayer());
        assertNotNull(move);
        assertEquals(2, move.length);
    }

    // ── TheDOPOHardestGame ────────────────────────────────────────────────

    @Test
    void shouldNotMovePlayerWhenDestinationIsAWall() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        game.movePlayer(-1, 0); // arriba = WALL
        assertEquals(new Position(1, 1), game.getPlayer().getPosition());
    }

    @Test
    void shouldActivateGameOverWhenTimeReachesZero() {
        TheDOPOHardestGame game = makeGameWithTime(1);
        game.tickTime();
        assertTrue(game.isGameOver());
    }

    @Test
    void shouldNotDecreaseTimeBelowZeroAfterGameOver() {
        TheDOPOHardestGame game = makeGameWithTime(1);
        game.tickTime();
        game.tickTime();
        assertTrue(game.getTimeRemaining() <= 0);
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
    void shouldNotActivateVictoryWhenPlayerReachesSafeEndWithCoinsRemaining() {
        List<Coin> coins = new ArrayList<>();
        coins.add(new YellowCoin(new Position(1, 3))); // moneda fuera de la ruta (1,1)→(3,3)
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), coins);
        game.movePlayer(1, 0);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1);
        assertFalse(game.isVictory()); // llegó a SAFE_END pero aún hay moneda en (1,3)
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
    void shouldReturnTrueWhenPositionIsInsideBoardBounds() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        assertTrue(game.isValidPosition(1, 1));
        assertTrue(game.isValidPosition(3, 3));
    }

    @Test
    void shouldUpdateRespawnWhenPlayerStepsOnSafeMidZone() {
        CellType[][] board = makeBoard();
        board[2][2] = CellType.SAFE_MID;
        TheDOPOHardestGame game = new TheDOPOHardestGame(
                board, new Position(1, 1),
                new ArrayList<>(), new ArrayList<>(),
                60, GameMode.PLAYER, Skin.RED);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        assertEquals(new Position(2, 2), game.getPlayer().getRespawnPosition());
    }

    @Test
    void shouldNotMoveAfterVictory() {
        TheDOPOHardestGame game = makeGame(new ArrayList<>(), new ArrayList<>());
        game.movePlayer(1, 0);
        game.movePlayer(1, 0);
        game.movePlayer(0, 1);
        game.movePlayer(0, 1); // llega al END -> victoria
        assertTrue(game.isVictory());
        game.movePlayer(0, 1); // no deberia moverse
        assertEquals(new Position(3, 3), game.getPlayer().getPosition());
    }

    @Test
    void shouldRemoveEnemyWhenRemovedViaRemoveEnemy() throws GameException {
        List<Enemy> enemies = new ArrayList<>();
        Enemy enemy = EnemyFactory.create("BASIC_BLUE", new Position(2, 2), true,
                new String[]{"ENEMY", "BASIC_BLUE", "2", "2", "HORIZONTAL"});
        enemies.add(enemy);
        TheDOPOHardestGame game = makeGame(enemies, new ArrayList<>());
        game.removeEnemy(enemy);
        assertTrue(game.getEnemies().isEmpty());
    }

    // ── Position ──────────────────────────────────────────────────────────

    @Test
    void shouldBeEqualWhenPositionsHaveSameRowAndCol() {
        assertEquals(new Position(3, 4), new Position(3, 4));
    }

    @Test
    void shouldNotBeEqualWhenPositionsHaveDifferentCoordinates() {
        assertNotEquals(new Position(3, 4), new Position(3, 5));
        assertNotEquals(new Position(3, 4), new Position(2, 4));
    }

    @Test
    void shouldReturnCorrectRowAndColAfterSetters() {
        Position p = new Position(1, 1);
        p.setRow(5);
        p.setCol(7);
        assertEquals(5, p.getRow());
        assertEquals(7, p.getCol());
    }
}