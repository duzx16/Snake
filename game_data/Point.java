package game_data;

public class Point {
    public int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        x = y = 0;
    }

    public Point sub(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public boolean equalTo(Point p) {
        return x == p.x && y == p.y;
    }

    public void bound() {
        if (x < 0) {
            x += GameConstant.map_width;
        } else {
            x = x % GameConstant.map_width;
        }

        if (y < 0) {
            y += GameConstant.map_height;
        } else {
            y = y % GameConstant.map_height;
        }
    }

    public Point minus() {
        return new Point(-x, -y);
    }

    public String toString() {
        return x + "," + y;
    }
}