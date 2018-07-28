package socket;

import java.io.IOException;

public interface DataListener {
    void dataReceived(byte[] data);
    void connectStop(IOException error);
}
