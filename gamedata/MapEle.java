package gamedata;

public class MapEle {
    public enum EleType {FOOD, WALL, HOLE, STONE, NULL}

    public EleType type;
    public Object obj;
    public boolean[] on_snakes = {false, false};

    public boolean hasSnake() {
        return on_snakes[0] || on_snakes[1];
    }

    public MapEle(EleType type, Object obj, boolean on_snake1, boolean on_snake2) {
        this.type = type;
        this.obj = obj;
        this.on_snakes[0] = on_snake1;
        this.on_snakes[1] = on_snake2;
    }
}