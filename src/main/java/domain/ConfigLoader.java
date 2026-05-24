package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga la configuración de un nivel desde un archivo .txt.
 * SRP: Su única responsabilidad es leer el archivo y construir el juego.
 * OCP: No instancia enemigos ni monedas directamente. Delega eso a las
 * fábricas (EnemyFactory, CoinFactory). Para agregar un nuevo tipo de
 * entidad, no se toca este archivo.
 */
public class ConfigLoader {

    /**
     * Objeto interno para acumular los datos del nivel mientras se lee el archivo.
     */
    private static class ParseContext {
        int time = 60;
        CellType[][] board = null;
        Position startPos = null;
        List<Enemy> enemies = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
        List<SpecialElement> specialElements = new ArrayList<>();
    }

    /**
     * Lee un archivo de nivel y construye una instancia de TheDOPOHardestGame
     *
     * @param filepath Ruta del archivo .txt del nivel
     * @param mode     Modo de juego (PLAYER, PVP, PVM)
     * @param skin     Skin seleccionada por el jugador
     * @return Instancia del juego lista para jugar
     * @throws GameException Si el archivo tiene errores o formato invalido
     */
    public static TheDOPOHardestGame loadConfig(String filepath, GameMode mode, Skin skin) throws GameException {
        ParseContext context = new ParseContext();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line.trim(), context);
            }
        } catch (IOException e) {
            throw new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
        } catch (NumberFormatException e) {
            throw new GameException(GameException.ERROR_FORMATO_NUMERO);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GameException(GameException.ERROR_FUERA_DE_LIMITES);
        }

        if (context.board == null || context.startPos == null) {
            throw new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
        }

        return new TheDOPOHardestGame(
                context.board, context.startPos,
                context.enemies, context.coins,
                context.specialElements,
                context.time, mode, skin
        );
    }

    /**
     * Procesa una línea del archivo de nivel
     */
    private static void processLine(String line, ParseContext context) throws GameException {
        if (line.isEmpty() || line.startsWith("#")) return;

        String[] parts = line.split(" ");
        String command = parts[0];

        switch (command) {
            case "DIMENSIONS":
                initBoard(parts, context);
                break;
            case "TIME":
                context.time = Integer.parseInt(parts[1]);
                break;
            case "START":
                configureStart(parts, context);
                break;
            case "MID":
                setCell(parts, context, CellType.SAFE_MID);
                break;
            case "END":
                setCell(parts, context, CellType.SAFE_END);
                break;
            case "WALL":
                setCell(parts, context, CellType.WALL);
                break;
            case "SPECIAL":
                registerSpecialElement(parts, context);
                break;
            case "COIN":
                registerCoin(parts, context);
                break;
            case "ENEMY":
                registerEnemy(parts, context);
                break;
        }
    }

    private static void initBoard(String[] parts, ParseContext context) {
        int rows = Integer.parseInt(parts[1]);
        int cols = Integer.parseInt(parts[2]);
        context.board = new CellType[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                context.board[i][j] = CellType.EMPTY;
            }
        }
    }

    private static void configureStart(String[] parts, ParseContext context) {
        int r = Integer.parseInt(parts[1]);
        int c = Integer.parseInt(parts[2]);
        context.startPos = new Position(r, c);
        if (context.board != null) context.board[r][c] = CellType.SAFE_START;
    }

    private static void setCell(String[] parts, ParseContext context, CellType type) {
        int r = Integer.parseInt(parts[1]);
        int c = Integer.parseInt(parts[2]);
        if (context.board != null) context.board[r][c] = type;
    }

    /**
     * Delega la creación de la moneda a CoinFactory.
     * Si mañana hay un nuevo coin, se escribe en el .txt, solo se agrega el caso en CoinFactory dentro
     * del contructor de coin
     */
    private static void registerCoin(String[] parts, ParseContext context) throws GameException {
        String type = parts[1];           // Ej: "YELLOW" o "GREEN_MAGIC"
        int r = Integer.parseInt(parts[2]);
        int c = Integer.parseInt(parts[3]);
        context.coins.add(CoinFactory.create(type, new Position(r, c)));
    }

    private static void registerSpecialElement(String[] parts, ParseContext context) throws GameException {
        String type = parts[1];           // Ej: "BOMB", "LIFE_SOURCE"
        int r = Integer.parseInt(parts[2]);
        int c = Integer.parseInt(parts[3]);
        context.specialElements.add(SpecialElementFactory.create(type, new Position(r, c)));
    }

    /**
     * Delega la creación del enemigo a EnemyFactory.
     * Si mañana hay que crear un nuevo enemigo, se escribe en el .txt, solo se agrega el caso en EnemyFactory
     */
    private static void registerEnemy(String[] parts, ParseContext context) throws GameException {
        String type = parts[1];           // Ej: "BASIC_BLUE" o "BASIC_RED"
        int r = Integer.parseInt(parts[2]);
        int c = Integer.parseInt(parts[3]);
        boolean isHorizontal = parts[4].equals("HORIZONTAL");
        context.enemies.add(EnemyFactory.create(type, new Position(r, c), isHorizontal));
    }
}