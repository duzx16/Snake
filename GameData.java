import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameData {
    static final int MAP_WIDTH = 30, MAP_HEIGHT = 30;
    Snake[] snakes = {new Snake(), new Snake()};
    ArrayList<Food> foods = new ArrayList<>();
    ArrayList<ArrayList<Point>> walls = new ArrayList<>();
    ArrayList<Hole> holes = new ArrayList<>();
    ArrayList<Point> stones = new ArrayList<>();
    GameMap map = new GameMap();
    Dir[] dirs = {Dir.UP, Dir.UP};
    int[] snake_nums = {5, 5}, scores = {0, 0};
    boolean[] is_lives = {true, true};
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}

enum Dir {
    UP, DOWN, LEFT, RIGHT, NULL;

    static Dir fromPoint(Point dir) {
        if (dir.x == 0) {
            if (dir.y == -1) {
                return UP;
            } else {
                return DOWN;
            }
        } else if (dir.x == 1) {
            return RIGHT;
        } else {
            return LEFT;
        }
    }
}

class Point {
    public int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point() {
        x = y = 0;
    }

    Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    boolean equalTo(Point p) {
        return x == p.x && y == p.y;
    }

    void bound() {
        if (x < 0) {
            x += GameData.MAP_WIDTH;
        } else {
            x = x % GameData.MAP_WIDTH;
        }

        if (y < 0) {
            y += GameData.MAP_HEIGHT;
        } else {
            y = y % GameData.MAP_HEIGHT;
        }
    }

    Point minus() {
        return new Point(-x, -y);
    }

    public String toString() {
        return x + "," + y;
    }
}


class Food {
    enum FoodType {Apple, Banana, Cherry, Melon, Orange, NULL}

    Point pos;
    FoodType type;

    Food(FoodType type, Point pos) {
        this.pos = pos;
        this.type = type;
    }
}


class MapEle {
    enum EleType {FOOD, SNAKE, WALL, HOLE, STONE, NULL}

    EleType type = EleType.NULL;
    Object obj = null;

    MapEle(EleType type, Object obj) {
        this.type = type;
        this.obj = obj;
    }

    MapEle() {
    }

}

class GameMap {
    private MapEle[][] _map = new MapEle[GameData.MAP_WIDTH][GameData.MAP_HEIGHT];

    MapEle elementAt(Point p) {
        return elementAt(p.x, p.y);
    }

    MapEle elementAt(int x, int y) {
        return _map[x][y];
    }

    void setElementAt(MapEle ele, int x, int y) {
        _map[x][y] = ele;
    }

    void setElementAt(MapEle ele, Point p) {
        setElementAt(ele, p.x, p.y);
    }
}

class Snake {
    enum State {ENTER, IN, FREE}


    Point tail;
    State state = State.FREE;
    int length = 0, hole_wait = 0;
    boolean moved = false;
    MyDeque body = new MyDeque(GameData.MAP_HEIGHT * GameData.MAP_WIDTH);

    int size() {
        return body.size();
    }
}

class MyDeque {
    private int _max_len, _length, _start;
    private Point[] _data;

    MyDeque(int max_len) {
        _data = new Point[max_len];
        this._max_len = max_len;
        _length = _start = 0;
    }

    Point elementAt(int index) {
        return _data[(_start + index) % _max_len];
    }

    void addFirst(Point p) {
        _length += 1;
        _start -= 1;
        if (_start < 0) {
            _start += _max_len;
        }
        _data[_start] = p;
    }

    void setElementAt(Point p, int index) {
        _data[(_start + index) % _max_len] = p;
    }

    void removeFirst() {
        _length -= 1;
        _start += 1;
        if (_start >= _max_len) {
            _start -= _max_len;
        }
    }

    void addLast(Point p) {
        _data[(_start + _length) % _max_len] = p;
        _length += 1;
    }

    void removeLast() {
        _length -= 1;
    }

    void clear() {
        _length = 0;
    }

    int size() {
        return _length;
    }
}

class Hole {
    Point pos;
    boolean used;

    Hole(Point pos, boolean used) {
        this.pos = pos;
        this.used = used;
    }
}