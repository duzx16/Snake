package game_data;

public class Snake {
    public enum State {ENTER, IN, FREE}

    public Point tail;
    public State state = State.FREE;
    public int length = 0, hole_wait = 0;
    public boolean moved = false;
    public MyDeque<Point> body = new MyDeque<>(GameConstants.map_height * GameConstants.map_width);

    public int size() {
        return body.size();
    }
}