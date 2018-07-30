package game_data;

public class MapEle {
    public enum EleType {FOOD, SNAKE, WALL, HOLE, STONE, NULL}

    public EleType type;
    public Object obj;

    public MapEle(EleType type, Object obj) {
        this.type = type;
        this.obj = obj;
    }
}