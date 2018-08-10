package game_data;

public class Snake {
    public enum State {ENTER, IN, FREE, DEAD}

    public Point tail;
    public State state = State.FREE;
    public int length = 0, hole_wait = 0;
    public MyDeque<Point> body = new MyDeque<>(GameConstants.map_height * GameConstants.map_width);

    public int size() {
        return body.size();
    }
}