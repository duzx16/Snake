package gamedata;

public class GameConstants {
    // The game setting
    // 地图长宽（不要修改）
    public static final int map_width = 30, map_height = 30;
    // 动画更新的间隔(ms)
    public static final int timer_interval = 20;
    // 游戏前进的最小帧数
    public static final int min_game_interval = 5;
    // 最大、最小和默认速度
    public static final int max_speed = 16, min_speed = 2, default_speed = 12;
    // 添加食物和出洞的等待时间(ms)
    public static final int food_wait = 2000;
    public static final int hole_wait = 2000;
    //初始的蛇、洞、石头、墙、食物的数量
    public static final int init_snakes = 20;
    public static final int hole_num = 4;
    public static final int stone_num = 5;
    public static final int wall_num = 2;
    public static final int food_num = 2;
    // The connect setting
    // 客户端连接的等待时间
    public static final int client_patience = 2000;
    // The animation setting
    public static final int death_flash_delay = 2000;
    public static final int plus_one_interval = 3;
}
