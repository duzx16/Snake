package gameui;

import javax.swing.*;
import java.awt.*;

// 能够设置图片的JPanel
public class BackgroundPanel extends JPanel {
    private Image background;

    public BackgroundPanel(Image img) {
        super();
        background = img;
        setBackground(null);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
    }
}
