import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;

public class MaskLayer extends JPanel {
    private GameMain _parent;
    final MyDeque plusList = new MyDeque(100);

    MaskLayer(GameMain main) {
        super();
        _parent = main;
    }

    public void paint(Graphics g) {
        // paint the layer as is
        synchronized (plusList) {
            g.setFont(new Font("Yuanti SC", Font.BOLD, 20));
            for (int i = 0; i < plusList.size(); i++) {
                Point p = plusList.elementAt(i);
                g.setColor(Color.WHITE);
                g.drawString("+1", (int) (p.x * _parent.ui.unit_x), (int) (p.y * _parent.ui.unit_y));
            }
        }
        if (_parent.is_pause < 2) {
            g.setColor(new Color(30, 30, 30, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setFont(new Font("Yuanti SC", Font.BOLD, 40));
            g.setColor(Color.WHITE);
            if (_parent.is_pause == 0 ^ _parent.is_server_mode())
                g.drawString("对方已暂停游戏", getWidth() / 2, getHeight() / 2);
            else {
                g.drawString("游戏已暂停", getWidth() / 2, getHeight() / 2);
            }
        } else {
            if (_parent.data.snakes[0].state == Snake.State.IN || _parent.data.snakes[1].state == Snake.State.IN) {
                g.setFont(new Font("Yuanti SC", Font.BOLD, 40));
                g.setColor(Color.WHITE);
                g.drawString("蛇已入洞", getWidth() / 2, getHeight() / 2);
            }
        }
    }
}
