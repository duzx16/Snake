import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import game_data.*;
import game_data.Point;
import imageio.ImageManager;


public class MainUI extends JPanel {
    private GameData _data;
    double unit_x, unit_y;

    MainUI(GameData data) {
        _data = data;

    }

    public void paintComponent(Graphics g) {
        _data.lock.readLock().lock();
        unit_x = (double) getWidth() / GameConstants.map_width;
        unit_y = (double) getHeight() / GameConstants.map_height;
        g.drawImage(ImageManager.game_background, 0, 0, getWidth(), getHeight(), null);
        paintWalls(_data.walls, g);
        paintFoods(_data.foods, g);
        paintHoles(_data.holes, g);
        paintSingles(_data.stones, ImageManager.stone_image, g);
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

    static int dirToAngle(Point dir) {
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
                    g.drawImage(ImageManager.wall_edges[dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                } else if (i == wall.size()) {
                    g.drawImage(ImageManager.wall_edges[(dirToAngle(dir) + 2) % 4], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                } else {
                    if (dir.equalTo(old_dir)) {
                        g.drawImage(ImageManager.wall_middles[dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                    } else {
                        g.drawImage(ImageManager.wall_trans[dirToAngle(dir.sub(old_dir))], ui_x, ui_y, (int) unit_x + 3, (int) unit_y + 3, null);
                    }
                }
                old_dir = dir;
            }
        }
    }

    void paintSnake(int index, Graphics g) {
        Snake snake = _data.snakes[index];
        if (snake.state == Snake.State.IN || snake.state == Snake.State.DEAD) {
            return;
        }
        Point dir, old_dir = new Point(), pos = snake.body.elementAt(0);
        for (int i = 1; i <= snake.body.size(); i++) {
            if (i == snake.body.size()) {
                dir = old_dir;
            } else {
                dir = snake.body.elementAt(i);
            }
            int ui_x = (int) (pos.x * unit_x) - 1, ui_y = (int) (pos.y * unit_y) - 1;
            if (i == 1) {
                if (_data.map.elementAt(pos).on_snakes[index])
                    g.drawImage(ImageManager.snake_heads[index][dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                pos = pos.add(snake.body.elementAt(i));
            } else if (i == snake.body.size()) {
                if (_data.map.elementAt(pos).on_snakes[index])
                    g.drawImage(ImageManager.snake_tails[index][dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
            } else {
                if (dir.equalTo(old_dir)) {
                    if (_data.map.elementAt(pos).on_snakes[index])
                        g.drawImage(ImageManager.snake_middles[index][dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                } else {
                    if (_data.map.elementAt(pos).on_snakes[index])
                        g.drawImage(ImageManager.snake_trans[index][dirToAngle(dir.sub(old_dir))], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
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
            g.drawImage(ImageManager.food_images[food.type.ordinal()], ui_x, ui_y, (int) unit_x, (int) unit_y, null);
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
            g.drawImage(ImageManager.hole_image, ui_x, ui_y, (int) unit_x, (int) unit_y, null);
        }
    }
}
