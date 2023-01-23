package raven.glasspanepopup;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

/**
 *
 * @author Raven
 */
public class Popup extends JComponent {

    private final DecimalFormat df = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));
    private final GlassPanePopup parent;
    private final Component component;
    private final Option option;
    private Animator animator;
    private MigLayout layout;
    private float animate;
    private boolean show;
    private boolean mouseHover;

    public Popup(GlassPanePopup parent, Component component, Option option) {
        this.parent = parent;
        this.component = component;
        this.option = option;
        init();
        initAnimator();
    }

    private void init() {
        layout = new MigLayout();
        initOption();
        setLayout(layout);
        add(component, option.getLayout(parent.getLayerPane(), 0));
    }

    private void initOption() {
        LayoutCallback callBack = option.getLayoutCallBack(parent.getLayerPane());
        if (callBack != null) {
            layout.addLayoutCallback(callBack);
        }
        if (option.closeWhenClickOutside()) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseHover = true;
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mouseHover = false;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (mouseHover && SwingUtilities.isLeftMouseButton(e)) {
                        setShowPopup(false);
                    }
                }
            });
        } else if (option.blockBackground()) {
            addMouseListener(new MouseAdapter() {
            });
        }
    }

    private void initAnimator() {
        animator = new Animator(option.duration(), new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                if (show) {
                    animate = fraction;
                } else {
                    animate = 1f - fraction;
                }
                float f = Float.parseFloat(df.format(animate));
                option.setAnimate(f);
                String lc = option.getLayout(parent.getLayerPane(), f);
                if (lc != null) {
                    layout.setComponentConstraints(component, lc);
                }
                repaint();
                revalidate();
            }

            @Override
            public void end() {
                if (!show) {
                    parent.removePopup(Popup.this);
                }
            }
        });
        animator.setAcceleration(.5f);
        animator.setDeceleration(.5f);
        animator.setResolution(5);
    }

    public void setShowPopup(boolean show) {
        if (this.show != show) {
            if (animator.isRunning()) {
                float f = animator.getTimingFraction();
                animator.stop();
                animator.setStartFraction(1f - f);
            } else {
                animator.setStartFraction(0);
            }
            this.show = show;
            animator.start();
            if (!show) {
                if (getMouseListeners().length != 0) {
                    removeMouseListener(getMouseListeners()[0]);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.SrcOver.derive(animate * option.opacity()));
        g2.setColor(option.background());
        g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        g2.setComposite(AlphaComposite.SrcOver.derive(animate));
        super.paintComponent(g);
    }
}
