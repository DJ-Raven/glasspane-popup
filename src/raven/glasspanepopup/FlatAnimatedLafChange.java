package raven.glasspanepopup;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.VolatileImage;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.RootPaneContainer;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

/**
 * This code take from flatlaf extras project
 *
 * https://github.com/JFormDesigner/FlatLaf/tree/main/flatlaf-extras
 */
public class FlatAnimatedLafChange {

    public static int duration = 160;

    public static int resolution = 30;

    private static Animator animator;
    private static final Map<JLayeredPane, JComponent> oldUIsnapshots = new WeakHashMap<>();
    private static final Map<JLayeredPane, JComponent> newUIsnapshots = new WeakHashMap<>();
    private static float alpha;
    private static boolean inShowSnapshot;

    public static void showSnapshot() {
        if (animator != null) {
            animator.stop();
        }
        alpha = 1;
        showSnapshot(true, oldUIsnapshots);
    }

    private static void showSnapshot(boolean useAlpha, Map<JLayeredPane, JComponent> map) {
        inShowSnapshot = true;
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (!(window instanceof RootPaneContainer) || !window.isShowing()) {
                continue;
            }
            VolatileImage snapshot = window.createVolatileImage(window.getWidth(), window.getHeight());
            if (snapshot == null) {
                continue;
            }
            JLayeredPane layeredPane = ((RootPaneContainer) window).getLayeredPane();
            layeredPane.paint(snapshot.getGraphics());

            JComponent snapshotLayer = new JComponent() {
                @Override
                public void paint(Graphics g) {
                    if (inShowSnapshot || snapshot.contentsLost()) {
                        return;
                    }
                    if (useAlpha) {
                        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    }
                    g.drawImage(snapshot, 0, 0, null);
                }

                @Override
                public void removeNotify() {
                    super.removeNotify();
                    snapshot.flush();
                }
            };
            if (!useAlpha) {
                snapshotLayer.setOpaque(true);
            }
            snapshotLayer.setSize(layeredPane.getSize());

            layeredPane.add(snapshotLayer, Integer.valueOf(JLayeredPane.DRAG_LAYER + (useAlpha ? 2 : 1)));
            map.put(layeredPane, snapshotLayer);
        }

        inShowSnapshot = false;
    }

    public static void hideSnapshotWithAnimation() {
        if (oldUIsnapshots.isEmpty()) {
            return;
        }
        showSnapshot(false, newUIsnapshots);

        animator = new Animator(duration, new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                if (fraction < 0.1 || fraction > 0.9) {
                    return;
                }
                alpha = 1f - fraction;
                for (Map.Entry<JLayeredPane, JComponent> e : oldUIsnapshots.entrySet()) {
                    if (e.getKey().isShowing()) {
                        e.getValue().repaint();
                    }
                }
                Toolkit.getDefaultToolkit().sync();
            }

            @Override
            public void end() {
                hideSnapshot();
                animator = null;
            }
        });
        animator.setResolution(resolution);
        animator.start();
    }

    private static void hideSnapshot() {
        hideSnapshot(oldUIsnapshots);
        hideSnapshot(newUIsnapshots);
    }

    private static void hideSnapshot(Map<JLayeredPane, JComponent> map) {
        for (Map.Entry<JLayeredPane, JComponent> e : map.entrySet()) {
            e.getKey().remove(e.getValue());
            e.getKey().repaint();
        }
        map.clear();
    }

    public static void stop() {
        if (animator != null) {
            animator.stop();
        } else {
            hideSnapshot();
        }
    }
}
