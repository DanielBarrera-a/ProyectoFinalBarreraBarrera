package domain;

import java.io.*;

public class SaveManager {

    private static final String SAVE_FILE = "partida_guardada.dat";

    public static void saveGame(Gamesave save) throws GameException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(save);
        } catch (IOException e) {
            throw new GameException(GameException.ERROR_AL_GUARDAR);
        }
    }

    public static Gamesave loadGame() throws GameException {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            throw new GameException(GameException.ERROR_NO_HAY_PARTIDA);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (Gamesave) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException(GameException.ERROR_AL_CARGAR_PARTIDA);
        }
    }

    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }
}