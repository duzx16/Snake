import game_data.*;

import java.util.Calendar;
import java.util.Random;
import java.util.ArrayList;

class GameLogic {
    private static Random _random = new Random(Calendar.getInstance().getTimeInMillis());
    static GameMain _parent;
    static final Point[] _dir_pos = {new Point(0, -1), new Point(0, 1), new Point(-1, 0), new Point(1, 0)};

    static void killSnake(int index, GameData data) {
        _parent.maskLayer.addDeadSnake(index, data);
        data.snake_nums[index] -= 1;
        initSnake(index, data);
    }

    static void initSnake(int index, GameData data) {
        Snake snake = data.snakes[index];
        Point pos = snake.body.elementAt(0);
        for (int i = 1; i <= snake.body.size(); i++) {
            data.map.elementAt(pos).on_snakes[index] = false;
            if (data.map.elementAt(pos).type == MapEle.EleType.HOLE) {
                ((Hole) data.map.elementAt(pos).obj).used = false;
            }
            if (i < snake.body.size()) {
                pos = pos.add(snake.body.elementAt(i));
                pos.bound();
            }
        }
        snake.body.clear();
        snake.state = Snake.State.IN;
        snake.length = 2;
        snake.hole_wait = 0;
    }

    private static boolean neighborSpare(GameMap map, Point pos) {
        for (int j = 0; j < 4; j++) {
            Point neighbor = pos.add(_dir_pos[j]);
            neighbor.bound();
            if (map.elementAt(neighbor).type != MapEle.EleType.NULL) {
                return false;
            }
        }
        return true;
    }

    private static Point randomSpare(GameMap map) {
        int x = _random.nextInt(GameConstants.map_width), y = _random.nextInt(GameConstants.map_height);
        while (map.elementAt(x, y).type != MapEle.EleType.NULL || map.elementAt(x, y).on_snakes[0] || map.elementAt(x, y).on_snakes[1]) {
            x = _random.nextInt(GameConstants.map_width);
            y = _random.nextInt(GameConstants.map_height);
        }
        return new Point(x, y);
    }

    private static Dir randomDir(GameMap map, Point pos, Point last_dir) {
        Dir[] choices = new Dir[4];
        int n = 0;
        for (int i = 0; i < 4; i++) {
            if (!_dir_pos[i].equalTo(last_dir)) {
                Point new_pos = pos.add(_dir_pos[i]);
                if (new_pos.x >= 0 && new_pos.x < GameConstants.map_width && new_pos.y >= 0 && new_pos.y < GameConstants.map_height) {
                    if((map.elementAt(new_pos).type == MapEle.EleType.NULL || map.elementAt(new_pos).type == MapEle.EleType.FOOD) && !map.elementAt(new_pos).hasSnake())
                    {
                        choices[n] = Dir.values()[i];
                        n++;
                    }
                }
            }
        }
        if (n > 0)
            return choices[_random.nextInt(n)];
        return null;
    }

    static int randomHole(ArrayList<Hole> holes) {
        int choice = _random.nextInt(holes.size());
        while (holes.get(choice).used) {
            choice = (choice + 1) % holes.size();
        }
        return choice;
    }

    static void initWalls(GameMap map, ArrayList<ArrayList<Point>> walls) {
        walls.clear();
        for (int i = 0; i < GameConstants.wall_num; i++) {
            ArrayList<Point> wall = new ArrayList<>();
            Point dir = new Point(0, 0), pos = randomSpare(map);
            wall.add(pos);
            map.elementAt(pos).type = MapEle.EleType.WALL;
            map.elementAt(pos).obj = wall;
            do {
                Dir next = randomDir(map, pos, dir.minus());
                if (next == null)
                    break;
                Point next_dir = _dir_pos[next.ordinal()];
                pos = pos.add(next_dir);
                map.elementAt(pos).type = MapEle.EleType.WALL;
                map.elementAt(pos).obj = wall;
                wall.add(pos);
                dir = next_dir;
            }
            while (_random.nextFloat() < 0.93);
            walls.add(wall);
        }
    }

    static void addFoods(GameMap map, ArrayList<Food> foods) {
        foods.clear();
        for (int i = 0; i < GameConstants.food_num; i++) {
            Point pos = randomSpare(map);
            while (!neighborSpare(map, pos)) {
                pos = randomSpare(map);
            }
            int choice = _random.nextInt(Food.FoodType.values().length - 1);
            Food food = new Food(Food.FoodType.values()[choice], pos);
            map.elementAt(pos).type = MapEle.EleType.FOOD;
            map.elementAt(pos).obj = food;
            foods.add(food);
        }
    }

