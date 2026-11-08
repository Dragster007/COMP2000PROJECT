import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SimulationPanel extends JPanel {

    private static final double DISTANCE_SCALE = 0.14;
    private static final double SIZE_SCALE = 0.3;
    private static final int MIN_RADIUS_PX = 2;

    private static final int TICK_DELAY_MS = 30;
    private static final int STEPS_PER_TICK = 400;
    private static final double DT = 1;

    private final List<Body> bodies;

    // The star type that will be created on the next click.
    private StarType selectedStarType = StarType.G;

    SimulationPanel(List<? extends Body> bodies) {
        this.bodies = new ArrayList<>(bodies);

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000, 600));

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                // Only respond to the left mouse button.
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                // Allow only one star in this system.
                for (Body body : SimulationPanel.this.bodies) {
                    if (body instanceof Star) {
                        return;
                    }
                }

                // Convert the mouse position into simulation coordinates.
                int centreX = getWidth() / 2;
                int centreY = getHeight() / 2;

                double simX =
                        (e.getX() - centreX) / DISTANCE_SCALE;

                double simY =
                        (e.getY() - centreY) / DISTANCE_SCALE;

                try {
                    Star star = new Star(
                            selectedStarType.label,
                            selectedStarType,
                            new Location(simX, simY),
                            new Velocity(0, 0)
                    );

                    SimulationPanel.this.bodies.add(star);

                    repaint();

                } catch (InvalidBodyException ex) {
                    System.err.println(
                            "Could not create star: " + ex.getMessage()
                    );
                }
            }
        });

        Timer timer = new Timer(TICK_DELAY_MS, e -> tick());
        timer.start();
    }

    // Called when the player changes the dropdown selection.
    void setSelectedStarType(StarType type) {
        selectedStarType = type;
    }

    private void tick() {
        for (int step = 0; step < STEPS_PER_TICK; step++) {
            applyGravity();
        }

        handleBlackHoleCollisions();
        repaint();
    }

    private void applyGravity() {
        for (Body body : bodies) {
            double ax = 0;
            double ay = 0;

            for (Body source : bodies) {
                if (source == body) {
                    continue;
                }

                Velocity acceleration =
                        Physics.calculateGravity(body, source);

                ax += acceleration.vx;
                ay += acceleration.vy;
            }

            Physics.updateBody(
                    body,
                    new Velocity(ax, ay),
                    DT
            );
        }
    }

    private void handleBlackHoleCollisions() {
        Iterator<Body> iterator = bodies.iterator();

        while (iterator.hasNext()) {
            Body body = iterator.next();

            if (body instanceof BlackHole) {
                continue;
            }

            for (Body source : bodies) {
                if (!(source instanceof BlackHole)) {
                    continue;
                }

                double dx = source.l.x - body.l.x;
                double dy = source.l.y - body.l.y;

                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < source.radius + body.radius) {
                    try {
                        throw new CollisionException(
                                body.name + " was consumed by " + source.name
                        );

                    } catch (CollisionException ex) {
                        System.out.println(ex.getMessage());
                        iterator.remove();
                    }

                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int centreX = getWidth() / 2;
        int centreY = getHeight() / 2;

        for (Body body : bodies) {
            int x = centreX
                    + (int) Math.round(body.l.x * DISTANCE_SCALE);

            int y = centreY
                    + (int) Math.round(body.l.y * DISTANCE_SCALE);

            int radius = Math.max(
                    MIN_RADIUS_PX,
                    (int) Math.round(body.radius * SIZE_SCALE)
            );

            // Enlarge the star's drawing so it is easy to see.
            if (body instanceof Star) {
                radius = Math.max(
                        6,
                        (int) Math.round(body.radius * 6)
                );
            }

            g2.setColor(body.colour());

            g2.fillOval(
                    x - radius,
                    y - radius,
                    radius * 2,
                    radius * 2
            );

            g2.drawString(
                    body.name,
                    x + radius + 4,
                    y - radius - 4
            );
        }
    }
}