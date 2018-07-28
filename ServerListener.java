import socket.DataListener;
import socket.NumberUtil;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

enum MessageType {Message, Process, InitData, DirData, Pause, Speed}

public class ServerListener implements DataListener {

    private GameMain _parent;
    int[] snake_holes = {-1, -1};
    Dir[] snake_dirs = {Dir.NULL, Dir.NULL};


    ServerListener(GameMain main) {
        _parent = main;
    }

    public void connectStop(IOException error) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(_parent, "连接中断，游戏结束");
            _parent.gameOver();
        });
    }


    private void send(byte[] data) {
        synchronized (_parent.sendBuffer) {
            _parent.sendBuffer.add(data);
            _parent.sendBuffer.notify();
        }
    }

    public void sendChatData(String text) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.Message.ordinal()));
            output.write(text.getBytes());
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
            case DirData:
                _parent.data.lock.writeLock().lock();
                Dir dir = Dir.values()[input.nextInt()];
                if (!_parent.data.snakes[1].body.elementAt(1).equalTo(GameLogic._dir_pos[dir.ordinal()])) {
                    _parent.data.dirs[1] = dir;
                }
                _parent.data.lock.writeLock().unlock();
                break;
            case Message:
                byte[] message = new byte[data.length - 4];
                System.arraycopy(data, 4, message, 0, data.length - 4);
                SwingUtilities.invokeLater(() -> _parent.chat_text.append("Server:\n\t" + new String(message) + "\n"));
                break;
            case Speed:
                int speed = input.nextInt();
                SwingUtilities.invokeLater(() -> _parent.speed_slider.setValue(speed));
        }
    }

    void sendInitData() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.InitData.ordinal()));
            int wall_num = _parent.data.walls.size();
            output.write(NumberUtil.intToByte4(wall_num));
            for (int i = 0; i < wall_num; i++) {
                ArrayList<Point> wall = _parent.data.walls.get(i);
                int wall_length = wall.size();
                output.write(NumberUtil.intToByte4(wall_length));
                for (Point aWall : wall) {
                    output.write(NumberUtil.intToByte4(aWall.x));
                    output.write(NumberUtil.intToByte4(aWall.y));
                }
            }
            int food_num = _parent.data.foods.size();
            output.write(NumberUtil.intToByte4(food_num));
            for (Food food :
                    _parent.data.foods) {
                output.write(NumberUtil.intToByte4(food.type.ordinal()));
                output.write(NumberUtil.intToByte4(food.pos.x));
                output.write(NumberUtil.intToByte4(food.pos.y));
            }
            int stone_num = _parent.data.stones.size();
            output.write(NumberUtil.intToByte4(stone_num));
            for (Point stone :
                    _parent.data.stones) {
                output.write(NumberUtil.intToByte4(stone.x));
                output.write(NumberUtil.intToByte4(stone.y));
            }
            int hole_num = _parent.data.holes.size();
            output.write(NumberUtil.intToByte4(hole_num));
            for (Hole hole :
                    _parent.data.holes) {
                output.write(NumberUtil.intToByte4(hole.pos.x));
                output.write(NumberUtil.intToByte4(hole.pos.y));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());

    }

    void sendData() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(NumberUtil.intToByte4(MessageType.Process.ordinal()));
            output.write(NumberUtil.intToByte4(snake_holes[0]));
            output.write(NumberUtil.intToByte4(snake_holes[1]));
            output.write(NumberUtil.intToByte4(snake_dirs[0].ordinal()));
            output.write(NumberUtil.intToByte4(snake_dirs[1].ordinal()));
            for (int i = 0; i < 2; i++) {
                if (i < _parent.data.foods.size()) {
                    Food food = _parent.data.foods.get(i);
                    output.write(NumberUtil.intToByte4(food.type.ordinal()));
                    output.write(NumberUtil.intToByte4(food.pos.x));
                    output.write(NumberUtil.intToByte4(food.pos.y));
                } else {
                    output.write(NumberUtil.intToByte4(Food.FoodType.NULL.ordinal()));
                    output.write(NumberUtil.intToByte4(-1));
                    output.write(NumberUtil.intToByte4(-1));
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        send(output.toByteArray());
        initData();
    }

    void initData() {
        snake_holes[0] = snake_holes[1] = -1;
        snake_dirs[0] = snake_dirs[1] = Dir.NULL;
    }
}


