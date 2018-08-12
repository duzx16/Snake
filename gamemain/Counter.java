package gamemain;

public class Counter {
    private int num;

    public Counter() {
        num = 0;
    }

    public void add() {
        num += 1;
    }

    public void clear() {
        num = 0;
    }

    public int getNum() {
        return num;
    }
}
