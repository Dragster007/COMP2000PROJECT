
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
import java.util.Random; // NEW

import javax.swing.JPanel;
import javax.swing.Timer;

public class SimulationPanel extends JPanel {

    private static final double DISTANCE_SCALE = 0.14;
    private static final double SIZE_SCALE = 0.3;
    private static final int MIN_RADIUS_PX = 2;

    private static final int TICK_DELAY_MS = 30;
    private static final int STEPS_PER_TICK = 400;
    private static final double DT = 50000;

    private final List<Body> bodies;

    private StarType selectedStarType = StarType.G;
    private PlanetType selectedPlanetType = PlanetType.ROCKY;

    private boolean planetMode = false;
    private int planetCount = 0;

    private boolean running = false;

    // --------------------------------
    // NEW: BACKGROUND STARS
    // --------------------------------

    private static final int BACKGROUND_STAR_COUNT = 150;

    private final List<BackgroundStar> backgroundStars =
            new ArrayList<>();

    private final Random random = new Random();

    // --------------------------------
    // CONSTRUCTOR
    // --------------------------------

    SimulationPanel(List<? extends Body> bodies) {

        this.bodies = new ArrayList<>(bodies);

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000, 600));

        // NEW: Generate decorative stars once.
        generateBackgroundStars();

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                int centreX = getWidth() / 2;
                int centreY = getHeight() / 2;

                double simX =
                        (e.getX() - centreX) / DISTANCE_SCALE;

                double simY =
                        (e.getY() - centreY) / DISTANCE_SCALE;

                Location position =
                        new Location(simX, simY);

                if (planetMode) {
                    createPlanet(position);
                } else {
                    createStar(position);
                }

                repaint();
            }
        });

        Timer timer = new Timer(TICK_DELAY_MS, e -> tick());
        timer.start();
    }

    // --------------------------------
    // NEW: GENERATE BACKGROUND STARS
    // --------------------------------

    private void generateBackgroundStars() {

        backgroundStars.clear();

        int width = getPreferredSize().width;
        int height = getPreferredSize().height;

        for (int i = 0; i < BACKGROUND_STAR_COUNT; i++) {

            int x = random.nextInt(width);
            int y = random.nextInt(height);

            // Stars are 1 or 2 pixels wide.
            int size = random.nextInt(2) + 1;

            // Different shades of grey and white.
            int brightness = random.nextInt(156) + 100;

            BackgroundStar star = new BackgroundStar(
                    x,
                    y,
                    size,
                    brightness
            );

            backgroundStars.add(star);
        }
    }

    // --------------------------------
    // OBJECT SELECTION
    // --------------------------------

    void setSelectedStarType(StarType type) {
        selectedStarType = type;
    }

    void setSelectedPlanetType(PlanetType type) {
        selectedPlanetType = type;
    }

    void setPlanetMode(boolean planetMode) {
        this.planetMode = planetMode;
    }

    // --------------------------------
    // PLAY, PAUSE AND RESET
    // --------------------------------

    void play() {
        running = true;
    }

    void pause() {
        running = false;
    }

    void reset() {

        running = false;
        bodies.clear();
        planetCount = 0;

        // Background stars are deliberately NOT cleared.
        repaint();
    }

    // --------------------------------
    // STAR CREATION
    // --------------------------------

    private void createStar(Location position) {

        // Allow only one star for now.
        for (Body body : bodies) {
            if (body instanceof Star) {
                return;
            }
        }

        Star star = new Star(
                selectedStarType.label,
                selectedStarType,
                position,
                new Velocity(0, 0)
        );

        bodies.add(star);
    }

    private Star findStar() {

        for (Body body : bodies) {
            if (body instanceof Star) {
                return (Star) body;
            }
        }

        return null;
    }

    // --------------------------------
    // PLANET CREATION
    // --------------------------------

    private Velocity calculateOrbitalVelocity(
            Star star, Location position) {

        double dx = position.x - star.l.x;
        double dy = position.y - star.l.y;

        double distance =
                Math.sqrt(dx * dx + dy * dy);

        double speed = Math.sqrt(
                Physics.G * star.mass / distance
        );

        // Initial velocity perpendicular to gravity.
        double vx = -dy / distance * speed;
        double vy = dx / distance * speed;

        return new Velocity(
                star.v.vx + vx,
                star.v.vy + vy
        );
    }

    private void createPlanet(Location position) {

        Star star = findStar();

        if (star == null) {
            System.out.println("Place a star first.");
            return;
        }

        double dx = position.x - star.l.x;
        double dy = position.y - star.l.y;

        double distance =
                Math.sqrt(dx * dx + dy * dy);

        if (distance <= star.radius
                + selectedPlanetType.radius) {

            System.out.println("Too close to the star.");
            return;
        }

        // Prevent placing two bodies in exactly
        // the same simulation location.
        for (Body body : bodies) {
            if (body.l.x == position.x
                    && body.l.y == position.y) {
                return;
            }
        }

        Velocity orbitalVelocity =
                calculateOrbitalVelocity(star, position);

        planetCount++;

        Planet planet = new Planet(
                "Planet " + planetCount,
                selectedPlanetType,
                position,
                orbitalVelocity
        );

        bodies.add(planet);
    }

    // --------------------------------
    // SIMULATION UPDATE
    // --------------------------------

    private void tick() {

        if (!running) {
            return;
        }

        for (int step = 0; step < STEPS_PER_TICK; step++) {
            applyGravity();
        }

        handleBlackHoleCollisions();

        repaint();
    }

    // --------------------------------
    // GRAVITY
    // --------------------------------

    private void applyGravity() {

        List<Velocity> accelerations =
                new ArrayList<>();

        // Calculate all accelerations first.
        for (Body body : bodies) {

            double ax = 0;
            double ay = 0;

            for (Body source : bodies) {

                if (source == body) {
                    continue;
                }

                double dx = source.l.x - body.l.x;
                double dy = source.l.y - body.l.y;

                double distanceSquared =
                        dx * dx + dy * dy;

                // Avoid division by zero.
                if (distanceSquared < 1e-12) {
                    continue;
                }

                Velocity acceleration =
                        Physics.calculateGravity(body, source);

                ax += acceleration.vx;
                ay += acceleration.vy;
            }

            accelerations.add(new Velocity(ax, ay));
        }

        // Update all positions after calculations.
        for (int i = 0; i < bodies.size(); i++) {

            Physics.updateBody(
                    bodies.get(i),
                    accelerations.get(i),
                    DT
            );
        }
    }

    // --------------------------------
    // BLACK HOLE COLLISIONS
    // --------------------------------

    private void handleBlackHoleCollisions() {

        Iterator<Body> iterator =
                bodies.iterator();

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

                double distance =
                        Math.sqrt(dx * dx + dy * dy);

                if (distance < source.radius
                        + body.radius) {

                    System.out.println(
                            body.name + " was consumed by "
                                    + source.name
                    );

                    iterator.remove();
                    break;
                }
            }
        }
    }

    // --------------------------------
    // DRAW THE SIMULATION
    // --------------------------------

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // --------------------------------
        // NEW: DRAW BACKGROUND STARS FIRST
        // --------------------------------

        for (BackgroundStar star : backgroundStars) {
            star.draw(g2);
        }

        // --------------------------------
        // DRAW STARS AND PLANETS
        // --------------------------------

        int centreX = getWidth() / 2;
        int centreY = getHeight() / 2;

        for (Body body : bodies) {

            int x = centreX
                    + (int) Math.round(
                            body.l.x * DISTANCE_SCALE
                    );

            int y = centreY
                    + (int) Math.round(
                            body.l.y * DISTANCE_SCALE
                    );

            int radius = Math.max(
                    MIN_RADIUS_PX,
                    (int) Math.round(
                            body.radius * SIZE_SCALE
                    )
            );

            if (body instanceof Star) {
                radius = Math.max(
                        6,
                        (int) Math.round(body.radius * 6)
                );
            }

            if (body instanceof Planet) {
                Planet planet = (Planet) body;
                radius = planet.type.displayRadius;
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
