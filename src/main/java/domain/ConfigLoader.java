package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*Hacer refactor*/

public class ConfigLoader {

    /**
     * Objeto de transferencia de datos interno para mantener el estado de la lectura del nivel.
     */
    private static class ParseContext {
        int time = 60;
        CellType[][] board = null;
        Position startPos = null;
        List<Enemy> enemies = new ArrayList<>();
        List<Coin> coins = new ArrayList<>();
    }

    /**
     * Lee un archivo de nivel y construye una instancia de TheDOPOHardestGame.
     *
     * @param filepath Ruta del archivo de texto del nivel.
     * @param mode     Modo de juego seleccionado (PLAYER, PVP, etc.).
     * @param skin     Skin o aspecto seleccionado por el jugador.
     * @return Instancia configurada de TheDOPOHardestGame lista para jugar.
     * @throws GameException Si el archivo no existe, tiene formato inválido o datos corruptos.
     */
    public static TheDOPOHardestGame loadConfig(String filepath, GameMode mode, Skin skin) throws GameException {
        ParseContext context = new ParseContext();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                processConfigurationLine(line.trim(), context);
            }

            if (context.board == null || context.startPos == null) {
                throw new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
            }

            return new TheDOPOHardestGame(context.board, context.startPos, context.enemies, context.coins, context.time, mode, skin);
        } catch (NumberFormatException e) {
            throw new GameException(GameException.ERROR_FORMATO_NUMERO);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GameException(GameException.ERROR_FUERA_DE_LIMITES);
        } catch (Exception e) {
            throw new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
        }
    }

    /**
     * Procesa una sola línea del archivo de configuración.
     * 
     * @param line    Línea leída del archivo.
     * @param context Contexto de parseo actual donde se guardarán los resultados.
     */
    private static void processConfigurationLine(String line, ParseContext context) {
        if (line.isEmpty() || line.startsWith("#")) return;
        
        String[] parts = line.split(" ");
        String command = parts[0];

        switch (command) {
            case "DIMENSIONS":
                initializeBoardDimensions(parts, context);
                break;
            case "TIME":
                context.time = Integer.parseInt(parts[1]);
                break;
            case "START":
                configurePlayerStartPosition(parts, context);
                break;
            case "MID":
                configureBoardCellType(parts, context, CellType.SAFE_MID);
                break;
            case "END":
                configureBoardCellType(parts, context, CellType.SAFE_END);
                break;
            case "WALL":
                configureBoardCellType(parts, context, CellType.WALL);
                break;
            case "COIN":
                registerCoinEntity(parts, context);
                break;
            case "ENEMY":
                registerEnemyEntity(parts, context);
                break;
        }
    }

    /**
     * Inicializa las dimensiones del tablero a partir del comando DIMENSIONS.
     * 
     * @param parts   Fragmentos de la línea separados por espacio.
     * @param context Contexto de parseo actual.
     */
    private static void initializeBoardDimensions(String[] parts, ParseContext context) {
        int rows = Integer.parseInt(parts[1]); // y (arriba a abajo)
        int cols = Integer.parseInt(parts[2]); // x (izquierda a derecha)
        context.board = new CellType[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                context.board[i][j] = CellType.EMPTY;
            }
        }
    }

    /**
     * Configura la posición inicial del jugador (START).
     * 
     * @param parts   Fragmentos de la línea separados por espacio.
     * @param context Contexto de parseo actual.
     */
    private static void configurePlayerStartPosition(String[] parts, ParseContext context) {
        int sr = Integer.parseInt(parts[1]); // y
        int sc = Integer.parseInt(parts[2]); // x
        context.startPos = new Position(sr, sc);
        if (context.board != null) context.board[sr][sc] = CellType.SAFE_START;
    }

    /**
     * Configura una zona específica en el tablero (WALL, MID, END).
     * 
     * @param parts    Fragmentos de la línea separados por espacio.
     * @param context  Contexto de parseo actual.
     * @param cellType Tipo de celda a establecer.
     */
    private static void configureBoardCellType(String[] parts, ParseContext context, CellType cellType) {
        int r = Integer.parseInt(parts[1]); // y
        int c = Integer.parseInt(parts[2]); // x
        if (context.board != null) context.board[r][c] = cellType;
    }

    /**
     * Crea y añade una moneda (COIN) al contexto.
     * 
     * @param parts   Fragmentos de la línea separados por espacio.
     * @param context Contexto de parseo actual.
     */
    private static void registerCoinEntity(String[] parts, ParseContext context) {
        boolean isYellow = parts[1].equals("YELLOW");
        int r = Integer.parseInt(parts[2]); // y
        int c = Integer.parseInt(parts[3]); // x
        context.coins.add(new Coin(new Position(r, c), isYellow));
    }

    /**
     * Crea y añade un enemigo (ENEMY) al contexto.
     * 
     * @param parts   Fragmentos de la línea separados por espacio.
     * @param context Contexto de parseo actual.
     */
    private static void registerEnemyEntity(String[] parts, ParseContext context) {
        // Permite cargar enemigos BASIC_RED (como BasicBlueEnemy internamente)
        if (parts[1].equals("BASIC_BLUE") || parts[1].equals("BASIC_RED")) {
            int r = Integer.parseInt(parts[2]); // y
            int c = Integer.parseInt(parts[3]); // x
            boolean isHorizontal = parts[4].equals("HORIZONTAL");
            context.enemies.add(new BasicBlueEnemy(new Position(r, c), isHorizontal));
        }
    }
}
