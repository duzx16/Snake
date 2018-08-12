package gameui;

import javax.swing.*;
import java.awt.*;

import gamedata.*;
import gamedata.Point;
import gamemain.GameMain;
import imageio.ImageManager;

public class MaskLayer extends JPanel {
    private final static int death_count_total = GameConstants.death_flash_delay / GameConstants.timer_interval;
    private GameMain _parent;
    private double unit_x, unit_y;
    private final MyDeque<Point> plusList = new MyDeque<>(100);
    private final MyDeque<Point>[] snakes = new MyDeque[2];
    private final int[] snake_counts = {0, 0};

    public MaskLayer(GameMain main) {
        super();
        _parent = main;
        for (int i = 0; i < 2; ++i) {
            snakes[i] = new MyDeque<>(GameConstants.map_height * GameConstants.map_width);
        }
    }

    public void addDeadSnake(int index, GameData data) {
        synchronized (snakes) {
            snakes[index].clear();
            for (int i = 0; i < data.snakes[index].size(); i++) {
                snakes[index].addLast(data.snakes[index].body.elementAt(i));
            }
        }
        synchronized (snake_counts) {
            snake_counts[index] = death_count_total;
        }
    }

    public void addPlusOne(Point pos) {
        synchronized (plusList) {
            plusList.addFirst(new Point(pos.x, pos.y));
        }
    }

    public void plusOneStep() {
        synchronized (plusList) {
            for (int i = 0; i < plusList.size(); i++) {
                plusList.elementAt(i).y -= 1;
            }
            while (plusList.size() > 0 && plusList.elementAt(0).y < 1) {
                plusList.clear();
            }
        }
    }

    public void deathStep() {
        synchronized (snake_counts) {
            for (int i = 0; i < 2; i++) {
                if (snake_counts[i] > 0) {
                    snake_counts[i]--;
                }
            }
        }
    }

    public void paint(Graphics g) {
        unit_x = _parent.ui.unit_x;
        unit_y = _parent.ui.unit_y;
        synchronized (snakes) {
            for (int i = 0; i < 2; i++) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, (float) snake_counts[i] / death_count_total));
                paintSnake(i, g2d);
                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 1.0f));
            }
        }

        synchronized (plusList) {
            g.setFont(new Font("Yuanti SC", Font.BOLD, 18));
            for (int i = 0; i < plusList.size(); i++) {
                Point p = plusList.elementAt(i);
                g.setColor(Color.WHITE);
                g.drawString("+1", (int) (p.x * _parent.ui.unit_x), (int) (p.y * _parent.ui.unit_y));
            }
        }
        if (_parent.is_pause < 2) {
            g.setColor(new Color(30, 30, 30, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(new Font("Yuanti SC", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            if (_parent.is_pause == 0 ^ _parent.is_server_mode())
                g.drawString("对方已暂停游戏", getWidth() / 2, getHeight() / 2);
            else {
                g.drawString("游戏已暂停", getWidth() / 2, getHeight() / 2);
            }
        } else {
            if (_parent.data.snakes[0].state == Snake.State.IN || _parent.data.snakes[1].state == Snake.State.IN) {
                g.setFont(new Font("Yuanti SC", Font.BOLD, 30));
                g.setColor(Color.WHITE);
                g.drawString("蛇已入洞", getWidth() / 2, getHeight() / 2);
            }
        }
    }

    void paintSnake(int index, Graphics g) {
        if (snake_counts[index] > 0) {
            MyDeque<Point> snake = snakes[index];
            Point dir, old_dir = new Point(), pos = snake.elementAt(0);
            for (int i = 1; i <= snake.size(); i++) {
                if (i == snake.size()) {
                    dir = old_dir;
                } else {
                    dir = snake.elementAt(i);
                }
                int ui_x = (int) (pos.x * unit_x) - 1, ui_y = (int) (pos.y * unit_y) - 1;
                if (i == 1) {
                    g.drawImage(ImageManager.snake_heads[index][MainUI.dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                    pos = pos.add(snake.elementAt(i));
                } else if (i == snake.size()) {
                    g.drawImage(ImageManager.snake_tails[index][MainUI.dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                } else {
                    if (dir.equalTo(old_dir)) {
                        g.drawImage(ImageManager.snake_middles[index][MainUI.dirToAngle(dir)], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                    } else {
                        g.drawImage(ImageManager.snake_trans[index][MainUI.dirToAngle(dir.sub(old_dir))], ui_x, ui_y, (int) unit_x + 2, (int) unit_y + 2, null);
                    }
                    pos = pos.add(snake.elementAt(i));
                }
                pos.bound();
                old_dir = dir;
            }
        }
    }
}
