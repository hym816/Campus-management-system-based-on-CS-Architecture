package Server.Public;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

public class TransparentButton extends JButton {

    private Color startColor;
    private Color hoverColor;
    private Color clickColor = new Color(169, 169, 169, 150);
    private Point clickPoint;
    private int rippleRadius;
    private boolean rippleActive = false;
    private boolean isMouseOver = false;
    private int cornerRadius;
    private Timer rippleTimer;
    private Dimension buttonSize;

    // Main constructor
    public TransparentButton(String text, Color color, int cornerRadius, int width, int height) {
        super(text);
        this.cornerRadius = cornerRadius;
        this.buttonSize = new Dimension(width, height);

        // Restrict background color to blue or white
        if (color.equals(Color.BLUE)) {
            this.startColor = new Color(70, 130, 180);
            this.hoverColor = new Color(90, 150, 200); // Hover color for blue
        } else if (color.equals(Color.WHITE)) {
            this.startColor = new Color(70, 130, 180, 0);
            this.hoverColor = new Color(211, 211, 211, 128); // Hover color for white
        } else {
            throw new IllegalArgumentException("Color must be either Color.BLUE or Color.WHITE.");
        }

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false); // Set button background to transparent
        setBackground(new Color(0, 0, 0, 0)); // Ensure full transparency

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isMouseOver = true;
                startHoverAnimation(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isMouseOver = false;
                startHoverAnimation(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                clickPoint = e.getPoint();
                rippleRadius = 0;
                rippleActive = true;
                startClickAnimation();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (rippleRadius >= Math.max(getWidth(), getHeight()) * 2) {
                    startReleaseAnimation();
                }
            }
        });

        // Set text color based on background color
        setForeground(color.equals(Color.BLUE) ? Color.WHITE : Color.BLACK);
    }

    // Overloaded constructor with default values: white color, corner radius 30, and 150x50 size
    public TransparentButton(String text) {
        this(text, Color.WHITE, 4, 150, 50);
    }

    @Override
    public Dimension getPreferredSize() {
        return buttonSize;
    }

    private void startHoverAnimation(boolean enter) {
        if (rippleTimer != null && rippleTimer.isRunning()) {
            rippleTimer.stop();
        }

        Color targetColor = enter ? hoverColor : startColor;
        rippleTimer = new Timer(10, new ActionListener() {
            private int step = 0;
            private final int totalSteps = 50;

            @Override
            public void actionPerformed(ActionEvent e) {
                float ratio = (float) step / (float) totalSteps;
                Color currentColor = new Color(
                        (int) ((1 - ratio) * getBackground().getRed() + ratio * targetColor.getRed()),
                        (int) ((1 - ratio) * getBackground().getGreen() + ratio * targetColor.getGreen()),
                        (int) ((1 - ratio) * getBackground().getBlue() + ratio * targetColor.getBlue()),
                        (int) ((1 - ratio) * getBackground().getAlpha() + ratio * targetColor.getAlpha())
                );
                setBackground(currentColor);
                step++;
                if (step > totalSteps) {
                    rippleTimer.stop();
                }
            }
        });
        rippleTimer.start();
    }

    private void startClickAnimation() {
        if (rippleTimer != null && rippleTimer.isRunning()) {
            rippleTimer.stop();
        }

        rippleTimer = new Timer(10, new ActionListener() {
            private final int maxRadius = Math.max(getWidth(), getHeight()) * 2;

            @Override
            public void actionPerformed(ActionEvent e) {
                rippleRadius += 10;
                if (rippleRadius >= maxRadius) {
                    rippleRadius = maxRadius;
                    rippleActive = false;
                    setBackground(clickColor);
                    rippleTimer.stop();
                    startReleaseAnimation(); // Automatically recover after diffusion ends
                }
                repaint();
            }
        });
        rippleTimer.start();
    }

    private void startReleaseAnimation() {
        if (rippleTimer != null && rippleTimer.isRunning()) {
            rippleTimer.stop();
        }

        rippleTimer = new Timer(10, new ActionListener() {
            private int step = 0;
            private final int totalSteps = 50;

            @Override
            public void actionPerformed(ActionEvent e) {
                float ratio = (float) step / (float) totalSteps;
                Color targetColor = isMouseOver ? hoverColor : startColor;
                Color currentColor = new Color(
                        (int) ((1 - ratio) * getBackground().getRed() + ratio * targetColor.getRed()),
                        (int) ((1 - ratio) * getBackground().getGreen() + ratio * targetColor.getGreen()),
                        (int) ((1 - ratio) * getBackground().getBlue() + ratio * targetColor.getBlue()),
                        (int) ((1 - ratio) * getBackground().getAlpha() + ratio * targetColor.getAlpha())
                );
                setBackground(currentColor);
                step++;
                if (step > totalSteps) {
                    rippleTimer.stop();
                    setBackground(targetColor); // Set to hover or initial color at the end
                }
            }
        });
        rippleTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw rounded rectangle background
        g2.setColor(getBackground());
        RoundRectangle2D roundRect = new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, cornerRadius, cornerRadius);
        g2.fill(roundRect);



        // Draw ripple effect if active
        if (rippleActive && clickPoint != null) {
            Area clipArea = new Area(roundRect);
            Area rippleArea = new Area(new Ellipse2D.Float(
                    clickPoint.x - rippleRadius,
                    clickPoint.y - rippleRadius,
                    rippleRadius * 2,
                    rippleRadius * 2));
            clipArea.intersect(rippleArea);
            g2.setClip(clipArea);
            g2.setColor(clickColor);
            g2.fill(roundRect);
        }

        g2.dispose();

        // Finally, draw text to ensure it is on top
        super.paintComponent(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Transparent Button Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);

                JPanel panel = new JPanel();
                frame.add(panel, BorderLayout.CENTER);

                // Test buttons with and without optional parameters
                TransparentButton defaultButton = new TransparentButton("Default Button");
                TransparentButton blueButton = new TransparentButton("Blue Button", Color.BLUE, 30, 150, 50);

                panel.add(defaultButton);
                panel.add(blueButton);

                frame.setVisible(true);
            }
        });
    }
}
