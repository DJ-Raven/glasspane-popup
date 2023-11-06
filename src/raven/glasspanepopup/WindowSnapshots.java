package raven.glasspanepopup;

import java.awt.Graphics;
import java.awt.image.VolatileImage;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

/**
 *
 * @author Raven
 */
public class WindowSnapshots {

    private final JFrame frame;

    private JComponent snapshotLayer;
    private boolean inShowSnapshot;

    public WindowSnapshots(JFrame frame) {
        this.frame = frame;
    }

    public void createSnapshot() {
        if (inShowSnapshot) {
            return;
        }
        inShowSnapshot = true;
        if (frame.isShowing()) {
            VolatileImage snapshot = frame.createVolatileImage(frame.getWidth(), frame.getHeight());
            if (snapshot != null) {
                JLayeredPane layeredPane = frame.getLayeredPane();
                layeredPane.paint(snapshot.getGraphics());
                snapshotLayer = new JComponent() {
                    @Override
                    public void paint(Graphics g) {
                        if(snapshot.contentsLost()){
                            return;
                        }
                        g.drawImage(snapshot, 0, 0, null);
                    }

                    @Override
                    public void removeNotify() {
                        super.removeNotify();
                        snapshot.flush();
                    }
                };
                snapshotLayer.setSize(layeredPane.getSize());
                layeredPane.add(snapshotLayer, Integer.valueOf(JLayeredPane.DRAG_LAYER + 1));
            }
        }
    }

    public void removeSnapshot() {
        frame.getLayeredPane().remove(snapshotLayer);
        inShowSnapshot = false;
    }
}
