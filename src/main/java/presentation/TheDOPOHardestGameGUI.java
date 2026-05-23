package presentation;

import javax.swing.SwingUtilities;

public class TheDOPOHardestGameGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
