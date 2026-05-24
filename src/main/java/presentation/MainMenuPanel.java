package presentation;

import domain.GameMode;
import domain.MachineFactory;
import domain.Skin;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    private GameWindow window;

    public MainMenuPanel(GameWindow window) {
        this.window = window;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(230, 230, 250));

        JLabel title = new JLabel("The DOPO Hardest Game");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPlayer = new JButton("Modo Player");
        btnPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlayer.addActionListener(e -> startGame(GameMode.PLAYER));

        JButton btnPvp = new JButton("Modo PvsP");
        btnPvp.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPvp.addActionListener(e -> startGame(GameMode.PVP));

        JButton btnPvm = new JButton("Modo PvsM");
        btnPvm.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPvm.addActionListener(e -> startPvmGame());

        // En esta parte es que se esta agregando la persistencia
        JButton btnLoad = new JButton("Cargar partida guardada");
        btnLoad.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLoad.addActionListener(e -> window.loadSavedGame());
        if (!domain.SaveManager.hasSave()) {
            btnLoad.setEnabled(false);
            btnLoad.setToolTipText("No hay ninguna partida guardada.");
        }

        add(Box.createVerticalStrut(100));
        add(title);
        add(Box.createVerticalStrut(50));
        add(btnPlayer);
        add(Box.createVerticalStrut(20));
        add(btnPvp);
        add(Box.createVerticalStrut(20));
        add(btnPvm);
        add(Box.createVerticalStrut(20));
        add(btnLoad);
    }

    private void startGame(GameMode mode) {
        String[] options = {"Rojo (Blinky)", "Azul (Inky) ", "Verde (Clyde)"};
        int skinChoice = JOptionPane.showOptionDialog(this, "Selecciona tu cuadrado", "Selección de Personaje",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        Skin skin;
        if (skinChoice == 1) {
            skin = Skin.BLUE;
        } else if (skinChoice == 2) {
            skin = Skin.GREEN;
        } else {
            skin = Skin.RED;
        }
        window.startGame(mode, skin, null);
    }

    private void startPvmGame() {
        String[] skinOptions = {"Rojo (Blinky)", "Azul (Inky)", "Verde (Clyde)"};
        int skinChoice = JOptionPane.showOptionDialog(this, "Selecciona tu cuadrado", "Selección de Personaje",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, skinOptions, skinOptions[0]);

        Skin skin;
        if (skinChoice == 1) skin = Skin.BLUE;
        else if (skinChoice == 2) skin = Skin.GREEN;
        else skin = Skin.RED;

        String[] machineOptions = {"Aleatoria", "Experta"};
        int machineChoice = JOptionPane.showOptionDialog(this, "Selecciona el perfil de la máquina",
                "Perfil de Máquina", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, machineOptions, machineOptions[0]);

        String machineType = (machineChoice == 1) ? "EXPERT" : "RANDOM";

        try {
            window.startGame(GameMode.PVM, skin, MachineFactory.create(machineType));
        } catch (domain.GameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}