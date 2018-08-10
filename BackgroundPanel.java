import imageio.ImageManager;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image background;
    BackgroundPanel(Image img) {
        super();
        background = img;
        setBackground(null);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }
}
