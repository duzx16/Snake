import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import imageio.ImageRender;
import imageio.SVGImageReader;


public class MainUI extends JPanel {
    private Image _background, _hole, _stone;
    private Image[] _wall_edges = new Image[4], _wall_middles = new Image[4], _wall_trans = new Image[4];
    private BufferedImage[][] _snake_heads = new BufferedImage[2][4], _snake_tails = new BufferedImage[2][4], _snake_middles = new BufferedImage[2][4], _snake_trans = new BufferedImage[2][4];
    private Image[] _food_images = new Image[5];
    private GameData _data;
    double unit_x, unit_y;

    MainUI(GameData data) {
        _data = data;
        try {
            _background = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/green_back.svg")));
            BufferedImage _wall_edge = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/grass_edge.svg")));
            BufferedImage _wall_middle = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/grass_middle.svg"))));
            BufferedImage _wall_tran = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/grass_tran.svg"))));
            BufferedImage _snake_head = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_head.svg"))));
            BufferedImage _snake_tail = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_tail.svg"))));
            BufferedImage _snake_tran = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_tran.svg"))));
            BufferedImage _snake_middle = SVGImageReader.svgToBufferedImage(new FileImageInputStream((new File("shape/snake_middle.svg"))));
            for (int i = 0; i < 4; i++) {
                _wall_edges[i] = ImageRender.rotateImage(_wall_edge, i * 90);
                _wall_trans[i] = ImageRender.rotateImage(_wall_tran, i * 90);
                _wall_middles[i] = ImageRender.rotateImage(_wall_middle, i * 90);
                _snake_heads[0][i] = ImageRender.rotateImage(_snake_head, i * 90);
                _snake_tails[0][i] = ImageRender.rotateImage(_snake_tail, i * 90);
                _snake_trans[0][i] = ImageRender.rotateImage(_snake_tran, i * 90);
                _snake_middles[0][i] = ImageRender.rotateImage(_snake_middle, i * 90);
                _snake_heads[1][i] = ImageRender.inverseImage(_snake_heads[0][i]);
                _snake_tails[1][i] = ImageRender.inverseImage(_snake_tails[0][i]);
                _snake_trans[1][i] = ImageRender.inverseImage(_snake_trans[0][i]);
                _snake_middles[1][i] = ImageRender.inverseImage(_snake_middles[0][i]);
            }
            _food_images[0] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/apple.svg")));
            _food_images[1] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/banana.svg")));
            _food_images[2] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/cherry.svg")));
            _food_images[3] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/melon.svg")));
            _food_images[4] = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/orange.svg")));
            _hole = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/hole.svg")));
            _stone = SVGImageReader.svgToBufferedImage(new FileImageInputStream(new File("shape/stone.svg")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void paintComponent(Graphics g) {
        _data.lock.readLock().lock();
        unit_x = (double) getWidth() / GameData.MAP_WIDTH;
        unit_y = (double) getHeight() / GameData.MAP_HEIGHT;
        g.drawImage(_background, 0, 0, getWidth(), getHeight(), null);
        paintWalls(_data.walls, g);
        paintFoods(_data.foods, g);
        paintHoles(_data.holes, g);
        paintSingles(_data.stones, _stone, g);
        paintSnake(0, g);
        paintSnake(1, g);

//        for (int i = 0; i < GameData.MAP_WIDTH; i++) {
//            for (int j = 0; j < GameData.MAP_HEIGHT; j++) {
//                g.setColor(Color.BLACK);
//                g.drawRect((int)(i * unit_x), (int)(j * unit_y), (int)(unit_x), (int)(unit_y));
//                switch (_data.map.elementAt(i, j).type){
//                    case SNAKE:
//                        g.setColor(Color.RED);
//                        break;
//                    case FOOD:
//                        g.setColor(Color.GREEN);
//                        break;
//                    case WALL:
//                        g.setColor(Color.GRAY);
//                        break;
//                    case HOLE:
//                        g.setColor(Color.BLACK);
//                        break;
//                    case STONE:
//                        g.setColor(Color.YELLOW);
//                        break;
//                    case NULL:
//                        g.setColor(Color.WHITE);
//                        break;
//                }
//                g.fillRect((int)(i * unit_x), (int)(j * unit_y), (int)(unit_x), (int)(unit_y));
//            }
//        }
        _data.lock.readLock().unlock();
    }

    private int dirToAngle(Point dir) {
        if (dir.x == 0) {
            if (dir.y == 1) {
                return 0;
            } else if (dir.y == -1) {
                return 2;
            }
        } else if (dir.x == -1) {
            if (dir.y == 0 || dir.y == 1) {
                return 1;
            } else {
                return 2;
            }
        } else {
            if (dir.y == 0 || dir.y == -1) {
                return 3;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private void paintWalls(ArrayList<ArrayList<Point>> walls, Graphics g) {
        for (ArrayList<Point> wall :
                walls) {
            Point dir, old_dir = new Point();
            for (int i = 1; i <= wall.size(); i++) {
                Point pos = wall.get(i - 1);
                if (i == wall.size()) {
                    dir = old_dir;
                } else {
                    dir = wall.get(i).sub(pos);
                }
                int ui_x = (int) (pos.x * unit_x) - 1, ui_y = (int) (pos.y * unit_y) - 1;
                if (i == 1) {
                    g.drawImage(_wall_edges[dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                } else if (i == wall.size()) {
                    g.drawImage(_wall_edges[(dirToAngle(dir) + 2) % 4], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                } else {
                    if (dir.equalTo(old_dir)) {
                        g.drawImage(_wall_middles[dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                    } else {
                        g.drawImage(_wall_trans[dirToAngle(dir.sub(old_dir))], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                    }
                }
                old_dir = dir;
            }
        }
    }

    void paintSnake(int index, Graphics g) {
        Snake snake = _data.snakes[index];
        Point dir, old_dir = new Point(), pos = snake.body.elementAt(0);
        for (int i = 1; i <= snake.body.size(); i++) {
            if (i == snake.body.size()) {
                dir = old_dir;
            } else {
                dir = snake.body.elementAt(i);
            }
            int ui_x = (int) (pos.x * unit_x) - 1, ui_y = (int) (pos.y * unit_y) - 1;
            if (i == 1) {
                if (_data.map.elementAt(pos).type == MapEle.EleType.SNAKE)
                    g.drawImage(_snake_heads[index][dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                pos = pos.add(snake.body.elementAt(i));
            } else if (i == snake.body.size()) {
                if (_data.map.elementAt(pos).type == MapEle.EleType.SNAKE)
                    g.drawImage(_snake_tails[index][dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
            } else {
                if (dir.equalTo(old_dir)) {
                    if (_data.map.elementAt(pos).type == MapEle.EleType.SNAKE)
                        g.drawImage(_snake_middles[index][dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                } else {
                    if (_data.map.elementAt(pos).type == MapEle.EleType.SNAKE) {
                        g.drawImage(_snake_trans[index][dirToAngle(dir.sub(old_dir))], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                    }
                }
                pos = pos.add(snake.body.elementAt(i));
            }
            pos.bound();
            old_dir = dir;
        }
    }

    private void paintFoods(ArrayList<Food> foods, Graphics g) {
        for (Food food :
                foods) {
            int ui_x = (int) (food.pos.x * unit_x), ui_y = (int) (food.pos.y * unit_y);
            g.drawImage(_food_images[food.type.ordinal()], ui_x, ui_y, (int) unit_x, (int) unit_y, null);
        }
    }

    private void paintSingles(ArrayList<Point> singles, Image img, Graphics g) {
        for (Point p :
                singles) {
            int ui_x = (int) (p.x * unit_x), ui_y = (int) (p.y * unit_y);
            g.drawImage(img, ui_x, ui_y, (int) unit_x, (int) unit_y, null);
        }
    }

    private void paintHoles(ArrayList<Hole> holes, Graphics g) {
        for (Hole h : holes) {
            int ui_x = (int) (h.pos.x * unit_x), ui_y = (int) (h.pos.y * unit_y);
            g.drawImage(_hole, ui_x, ui_y, (int) unit_x, (int) unit_y, null);
        }
    }
}
