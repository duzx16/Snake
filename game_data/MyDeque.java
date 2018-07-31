package game_data;

public class MyDeque<E> {
    private int _max_len, _length, _start;
    private E[] _data;

    @SuppressWarnings("unchecked")
    public MyDeque(int max_len) {
        _data = (E[])new Object[max_len];
        this._max_len = max_len;
        _length = _start = 0;
    }

    public E elementAt(int index) {
        return _data[(_start + index) % _max_len];
    }

    public void addFirst(E p) {
        _length += 1;
        _start -= 1;
        if (_start < 0) {
            _start += _max_len;
        }
        _data[_start] = p;
    }

    public void setElementAt(E p, int index) {
        _data[(_start + index) % _max_len] = p;
    }

    public void removeFirst() {
        _length -= 1;
        _start += 1;
        if (_start >= _max_len) {
            _start -= _max_len;
        }
    }

    public void addLast(E p) {
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