package presentation;

import javax.swing.SwingUtilities;

public class TheDOPOHardestGameGUI {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
