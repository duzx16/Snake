package imageio;

import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageManager {
    public static Image game_background, hole_image, stone_image;
    public static Image start_background;
    public static Image[] wall_edges = new Image[4], wall_middles = new Image[4], wall_trans = new Image[4];
    public static BufferedImage[][] snake_heads = new BufferedImage[2][4], snake_tails = new BufferedImage[2][4], snake_middles = new BufferedImage[2][4], snake_trans = new BufferedImage[2][4];
    public static Image[] food_images = new Image[5];
    public static Image play_option, pause_button, play_button, home_button, music_button;
    public static void initImage() {
        try {
            game_background = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/green_back.svg")));
            BufferedImage _wall_edge = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/grass_edge.svg")));
            BufferedImage _wall_middle = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/grass_middle.svg"))));
            BufferedImage _wall_tran = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/grass_tran.svg"))));
            BufferedImage _snake_head = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_head.svg"))));
            BufferedImage _snake_tail = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_tail.svg"))));
            BufferedImage _snake_tran = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_tran.svg"))));
            BufferedImage _snake_middle = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_middle.svg"))));
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
            food_images[0] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/apple.svg")));
            food_images[1] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/banana.svg")));
            food_images[2] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/cherry.svg")));
            food_images[3] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/melon.svg")));
            food_images[4] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/orange.svg")));
            hole_image = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/hole.svg")));
            stone_image = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/stone.svg")));
            play_option = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/play_option.svg")));
            pause_button = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/pause_button.svg")));
            play_button = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/play_button.svg")));
            home_button = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/home.svg")));
            music_button = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/music.svg")));
            start_background = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/start_ui.svg")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
