package presentation;

import domain.Gamesave;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private int currentLevel = 1;
    private domain.GameMode currentMode;
    private domain.Skin currentSkin;

    private domain.TheDOPOHardestGame currentGame;

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
        this.currentMode = mode;
        this.currentSkin = skin;
        this.currentLevel = 1;
        loadLevel();
    }

    private void loadLevel() {
        try {
            String levelFile = "level" + currentLevel + ".txt";
            currentGame = domain.ConfigLoader.loadConfig(levelFile, currentMode, currentSkin);
            GamePanel gamePanel = new GamePanel(this, currentGame);
            mainPanel.add(gamePanel, "Game");
            cardLayout.show(mainPanel, "Game");
            gamePanel.requestFocusInWindow();
        } catch (domain.GameException e) {
            // Si hay error, asumimos que no hay más niveles (se terminó el juego)
            JOptionPane.showMessageDialog(this, "¡Felicidades! Has completado todos los niveles.", "Juego Terminado", JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        }
    }

    public void levelCompleted() {
        currentLevel++;
        loadLevel();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "MainMenu");
    }



    public void saveGame() {
        try {
            Gamesave save = new Gamesave(currentGame, currentLevel, currentMode, currentSkin);
            domain.SaveManager.saveGame(save);
            JOptionPane.showMessageDialog(this, "¡Partida guardada correctamente!", "Guardar", JOptionPane.INFORMATION_MESSAGE);
        } catch (domain.GameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadSavedGame() {
        try {
            Gamesave save = domain.SaveManager.loadGame();
            this.currentLevel = save.currentLevel();
            this.currentMode  = save.mode();
            this.currentSkin  = save.skin();
            this.currentGame  = save.game();
            GamePanel gamePanel = new GamePanel(this, currentGame);
            mainPanel.add(gamePanel, "Game");
            cardLayout.show(mainPanel, "Game");
            gamePanel.requestFocusInWindow();
        } catch (domain.GameException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al cargar", JOptionPane.ERROR_MESSAGE);
        }
    }
}