package game_data;

public class GameMap {
    private MapEle[][] _map = new MapEle[GameData.MAP_WIDTH][GameData.MAP_HEIGHT];

    public MapEle elementAt(Point p) {
        return elementAt(p.x, p.y);
    }

    public MapEle elementAt(int x, int y) {
        return _map[x][y];
    }

    public void setElementAt(MapEle ele, int x, int y) {
        _map[x][y] = ele;
    }

    public void setElementAt(MapEle ele, Point p) {
        setElementAt(ele, p.x, p.y);
    }
}
