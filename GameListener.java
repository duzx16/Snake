import socket.DataListener;

import javax.swing.*;
import java.io.IOException;

enum MessageType {Message, Process, InitData, DirData, Pause, Speed}

public class GameListener implements DataListener {
    volatile boolean listening = true;
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
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(_parent, "连接中断，游戏结束");
                _parent.disconnectGame();
                _parent.gameOver();
                listening = false;
            });
        }
    }
}
