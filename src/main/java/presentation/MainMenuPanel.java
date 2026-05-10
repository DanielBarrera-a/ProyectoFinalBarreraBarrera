package presentation;

import domain.GameMode;
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

        JButton btnPvp = new JButton("Modo PvsP (En desarrollo)");
        btnPvp.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPvp.addActionListener(e -> JOptionPane.showMessageDialog(this, "Esta función está en desarrollo"));

        JButton btnPvm = new JButton("Modo PvsM (En desarrollo)");
        btnPvm.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPvm.addActionListener(e -> JOptionPane.showMessageDialog(this, "Esta función está en desarrollo"));

        add(Box.createVerticalStrut(100));
        add(title);
        add(Box.createVerticalStrut(50));
        add(btnPlayer);
        add(Box.createVerticalStrut(20));
        add(btnPvp);
        add(Box.createVerticalStrut(20));
        add(btnPvm);
    }

    private void startGame(GameMode mode) {
        String[] options = {"Rojo (Blinky)", "Azul (Inky) ", "Verde (Clyde)"};
        int skinChoice = JOptionPane.showOptionDialog(this, "Selecciona tu cuadrado", "Selección de Personaje",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (skinChoice == 0) {
            window.startGame(mode, Skin.RED);
        } else if (skinChoice == 1 || skinChoice == 2) {
            JOptionPane.showMessageDialog(this, "Esta función está en desarrollo ");
        }
    }
}
