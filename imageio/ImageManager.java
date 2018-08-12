package imageio;

import javax.imageio.stream.FileCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageManager {
    public static Image game_background, hole_image, stone_image, statistics_background;
    public static Image start_background;
    public static Image[] wall_edges = new Image[4], wall_middles = new Image[4], wall_trans = new Image[4];
    public static BufferedImage[][] snake_heads = new BufferedImage[2][4], snake_tails = new BufferedImage[2][4], snake_middles = new BufferedImage[2][4], snake_trans = new BufferedImage[2][4];
    public static Image[] food_images = new Image[5];
    public static Image play_option, pause_button, play_button, home_button, music_button;

    public static void initImage(Class<?> loader) {
        try {
            game_background = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/green_back.svg"), null));
            statistics_background = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/statistics.svg"), null));
            BufferedImage _wall_edge = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/grass_edge.svg"), null));
            BufferedImage _wall_middle = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/grass_middle.svg"), null));
            BufferedImage _wall_tran = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/grass_tran.svg"), null));
            BufferedImage _snake_head = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/snake_head.svg"), null));
            BufferedImage _snake_tail = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/snake_tail.svg"), null));
            BufferedImage _snake_tran = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/snake_tran.svg"), null));
            BufferedImage _snake_middle = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/snake_middle.svg"), null));
            for (int i = 0; i < 4; i++) {
                wall_edges[i] = ImageRender.rotateImage(_wall_edge, i * 90);
                wall_trans[i] = ImageRender.rotateImage(_wall_tran, i * 90);
                wall_middles[i] = ImageRender.rotateImage(_wall_middle, i * 90);
                snake_heads[0][i] = ImageRender.rotateImage(_snake_head, i * 90);
                snake_tails[0][i] = ImageRender.rotateImage(_snake_tail, i * 90);
                snake_trans[0][i] = ImageRender.rotateImage(_snake_tran, i * 90);
                snake_middles[0][i] = ImageRender.rotateImage(_snake_middle, i * 90);
                snake_heads[1][i] = ImageRender.inverseImage(snake_heads[0][i]);
                snake_tails[1][i] = ImageRender.inverseImage(snake_tails[0][i]);
                snake_trans[1][i] = ImageRender.inverseImage(snake_trans[0][i]);
                snake_middles[1][i] = ImageRender.inverseImage(snake_middles[0][i]);
            }
            food_images[0] = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/apple.svg"), null));
            food_images[1] = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/banana.svg"), null));
            food_images[2] = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/cherry.svg"), null));
            food_images[3] = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/melon.svg"), null));
            food_images[4] = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/orange.svg"), null));
            hole_image = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/hole.svg"), null));
            stone_image = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/stone.svg"), null));
            play_option = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/play_option.svg"), null));
            pause_button = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/pause_button.svg"), null));
            play_button = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/play_button.svg"), null));
            home_button = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/home.svg"), null));
            music_button = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/music.svg"), null));
            start_background = SVGImageReader.svgToBufferedImage(new FileCacheImageInputStream(loader.getResourceAsStream("shape/start_ui.svg"), null));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
