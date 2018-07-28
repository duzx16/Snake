package socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SendThread extends Thread {
    private Socket _socket;
    private OutputStream _writer;
    private DataListener _listener;
    private final ArrayList<byte[]> _buffer;
    private byte[] _data;
    public volatile boolean exit = false;


    public SendThread(Socket socket, ArrayList<byte[]> buffer, DataListener listener) {
        _socket = socket;
        _buffer = buffer;
        _listener = listener;
        try {
            _writer = _socket.getOutputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // todo 实现连接断开时的处理
    public void run() {
        while (!exit) {
            synchronized (_buffer) {
                if (_buffer.isEmpty()) {
                    try {
                        _buffer.wait();
                    } catch (InterruptedException error) {
                        continue;
                    }
                }
                _data = _buffer.remove(0);
                while (!exit) {
                    try {
                        _writer.write(NumberUtil.intToByte4(_data.length));
                        _writer.write(NumberUtil.intToByte4(0));
                        _writer.write(_data);
                        _writer.flush();
                        break;
                    } catch (IOException error) {
                        if (!exit) {
                            exit = true;
                            _listener.connectStop(error);
                        }
                        break;
                    }
                }
            }
        }
        System.out.println("Send end");
    }
}
