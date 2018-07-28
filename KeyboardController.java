import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyboardController extends KeyAdapter {
    private GameMain _main;

    public KeyboardController(GameMain main) {
        _main = main;
    }

    public void keyPressed(KeyEvent e) {
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
        if (_main.is_server_mode()) {
            _main.data.lock.writeLock().lock();
            if (!_main.data.snakes[0].body.elementAt(1).equalTo(GameLogic._dir_pos[dir.ordinal()])) {
                _main.data.dirs[0] = dir;
            }
            _main.data.lock.writeLock().unlock();
        } else {
            _main.clientListener.sendDirData(dir);
        }
    }
}
