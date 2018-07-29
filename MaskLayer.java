import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.util.ArrayList;

public class MaskLayer extends LayerUI<JPanel> {
    private GameMain _parent;
    final MyDeque plusList = new MyDeque(100);

    MaskLayer(GameMain main) {
        super();
        _parent = main;
    }

    public void paint(Graphics g, JComponent c) {
        // paint the layer as is
        super.paint(g, c);
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
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
            g.setFont(new Font("Yuanti SC", Font.BOLD, 40));
            g.setColor(Color.WHITE);
            if (_parent.is_pause == 0 ^ _parent.is_server_mode())
                g.drawString("对方已暂停游戏", c.getWidth() / 2, c.getHeight() / 2);
            else {
                g.drawString("游戏已暂停", c.getWidth() / 2, c.getHeight() / 2);
            }
        } else {
            if (_parent.data.snakes[0].state == Snake.State.IN || _parent.data.snakes[1].state == Snake.State.IN) {
                g.setFont(new Font("Yuanti SC", Font.BOLD, 40));
                g.setColor(Color.WHITE);
                g.drawString("蛇已入洞", c.getWidth() / 2, c.getHeight() / 2);
            }
        }
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        // enable mouse motion events for the layer's subcomponents
        ((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        // reset the layer event mask
        ((JLayer) c).setLayerEventMask(0);
    }

    // overridden method which catches MouseMotion events
    public void eventDispatched(AWTEvent e, JLayer<? extends JPanel> l) {
        //System.out.println("AWTEvent detected: " + e);
    }
}
