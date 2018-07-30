import game_data.GameData;
import imageio.SVGImageReader;

import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StatisticsUI extends JPanel {
    private GameData _data;
    private Image _img;

    public StatisticsUI(GameData data) {
        _data = data;
        setPreferredSize(new Dimension(650, 60));
        try {
            _img = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/statistics.svg")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void paintComponent(Graphics g) {
        _data.lock.readLock().lock();
        int width = getWidth(), height = getHeight();
        g.drawImage(_img, 0, 0, width, height, null);
        g.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        g.setColor(Color.BLACK);
        g.drawString(String.valueOf(_data.snake_nums[0]), width * 3 / 16, height * 10 / 11);
        g.drawString(String.valueOf(_data.scores[0]), width * 7 / 16, height * 10 / 11);
        g.drawString(String.valueOf(_data.snake_nums[1]), width * 11 / 16, height * 10 / 11);
        g.drawString(String.valueOf(_data.scores[1]), width * 15 / 16, height * 10 / 11);
        _data.lock.readLock().unlock();
    }
}
