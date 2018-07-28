package socket;

public class NumberUtil {
    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    public static void intToByte4(int i, byte[] bytes, int off) {
        bytes[3 + off] = (byte) (i & 0xFF);
        bytes[2 + off] = (byte) (i >> 8 & 0xFF);
        bytes[1 + off] = (byte) (i >> 16 & 0xFF);
        bytes[off] = (byte) (i >> 24 & 0xFF);
    }

    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    private byte[] _data;
    private int processed = 0;

    public NumberUtil(byte[] data) {
        _data = data;
    }

    public int nextInt() {
        int result = byte4ToInt(_data, processed);
        processed += 4;
        return result;
    }
}
