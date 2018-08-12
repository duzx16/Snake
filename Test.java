import gamemain.GameMain;
import imageio.ImageManager;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain app = new GameMain();
        });
    }
}
