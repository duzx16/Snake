package socket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

// 数据发送线程，使用生产者-消费者模式来发送buffer中的数据
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
                try {
                    _writer.write(NumberUtil.intToByte4(_data.length));
                    _writer.write(NumberUtil.intToByte4(0));
                    _writer.write(_data);
                    _writer.flush();
                } catch (IOException error) {
                    if (!exit) {
                        exit = true;
                        _listener.connectStop(error);
                    }
                }
            }
        }
        System.out.println("Send end");
    }
}
