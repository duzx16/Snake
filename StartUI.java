import imageio.SVGImageReader;

import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StartUI extends JPanel {
    private GameMain _parent;
    private Image _background;

    StartUI(GameMain main) {
        super();
        _parent = main;
        setBackground(null);
        try {
            _background = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/start_ui.svg")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(_background, 0, 0, getWidth(), getHeight(), null);
    }
}
