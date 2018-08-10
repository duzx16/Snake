import game_data.GameConstants;
import game_data.Hole;
import game_data.Snake;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameStepper implements ActionListener, ChangeListener {
    private GameMain _main;
    final Counter _count = new Counter(), connect_counter = new Counter();
    private int plus_count = 0;
    private int _food_wait = 0;

    private static int min_interval = GameConstants.timer_interval, food_wait_total = GameConstants.food_wait / min_interval / GameConstants.min_game_interval, hole_wait_total = GameConstants.hole_wait / min_interval / GameConstants.min_game_interval;

    public void setSpeed(int speed) {
        this._speed = speed;
    }

    private int _speed = GameConstants.max_speed / GameConstants.default_speed;
    private Timer _timer;

    public GameStepper(GameMain main) {
        _main = main;
        _timer = new Timer(min_interval, this);
    }

    public void stepStart() {
        _timer.start();
    }

    public void stepPause() {
        _timer.stop();
    }

    public void stateChanged(ChangeEvent e) {
        if (_main.is_server_mode()) {
            setSpeed(GameConstants.max_speed / ((JSlider) e.getSource()).getValue());
        } else {
            _main.clientListener.sendSpeedData(((JSlider) e.getSource()).getValue());
        }
    }

    public void actionPerformed(ActionEvent e) {
        plus_count += 1;
        if (plus_count == GameConstants.plus_one_interval) {
            plus_count = 0;
            _main.maskLayer.plusOneStep();
        }
        _main.maskLayer.deathStep();
        _main.maskLayer.repaint();

        if (_main.is_server_mode()) {
            if (_count.getNum() % GameConstants.min_game_interval == 0) {
                _main.data.lock.writeLock().lock();
                for (int i = 0; i < 2; i++) {
                    if (!_main.data.is_lives[i]) {
                        continue;
                    }
                    if (_main.data.snakes[i].state == Snake.State.IN) {
                        _main.data.snakes[i].hole_wait += 1;
                        if (_main.data.snakes[i].hole_wait >= hole_wait_total) {
                            _main.data.snakes[i].hole_wait = 0;
                            int hole_index = GameLogic.randomHole(_main.data.holes);
                            Hole selected_hole = _main.data.holes.get(hole_index);
                            if (selected_hole != null) {
                                _main.serverListener.snake_holes[i] = hole_index;
                                GameLogic.snakeOut(i, _main.data, selected_hole);
                            }
                        }
                    }
                    if (_count.getNum() / GameConstants.min_game_interval % _speed == 0) {
                        GameLogic.snakeStep(i, _main.data, _main.data.dirs[i]);
                        _main.serverListener.snake_dirs[i] = _main.data.dirs[i];
                    }
                }
                if (_count.getNum() / GameConstants.min_game_interval % _speed == 0) {
                    _count.clear();
                    GameLogic.snakeCrash(_main.data);
                }
                for (int i = 0; i < 2; i++) {
                    if (_main.data.snakes[i].state == Snake.State.DEAD) {
                        GameLogic.killSnake(i, _main.data);
                    }
                }
                for (int i = 0; i < 2; i++) {
                    if (_main.data.snake_nums[i] < 0) {
                        _main.data.is_lives[i] = false;
                    }
                }
                if (_main.data.foods.size() == 0) {
                    _food_wait += 1;
                    if (_food_wait == food_wait_total) {
                        _food_wait = 0;
                        GameLogic.addFoods(_main.data.map, _main.data.foods);
                    }
                }
                _main.data.lock.writeLock().unlock();
                _main.serverListener.sendData();
                _main.statistics.repaint();
                _main.ui.repaint();
                if (!_main.data.is_lives[0] || !_main.data.is_lives[1]) {
                    _main.gameOver();
                }
            }
            _count.add();
        } else {
            synchronized (_count) {
                _count.add();
                if (_count.getNum() >= 5) {
                    _count.clear();
                    _main.clientListener.sendNull();
                }
            }
        }
        synchronized (connect_counter) {
            connect_counter.add();
            if (connect_counter.getNum() > GameConstants.client_patience / min_interval) {
                JOptionPane.showMessageDialog(_main, "连接已断开，请重新连接");
                _main.disconnectGame();
                _main.gameOver();
            }
        }
    }
}

class Counter {
    private int num;

    public Counter() {
        num = 0;
    }

    public void add() {
        num += 1;
    }

    public void clear() {
        num = 0;
    }

    public int getNum() {
        return num;
    }
}
