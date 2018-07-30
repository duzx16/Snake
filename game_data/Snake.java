package game_data;

public class Snake {
    public enum State {ENTER, IN, FREE}

    public Point tail;
    public State state = State.FREE;
    public int length = 0, hole_wait = 0;
    public boolean moved = false;
    public MyDeque body = new MyDeque(GameData.MAP_HEIGHT * GameData.MAP_WIDTH);

    public int size() {
        return body.size();
    }
}