    static void initHoles(GameMap map, ArrayList<Hole> holes) {
        holes.clear();
        for (int i = 0; i < GameConstants.hole_num; i++) {
            Point pos = randomSpare(map);
            while (!neighborSpare(map, pos) || pos.x == 0 || pos.x == GameConstants.map_width - 1 || pos.y == 0 || pos.y == GameConstants.map_height - 1) {
                pos = randomSpare(map);
            }
            Hole hole = new Hole(pos, false);
            map.elementAt(pos).type = MapEle.EleType.HOLE;
            map.elementAt(pos).obj = hole;
            holes.add(hole);
        }
    }

    static void initStones(GameMap map, ArrayList<Point> stones) {
        stones.clear();
        for (int i = 0; i < GameConstants.stone_num; i++) {
            Point pos = randomSpare(map);
            while (!neighborSpare(map, pos)) {
                pos = randomSpare(map);
            }
            map.elementAt(pos).type = MapEle.EleType.STONE;
            map.elementAt(pos).obj = pos;
            stones.add(pos);
        }
    }

    private static void cutTail(GameData data, int index) {
        Snake snake = data.snakes[index];
        MapEle tail_ele = data.map.elementAt(snake.tail);
        tail_ele.on_snakes[index] = false;
        if (tail_ele.type == MapEle.EleType.HOLE) {
            Point last = snake.body.elementAt(snake.body.size() - 1);
            if (last.x != 0 || last.y != 0) {
                ((Hole) tail_ele.obj).used = false;
            }
        }
        snake.tail = snake.tail.sub(snake.body.elementAt(snake.size() - 1));
        snake.tail.bound();
        snake.body.removeLast();
    }

    static void snakeStep(int index, GameData data, Dir dir) {
        Snake snake = data.snakes[index];
        if (snake.size() > 0) {
            Point head_pos = snake.body.elementAt(0).add(_dir_pos[dir.ordinal()]);
            head_pos.bound();
            switch (snake.state) {
                case FREE:
                    switch (data.map.elementAt(head_pos).type) {
                        case FOOD:
                            for (int i = 0; i < data.foods.size(); i++) {
                                if (data.foods.get(i).pos.equalTo(head_pos)) {
                                    data.foods.remove(i);
                                    break;
                                }
                            }
                            snake.body.addFirst(head_pos);
                            snake.body.setElementAt(_dir_pos[dir.ordinal()].minus(), 1);
                            data.map.elementAt(head_pos).type = MapEle.EleType.NULL;
                            data.map.elementAt(head_pos).obj = null;
                            data.map.elementAt(head_pos).on_snakes[index] = true;
                            data.scores[index] += 1;
                            _parent.maskLayer.addPlusOne(head_pos);
                            break;
                        case WALL:
                        case STONE:
                            snake.state = Snake.State.DEAD;
                            break;
                        case HOLE:
                            if (((Hole) data.map.elementAt(head_pos).obj).used) {
                                killSnake(index, data);
                            } else {
                                snake.state = Snake.State.ENTER;
                                ((Hole) (data.map.elementAt(head_pos).obj)).used = true;
                                snake.length = snake.body.size();
                                snake.body.addFirst(head_pos);
                                snake.body.setElementAt(_dir_pos[dir.ordinal()].minus(), 1);
                                cutTail(data, index);
                            }
                            break;
                        case NULL:
                            if (data.map.elementAt(head_pos).on_snakes[index] && !head_pos.equalTo(snake.tail)) {
                                snake.state = Snake.State.DEAD;
                            } else {
                                snake.body.addFirst(head_pos);
                                snake.body.setElementAt(_dir_pos[dir.ordinal()].minus(), 1);
                                cutTail(data, index);
                                data.map.elementAt(head_pos).on_snakes[index] = true;
                            }

                            break;
                    }
                    break;
                case ENTER:
                    cutTail(data, index);
                    if (snake.body.size() == 1) {
                        ((Hole) (data.map.elementAt(snake.body.elementAt(0)).obj)).used = false;
                        snake.hole_wait = 0;
                        snake.state = Snake.State.IN;
                    }
                    break;
            }
        }
    }


    static void snakeCrash(GameData data) {
        for (int index = 0; index < 2; index++) {
            int other = (index + 1) % 2;
            if (data.snakes[index].body.size() > 0) {
                Point head_pos = data.snakes[index].body.elementAt(0);
                if (data.map.elementAt(head_pos).on_snakes[other]) {
                    data.snakes[index].state = Snake.State.DEAD;
                }
            }
        }
    }


    static void snakeOut(int index, GameData data, Hole hole) {
        data.snakes[index].body.clear();
        data.snakes[index].state = Snake.State.FREE;
        hole.used = true;
        data.snakes[index].body.addFirst(hole.pos);
        data.dirs[index] = GameLogic.randomDir(data.map, hole.pos, new Point());
        if (data.dirs[index] == null) {
            data.dirs[index] = Dir.UP;
        }
        Point stay = new Point();
        for (int j = 1; j < data.snakes[index].length; j++) {
            data.snakes[index].body.addLast(stay);
        }
        data.snakes[index].tail = hole.pos;
    }
}