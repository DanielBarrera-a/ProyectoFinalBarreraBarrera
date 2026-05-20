package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*Hacer refactor*/

public class ConfigLoader {
    public static TheDOPOHardestGame loadConfig(String filepath, GameMode mode, Skin skin) throws GameException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            int rows = 0, cols = 0, time = 60;
            CellType[][] board = null;
            Position startPos = null;
            List<Enemy> enemies = new ArrayList<>();
            List<Coin> coins = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "DIMENSIONS":
                        rows = Integer.parseInt(parts[1]); // y (arriba a abajo)
                        cols = Integer.parseInt(parts[2]); // x (izquierda a derecha)
                        board = new CellType[rows][cols];
                        for (int i = 0; i < rows; i++) {
                            for (int j = 0; j < cols; j++) {
                                board[i][j] = CellType.EMPTY;
                            }
                        }
                        break;
                    case "TIME":
                        time = Integer.parseInt(parts[1]);
                        break;
                    case "START":
                        int sr = Integer.parseInt(parts[1]); // y
                        int sc = Integer.parseInt(parts[2]); // x
                        startPos = new Position(sr, sc);
                        if (board != null) board[sr][sc] = CellType.SAFE_START;
                        break;
                    case "MID":
                        int mr = Integer.parseInt(parts[1]); // y
                        int mc = Integer.parseInt(parts[2]); // x
                        if (board != null) board[mr][mc] = CellType.SAFE_MID;
                        break;
                    case "END":
                        int er = Integer.parseInt(parts[1]); // y
                        int ec = Integer.parseInt(parts[2]); // x
                        if (board != null) board[er][ec] = CellType.SAFE_END;
                        break;
                    case "WALL":
                        int wr = Integer.parseInt(parts[1]); // y
                        int wc = Integer.parseInt(parts[2]); // x
                        if (board != null) board[wr][wc] = CellType.WALL;
                        break;
                    case "COIN":
                        int cr = Integer.parseInt(parts[2]); // y
                        int cc = Integer.parseInt(parts[3]); // x
                        coins.add(new Coin(new Position(cr, cc), parts[1].equals("YELLOW")));
                        break;
                    case "ENEMY":
                        // Permite cargar enemigos BASIC_RED (como BasicBlueEnemy internamente)
                        if (parts[1].equals("BASIC_BLUE") || parts[1].equals("BASIC_RED")) {
                            int enr = Integer.parseInt(parts[2]); // y
                            int enc = Integer.parseInt(parts[3]); // x
                            boolean isHorizontal = parts[4].equals("HORIZONTAL");
                            enemies.add(new BasicBlueEnemy(new Position(enr, enc), isHorizontal));
                        }
                        break;
                }
            }

            if (board == null || startPos == null) {
                throw new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
            }

            return new TheDOPOHardestGame(board, startPos, enemies, coins, time, mode, skin);
        } catch (NumberFormatException e) {
            throw new GameException(GameException.ERROR_FORMATO_NUMERO);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GameException(GameException.ERROR_FUERA_DE_LIMITES);
        } catch (Exception e) {
            throw new GameException(GameException.ERROR_AL_CARGAR_NIVEL);
        }
    }
}
