package presentation;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public GameWindow() {
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        MainMenuPanel mainMenu = new MainMenuPanel(this);
        mainPanel.add(mainMenu, "MainMenu");

        add(mainPanel);
    }

    public void startGame(domain.GameMode mode, domain.Skin skin) {
        try {
            domain.TheDOPOHardestGame game = domain.ConfigLoader.loadConfig("level1.txt", mode, skin);
            GamePanel gamePanel = new GamePanel(this, game);
            mainPanel.add(gamePanel, "Game");
            cardLayout.show(mainPanel, "Game");
            gamePanel.requestFocusInWindow();
        } catch (domain.GameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MainMenu");
    }
}
