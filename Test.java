import javax.swing.*;
import java.awt.*;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain app = new GameMain();
        });
    }
}
