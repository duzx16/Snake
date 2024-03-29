package gamemain;

import gamedata.Dir;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// 用于接收WASD和上下左右的键盘事件控制游戏
public class KeyboardController extends KeyAdapter {
    private GameMain _main;

    public KeyboardController(GameMain main) {
        _main = main;
    }

    public void keyPressed(KeyEvent e) {
        if (_main.is_server_mode()) {
            Dir dir = Dir.UP;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    dir = Dir.UP;
                    break;
                case KeyEvent.VK_S:
                    dir = Dir.DOWN;
                    break;
                case KeyEvent.VK_A:
                    dir = Dir.LEFT;
                    break;
                case KeyEvent.VK_D:
                    dir = Dir.RIGHT;
                    break;
            }
            _main.data.lock.writeLock().lock();
            if (!_main.data.snakes[0].body.elementAt(1).equalTo(GameLogic.dir_pos[dir.ordinal()])) {
                _main.data.dirs[0] = dir;
            }
            _main.data.lock.writeLock().unlock();
        } else {
            Dir dir = Dir.UP;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    dir = Dir.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    dir = Dir.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    dir = Dir.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    dir = Dir.RIGHT;
                    break;
            }
            _main.clientListener.sendDirData(dir);
        }
    }
}
