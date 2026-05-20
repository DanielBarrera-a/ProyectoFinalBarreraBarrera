package presentation;

import domain.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener {
    private GameWindow window;
    private TheDOPOHardestGame game;
    private Timer timer;
    private final int CELL_SIZE = 40;

    public GamePanel(GameWindow window, TheDOPOHardestGame game) {
        this.window = window;
        this.game = game;
        setBackground(Color.WHITE);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ── Persistencia ──────────────────────────────────────────
                if (e.getKeyCode() == KeyEvent.VK_G) {
                    timer.stop();
                    window.saveGame();
                    timer.start();
                    return;
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    timer.stop();
                    int choice = JOptionPane.showOptionDialog(
                            GamePanel.this,
                            "Juego pausado. ¿Qué deseas hacer?",
                            "Pausa",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new String[]{"Continuar", "Guardar y salir", "Salir sin guardar"},
                            "Continuar"
                    );
                    if (choice == 1) {
                        window.saveGame();
                        window.showMainMenu();
                    } else if (choice == 2) {
                        window.showMainMenu();
                    } else {
                        timer.start();
                    }
                    return;
                }
                // ── Movimiento Player 1 (flechas) ─────────────────────────
                int dx = 0, dy = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:    dy = -1; break;
                    case KeyEvent.VK_DOWN:  dy =  1; break;
                    case KeyEvent.VK_LEFT:  dx = -1; break;
                    case KeyEvent.VK_RIGHT: dx =  1; break;
                }
                if (dx != 0 || dy != 0) {
                    game.movePlayer(dy, dx);
                    repaint();
                    checkGameState();
                }
                // ── Movimiento Player 2 (WASD) ────────────────────────────
                int dx2 = 0, dy2 = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: dy2 = -1; break;
                    case KeyEvent.VK_S: dy2 =  1; break;
                    case KeyEvent.VK_A: dx2 = -1; break;
                    case KeyEvent.VK_D: dx2 =  1; break;
                }
                if (dx2 != 0 || dy2 != 0) {
                    game.movePlayer2(dy2, dx2);
                    repaint();
                    checkGameState();
                }
            }
        });

        timer = new Timer(500, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        game.tickTime();
        game.moveEnemies();
        repaint();
        checkGameState();
    }

    private void checkGameState() {
        if (game.isVictory()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "¡Victoria! Has completado el nivel " + window.getCurrentLevel() + ".");
            window.levelCompleted();
        } else if (game.isGameOver()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Game Over. Se acabó el tiempo o moriste.");
            window.showMainMenu();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int offsetX = (getWidth() - game.getCols() * CELL_SIZE) / 2;
        int offsetY = (getHeight() - game.getRows() * CELL_SIZE) / 2;

        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                CellType type = game.getCell(r, c);
                if (type == CellType.WALL) {
                    g.setColor(Color.DARK_GRAY);
                } else if (type == CellType.SAFE_START || type == CellType.SAFE_END || type == CellType.SAFE_MID) {
                    g.setColor(new Color(144, 238, 144));
                } else {
                    g.setColor(new Color(240, 240, 240));
                }
                g.fillRect(offsetX + c * CELL_SIZE, offsetY + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(offsetX + c * CELL_SIZE, offsetY + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        g.setColor(Color.YELLOW);
        for (Coin coin : game.getCoins()) {
            g.fillOval(offsetX + coin.getPosition().getCol() * CELL_SIZE + 10,
                    offsetY + coin.getPosition().getRow() * CELL_SIZE + 10,
                    20, 20);
        }

        g.setColor(Color.BLUE);
        for (Enemy enemy : game.getEnemies()) {
            g.fillOval(offsetX + enemy.getPosition().getCol() * CELL_SIZE + 5,
                    offsetY + enemy.getPosition().getRow() * CELL_SIZE + 5,
                    30, 30);
        }

        if (game.getPlayer().getSkin() == Skin.RED) {
            g.setColor(Color.RED);
        } else if (game.getPlayer().getSkin() == Skin.BLUE) {
            g.setColor(Color.BLUE);
        } else if (game.getPlayer().getSkin() == Skin.GREEN) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.BLACK);
        }
        g.fillRect(offsetX + game.getPlayer().getPosition().getCol() * CELL_SIZE + 5,
                offsetY + game.getPlayer().getPosition().getRow() * CELL_SIZE + 5,
                30, 30);

        if (game.getPlayer2() != null) {
            if (game.getPlayer2().getSkin() == Skin.RED) {
                g.setColor(Color.RED);
            } else if (game.getPlayer2().getSkin() == Skin.BLUE) {
                g.setColor(Color.BLUE);
            } else if (game.getPlayer2().getSkin() == Skin.GREEN) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLACK);
            }
            g.fillRect(offsetX + game.getPlayer2().getPosition().getCol() * CELL_SIZE + 5,
                    offsetY + game.getPlayer2().getPosition().getRow() * CELL_SIZE + 5,
                    30, 30);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Tiempo: " + game.getTimeRemaining(), 20, 30);

        if (game.getPlayer2() != null) {
            g.drawString("P1 Muertes: " + game.getPlayer().getDeaths(), 150, 30);
            g.drawString("P2 Muertes: " + game.getPlayer2().getDeaths(), 150, 50);
        } else {
            g.drawString("Muertes: " + game.getPlayer().getDeaths(), 150, 30);
        }

        g.drawString("Monedas: " + game.getCoins().size(), 280, 30);
        // ── Hint teclas ───────────────────────────────────────────────────
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.setColor(Color.GRAY);
        g.drawString("[G] Guardar   [ESC] Pausa", 20, getHeight() - 10);
    }
}