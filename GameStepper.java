import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameStepper implements ActionListener, ChangeListener {
    private GameMain _main;
    final public Counter _count = new Counter();
    private int _food_wait = 0;

    static int min_interval = 50, food_wait_total = 2000 / min_interval;

    public void setSpeed(int speed) {
        this._speed = speed;
    }

    private int _speed = 2;
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
            setSpeed(((JSlider) e.getSource()).getValue());
        } else {
            _main.clientListener.sendSpeedData(((JSlider) e.getSource()).getValue());
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (_main.is_server_mode()) {
            _main.data.lock.writeLock().lock();
            _main.data.snakes[0].moved = _main.data.snakes[1].moved = false;
            for (int i = 0; i < 2; i++) {
                if (!_main.data.is_lives[i]) {
                    continue;
                }
                if (_main.data.snakes[i].state == Snake.State.IN) {
                    _main.data.snakes[i].hole_wait += 1;
                    if (_main.data.snakes[i].hole_wait >= food_wait_total) {
                        int hole_index = GameLogic.randomHole(_main.data.holes);
                        Hole selected_hole = _main.data.holes.get(hole_index);
                        _main.serverListener.snake_holes[i] = hole_index;
                        GameLogic.snakeOut(i, _main.data, selected_hole);
                    }
                }
                if (_count.getNum() % _speed == 0) {
                    _count.clear();
                    GameLogic.snakeStep(i, _main.data, _main.data.dirs[i]);
                    _main.serverListener.snake_dirs[i] = _main.data.dirs[i];
                }
            }
            for (int i = 0; i < 2; i++) {
                if (_main.data.snake_nums[i] < 0) {
                    _main.data.is_lives[i] = false;
                }
            }
            if (!(_main.data.is_lives[0] || _main.data.is_lives[1])) {
                _main.gameOver();
            }
            if (_main.data.foods.size() == 0) {
                _food_wait += 1;
                if (_food_wait == food_wait_total) {
                    _food_wait = 0;
                    GameLogic.addFoods(_main.data.map, _main.data.foods, 2);
                }
            }
            _main.data.lock.writeLock().unlock();
            _main.serverListener.sendData();
            _main.ui.repaint();
            _main.statistics.repaint();
            _count.add();
        } else {
            synchronized (_count) {
                _count.add();
                if (_count.getNum() > 2000 / min_interval) {
                    JOptionPane.showMessageDialog(_main, "连接已断开，请重新连接");
                    _main.gameOver();
                }
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
