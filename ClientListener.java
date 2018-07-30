import game_data.*;
import socket.NumberUtil;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ClientListener extends GameListener {

    ClientListener(GameMain parent) {
        super(parent);
    }

    public void sendSpeedData(int speed) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.Speed.ordinal()));
            output.write(NumberUtil.intToByte4(speed));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());
    }

    public void sendContinue() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.Pause.ordinal()));
            output.write(NumberUtil.intToByte4(2));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());
    }

    public void sendPause() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.Pause.ordinal()));
            output.write(NumberUtil.intToByte4(1));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());
    }


    public void sendChatData(String text) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.Message.ordinal()));
            output.write(text.getBytes("UTF-8"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());
    }

    public void sendDirData(Dir dir) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.DirData.ordinal()));
            output.write(NumberUtil.intToByte4(dir.ordinal()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());
    }

    public void dataReceived(byte[] data) {
        if (data.length < 4) {
            return;
        }
        NumberUtil input = new NumberUtil(data);
        MessageType data_type = MessageType.values()[input.nextInt()];
        switch (data_type) {
            case Message:
                byte[] message = new byte[data.length - 4];
                System.arraycopy(data, 4, message, 0, data.length - 4);
                SwingUtilities.invokeLater(() -> {
                    _parent.chat_text.append("Server:\n  " + new String(message, Charset.forName("UTF-8")) + "\n");
                });
                SwingUtilities.invokeLater(() -> {
                    JScrollBar bar = _parent.chat_scroll.getVerticalScrollBar();
                    bar.setValue(bar.getMaximum());
                });
                break;
            case Process:
                synchronized (_parent.stepper._count) {
                    _parent.stepper._count.clear();
                }
                _parent.data.lock.writeLock().lock();
                for (int i = 0; i < 2; i++) {
                    int selected = input.nextInt();
                    if (selected != -1) {
                        GameLogic.snakeOut(i, _parent.data, _parent.data.holes.get(selected));
                    }
                }
                _parent.data.snakes[0].moved = _parent.data.snakes[1].moved = false;
                for (int i = 0; i < 2; i++) {
                    Dir dir = Dir.values()[input.nextInt()];
                    if (dir != Dir.NULL) {
                        GameLogic.snakeStep(i, _parent.data, dir);
                    }
                }
                _parent.data.foods.clear();
                for (int i = 0; i < 2; i++) {
                    Food.FoodType foodType = Food.FoodType.values()[input.nextInt()];
                    int x = input.nextInt();
                    int y = input.nextInt();
                    if (foodType != Food.FoodType.NULL) {
                        Food food = new Food(foodType, new Point(x, y));
                        _parent.data.foods.add(food);
                        _parent.data.map.elementAt(x, y).type = MapEle.EleType.FOOD;
                        _parent.data.map.elementAt(x, y).obj = food;
                    }
                }
                for (int i = 0; i < 2; i++) {
                    if (_parent.data.snake_nums[i] < 0) {
                        _parent.data.is_lives[i] = false;
                    }
                }
                _parent.data.lock.writeLock().unlock();
                _parent.ui.repaint();
                _parent.statistics.repaint();
                if (!_parent.data.is_lives[0] || !_parent.data.is_lives[1]) {
                    _parent.gameOver();
                }
                break;
            case InitData:
                _parent.data.lock.writeLock().lock();
                for (int i = 0; i < GameData.MAP_WIDTH; i++) {
                    for (int j = 0; j < GameData.MAP_HEIGHT; j++) {
                        _parent.data.map.setElementAt(new MapEle(MapEle.EleType.NULL, null), i, j);
                    }
                }
                _parent.data.walls.clear();
                int wall_num = input.nextInt();
                for (int i = 0; i < wall_num; i++) {
                    ArrayList<Point> wall = new ArrayList<>();
                    int wall_length = input.nextInt();
                    for (int j = 0; j < wall_length; ++j) {
                        int x = input.nextInt();
                        int y = input.nextInt();
                        wall.add(new Point(x, y));
                        _parent.data.map.elementAt(x, y).type = MapEle.EleType.WALL;
                        _parent.data.map.elementAt(x, y).obj = wall;
                    }
                    _parent.data.walls.add(wall);
                }
                _parent.data.foods.clear();
                int food_num = input.nextInt();
                for (int i = 0; i < food_num; i++) {
                    Food.FoodType foodType = Food.FoodType.values()[input.nextInt()];
                    int x = input.nextInt();
                    int y = input.nextInt();
                    if (foodType != Food.FoodType.NULL) {
                        Food food = new Food(foodType, new Point(x, y));
                        _parent.data.foods.add(food);
                        _parent.data.map.elementAt(x, y).type = MapEle.EleType.FOOD;
                        _parent.data.map.elementAt(x, y).obj = food;
                    }

                }
                _parent.data.stones.clear();
                int stone_num = input.nextInt();
                for (int i = 0; i < stone_num; ++i) {
                    int x = input.nextInt();
                    int y = input.nextInt();
                    Point stone = new Point(x, y);
                    _parent.data.stones.add(new Point(x, y));
                    _parent.data.map.elementAt(stone).type = MapEle.EleType.STONE;
                    _parent.data.map.elementAt(stone).obj = stone;
                }
                _parent.data.holes.clear();
                int hole_num = input.nextInt();
                for (int i = 0; i < hole_num; ++i) {
                    int x = input.nextInt();
                    int y = input.nextInt();
                    Hole hole = new Hole(new Point(x, y), false);
                    _parent.data.holes.add(hole);
                    _parent.data.map.elementAt(x, y).type = MapEle.EleType.HOLE;
                    _parent.data.map.elementAt(x, y).obj = hole;
                }
                GameLogic.initSnake(0, _parent.data);
                GameLogic.initSnake(1, _parent.data);
                _parent.data.snake_nums[0] = _parent.data.snake_nums[1] = 20;
                _parent.data.scores[0] = _parent.data.scores[1] = 0;
                _parent.data.is_lives[0] = _parent.data.is_lives[1] = true;
                _parent.data.lock.writeLock().unlock();
                _parent.setContentPane(_parent.game_panel);
                _parent.validate();
                break;
            case Pause:
                int pause = input.nextInt();
                if (pause == 1) {
                    SwingUtilities.invokeLater(() -> {
                        _parent.is_pause = 1;
                        _parent.pauseGame();
                    });
                } else if (pause == 0) {
                    SwingUtilities.invokeLater(() -> {
                        _parent.is_pause = 0;
                        _parent.pauseGame();
                    });
                } else {
                    _parent.is_pause = 2;
                    SwingUtilities.invokeLater(() -> _parent.continueGame());
                }
        }
    }
}
