package gamedata;

public enum Dir {
    UP, DOWN, LEFT, RIGHT, NULL;

   public  static Dir fromPoint(Point dir) {
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