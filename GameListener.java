import socket.DataListener;

import javax.swing.*;
import java.io.IOException;

enum MessageType {Message, Process, InitData, DirData, Pause, Speed, Null}

// 完成基本的发送数据和连接错误时的处理
public class GameListener implements DataListener {
    boolean listening = true;
    protected GameMain _parent;

    GameListener(GameMain main) {
        _parent = main;
    }

    protected void send(byte[] data) {
        synchronized (_parent.sendBuffer) {
            _parent.sendBuffer.add(data);
            _parent.sendBuffer.notify();
        }
    }

    public void dataReceived(byte[] data) {

    }

    public synchronized void connectStop(IOException error) {
        if (listening) {
            listening = false;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(_parent, "连接中断，游戏结束");
                _parent.disconnectGame();
                _parent.gameOver();
            });
        }
    }
}
