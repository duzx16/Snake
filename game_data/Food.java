package game_data;

public class Food {
    public enum FoodType {Apple, Banana, Cherry, Melon, Orange, NULL}

    public Point pos;
    public FoodType type;

    public Food(FoodType type, Point pos) {
        this.pos = pos;
        this.type = type;
    }
}
