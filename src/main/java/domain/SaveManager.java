package domain;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

public class SaveManager {

    public static boolean saveGame(Gamesave save) throws GameException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar partida");
        chooser.setFileFilter(new FileNameExtensionFilter("Partida guardada (*.dat)", "dat"));
        chooser.setSelectedFile(new File("partida.dat"));

        int result = chooser.showSaveDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return false;

        File file = chooser.getSelectedFile();
        if (!file.getName().endsWith(".dat")) {
            file = new File(file.getAbsolutePath() + ".dat");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(save);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new GameException(GameException.ERROR_AL_GUARDAR);
        }
    }

    public static Gamesave loadGame() throws GameException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir partida guardada");
        chooser.setFileFilter(new FileNameExtensionFilter("Partida guardada (*.dat)", "dat"));

        int result = chooser.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) return null;

        File file = chooser.getSelectedFile();
        if (!file.exists()) {
            throw new GameException(GameException.ERROR_NO_HAY_PARTIDA);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Gamesave) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new GameException(GameException.ERROR_AL_CARGAR_PARTIDA);
        }
    }

    public static boolean hasSave() {

        return true; // Con JFileChooser siempre habilitamos el botón
    }
}