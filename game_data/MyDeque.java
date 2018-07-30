package game_data;

public class MyDeque {
    private int _max_len, _length, _start;
    private Point[] _data;

    public MyDeque(int max_len) {
        _data = new Point[max_len];
        this._max_len = max_len;
        _length = _start = 0;
    }

    public Point elementAt(int index) {
        return _data[(_start + index) % _max_len];
    }

    public void addFirst(Point p) {
        _length += 1;
        _start -= 1;
        if (_start < 0) {
            _start += _max_len;
        }
        _data[_start] = p;
    }

    public void setElementAt(Point p, int index) {
        _data[(_start + index) % _max_len] = p;
    }

    public void removeFirst() {
        _length -= 1;
        _start += 1;
        if (_start >= _max_len) {
            _start -= _max_len;
        }
    }

    public void addLast(Point p) {
        _data[(_start + _length) % _max_len] = p;
        _length += 1;
    }

    public void removeLast() {
        _length -= 1;
    }

    public void clear() {
        _length = 0;
    }

    public int size() {
        return _length;
    }
}