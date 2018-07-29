package socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ReceiveThread extends Thread {
    Socket _socket;
    InputStream _input;
    DataListener _listener;
    public volatile boolean exit = false;

    public ReceiveThread(Socket socket, DataListener listener) {
        _socket = socket;
        _listener = listener;
        try {
            _input = _socket.getInputStream();
        } catch (IOException error) {
            System.out.println(error.getMessage());
        }
    }

    static byte[] read4Byte(InputStream input) throws IOException {
        byte[] buffer = new byte[4];
        int len = 0;
        while (len < 4) {
            int get_len = input.read(buffer, len, 4 - len);
            if (get_len >= 0) {
                len += get_len;
            }

        }
        return buffer;
    }

    public void run() {
        while (!exit) {
            try {
                int length = NumberUtil.byte4ToInt(read4Byte(_input), 0);
                if (NumberUtil.byte4ToInt(read4Byte(_input), 0) == 0) {
                    byte[] buffer = new byte[length];
                    int read_len = 0;
                    while (!exit && read_len < length) {

                        int get_len = _input.read(buffer, read_len, length - read_len);
                        if (get_len >= 0) {
                            read_len += get_len;
                        }
                    }
                    if (read_len == length)
                        _listener.dataReceived(buffer);
                }

            } catch (IOException error) {
                if (!exit) {
                    exit = true;
                    _listener.connectStop(error);
                }

            }
        }
        System.out.println("Receive end");
    }
}
