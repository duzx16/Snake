package game_data;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameData {
    public static final int MAP_WIDTH = 30, MAP_HEIGHT = 30;
    public Snake[] snakes = {new Snake(), new Snake()};
    public ArrayList<Food> foods = new ArrayList<>();
    public ArrayList<ArrayList<Point>> walls = new ArrayList<>();
    public ArrayList<Hole> holes = new ArrayList<>();
    public ArrayList<Point> stones = new ArrayList<>();
    public GameMap map = new GameMap();
    public Dir[] dirs = {Dir.UP, Dir.UP};
    public int[] snake_nums = {5, 5}, scores = {0, 0};
    public boolean[] is_lives = {true, true};
    public ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}
