
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
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class SimulationPanel extends JPanel {

    private static final double DISTANCE_SCALE = 0.14;
    private static final double SIZE_SCALE = 0.3;
    private static final int MIN_RADIUS_PX = 2;

    private static final int TICK_DELAY_MS = 30;
    private static final int STEPS_PER_TICK = 400;
    private static final double DT = 50000;

    private static final double ATTRACTION_RADIUS_MULTIPLIER = 3.0;
    private static final double EXTRA_GRAVITY_STRENGTH = 0.5;
    private static final double ORBITAL_DRAG = 1e-10;

    private static final int BACKGROUND_STAR_COUNT = 150;

    private final List<Body> bodies;

    private final List<ExplosionEffect> explosions =
            new ArrayList<>();

    private final CollisionStrategy collisionStrategy =
            new BlackHoleCollisionStrategy(
                    DISTANCE_SCALE,
                    body -> addExplosion(body)
            );

    private final CollisionStrategy planetCollisions =
            new PlanetCollisionStrategy(
                    DISTANCE_SCALE,
                    planet -> addExplosion(planet)
            );

    private final List<BackgroundStar> backgroundStars =
            new ArrayList<>();

    private final Random random = new Random();

    private StarType selectedStarType = StarType.G;
    private PlanetType selectedPlanetType = PlanetType.ROCKY;

    private String selectedObject = "Star";

    private boolean running = false;

    private int starCount = 0;
    private int planetCount = 0;
    private int blackHoleCount = 0;

    SimulationPanel(List<? extends Body> bodies) {

        this.bodies = new ArrayList<>(bodies);

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000, 600));

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

                Location position = new Location(simX, simY);

                switch (selectedObject) {

                    case "Star":
                        createStar(position);
                        break;

                    case "Planet":
                        createPlanet(position);
                        break;

                    case "Black Hole":
                        createBlackHole(position);
                        break;
                }

                repaint();
            }
        });

        Timer timer = new Timer(TICK_DELAY_MS, e -> tick());
        timer.start();
    }

    // Creates the background stars.
    private void generateBackgroundStars() {

        backgroundStars.clear();

        for (int i = 0; i < BACKGROUND_STAR_COUNT; i++) {

            double x = random.nextDouble();
            double y = random.nextDouble();

            int size = random.nextInt(2) + 1;
            int brightness = random.nextInt(156) + 100;

            backgroundStars.add(
                    new BackgroundStar(x, y, size, brightness)
            );
        }
    }

    void setSelectedStarType(StarType type) {
        selectedStarType = type;
    }

    void setSelectedPlanetType(PlanetType type) {
        selectedPlanetType = type;
    }

    void setSelectedObject(String selectedObject) {
        this.selectedObject = selectedObject;
    }

    void play() {
        running = true;
    }

    void pause() {
        running = false;
    }

    void reset() {

        running = false;

        bodies.clear();
        explosions.clear();

        starCount = 0;
        planetCount = 0;
        blackHoleCount = 0;

        repaint();
    }

    // Gets the size of a body on screen.
    private int getBodyRadius(Body body) {

        if (body instanceof Star) {
            return ((Star) body).getDisplayRadius();
        }

        if (body instanceof Planet) {
            return ((Planet) body).type.displayRadius;
        }

        if (body instanceof BlackHole) {
            return ((BlackHole) body).getDisplayRadius();
        }

        return Math.max(
                MIN_RADIUS_PX,
                (int) Math.round(body.radius * SIZE_SCALE)
        );
    }

    // Checks if there is room for a new body.
    private boolean isPositionOccupied(
            Location position,
            int newRadius) {

        for (Body body : bodies) {

            double dx = position.x - body.l.x;
            double dy = position.y - body.l.y;

            double distance = Math.hypot(dx, dy);

            double minimumDistance =
                    (newRadius + getBodyRadius(body))
                    / DISTANCE_SCALE;

            if (distance < minimumDistance) {
                return true;
            }
        }

        return false;
    }

    // Creates a star.
    private void createStar(Location position) {

        Star star = new Star(
                selectedStarType.label + " Star " + (starCount + 1),
                selectedStarType,
                position,
                new Velocity(0, 0)
        );

        if (isPositionOccupied(
                position,
                star.getDisplayRadius())) {

            System.out.println("Too close to another body.");
            return;
        }

        starCount++;

        bodies.add(star);

        System.out.println(star.name + " created.");
    }

    // Finds the nearest star.
    private Star findNearestStar(Location position) {

        Star nearestStar = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Body body : bodies) {

            if (!(body instanceof Star)) {
                continue;
            }

            Star star = (Star) body;

            double distance = Math.hypot(
                    position.x - star.l.x,
                    position.y - star.l.y
            );

            if (distance < nearestDistance) {

                nearestDistance = distance;
                nearestStar = star;
            }
        }

        return nearestStar;
    }

    // Gives planets their starting orbital velocity.
    private Velocity calculateOrbitalVelocity(
            Star star,
            Location position) {

        double dx = position.x - star.l.x;
        double dy = position.y - star.l.y;

        double distance = Math.hypot(dx, dy);

        if (distance < 1e-9) {
            return new Velocity(0, 0);
        }

        double speed = Math.sqrt(
                Physics.G * star.mass / distance
        );

        double vx = -dy / distance * speed;
        double vy = dx / distance * speed;

        return new Velocity(
                star.v.vx + vx,
                star.v.vy + vy
        );
    }

    // Creates a planet.
    private void createPlanet(Location position) {

        int radius = selectedPlanetType.displayRadius;

        if (isPositionOccupied(position, radius)) {

            System.out.println("Too close to another body.");
            return;
        }

        Star nearestStar = findNearestStar(position);

        Velocity startingVelocity;

        if (nearestStar != null) {

            startingVelocity = calculateOrbitalVelocity(
                    nearestStar,
                    position
            );

        } else {

            // Without a star, the planet starts stationary.
            startingVelocity = new Velocity(0, 0);
        }

        planetCount++;

        Planet planet = new Planet(
                "Planet " + planetCount,
                selectedPlanetType,
                position,
                startingVelocity
        );

        bodies.add(planet);
    }

    // Creates a black hole.
    private void createBlackHole(Location position) {

        BlackHole blackHole = new BlackHole(
                "Black Hole " + (blackHoleCount + 1),
                5000000,
                position,
                5,
                new Velocity(0, 0)
        );

        if (isPositionOccupied(
                position,
                blackHole.getDisplayRadius())) {

            System.out.println("Too close to another body.");
            return;
        }

        blackHoleCount++;

        bodies.add(blackHole);
    }

    // Creates a pop when a body is destroyed.
    private void addExplosion(Body body) {

        Color color = body.colour();

        if (body instanceof BlackHole) {
            color = new Color(255, 160, 60);
        }

        explosions.add(
                new ExplosionEffect(
                        body.l.x,
                        body.l.y,
                        color
                )
        );
    }

    // Runs the simulation.
    private void tick() {

        if (running) {

            for (int step = 0; step < STEPS_PER_TICK; step++) {

                applyGravity();

                // Two stars can form a black hole.
                handleStarStarCollisions();

                // Black holes consume other bodies.
                collisionStrategy.resolveCollisions(bodies);

                // Stars consume planets.
                handleStarCollisions();

                // Planets collide with each other.
                planetCollisions.resolveCollisions(bodies);
            }
        }

        // Updates the pop animations.
        for (ExplosionEffect effect : explosions) {
            effect.update();
        }

        explosions.removeIf(ExplosionEffect::finished);

        if (running || !explosions.isEmpty()) {
            repaint();
        }
    }

    // Two colliding stars turn into a black hole.
    private void handleStarStarCollisions() {

        boolean collisionFound = true;

        while (collisionFound) {

            collisionFound = false;

            for (int i = 0; i < bodies.size(); i++) {

                if (!(bodies.get(i) instanceof Star)) {
                    continue;
                }

                Star star1 = (Star) bodies.get(i);

                for (int j = i + 1; j < bodies.size(); j++) {

                    if (!(bodies.get(j) instanceof Star)) {
                        continue;
                    }

                    Star star2 = (Star) bodies.get(j);

                    double dx = star2.l.x - star1.l.x;
                    double dy = star2.l.y - star1.l.y;

                    double distance = Math.hypot(dx, dy);

                    double collisionDistance =
                            (star1.getDisplayRadius()
                            + star2.getDisplayRadius())
                            / DISTANCE_SCALE;

                    if (distance > collisionDistance) {
                        continue;
                    }

                    // Add the masses of both stars.
                    double totalMass =
                            star1.mass + star2.mass;

                    // Find the new black hole's position.
                    double newX =
                            (star1.l.x * star1.mass
                            + star2.l.x * star2.mass)
                            / totalMass;

                    double newY =
                            (star1.l.y * star1.mass
                            + star2.l.y * star2.mass)
                            / totalMass;

                    // Keep the combined momentum.
                    double newVx =
                            (star1.v.vx * star1.mass
                            + star2.v.vx * star2.mass)
                            / totalMass;

                    double newVy =
                            (star1.v.vy * star1.mass
                            + star2.v.vy * star2.mass)
                            / totalMass;

                    Location position = new Location(
                            newX,
                            newY
                    );

                    Velocity velocity = new Velocity(
                            newVx,
                            newVy
                    );

                    blackHoleCount++;

                    BlackHole blackHole = new BlackHole(
                            "Black Hole " + blackHoleCount,
                            totalMass,
                            position,
                            5,
                            velocity
                    );

                    // Both stars disappear with a pop.
                    addExplosion(star1);
                    addExplosion(star2);

                    System.out.println(
                            star1.name + " collided with "
                            + star2.name
                    );

                    System.out.println(
                            "A black hole has formed!"
                    );

                    // Remove both stars.
                    bodies.remove(j);
                    bodies.remove(i);

                    // Add the new black hole.
                    bodies.add(blackHole);

                    collisionFound = true;
                    break;
                }

                if (collisionFound) {
                    break;
                }
            }
        }
    }

    // Calculates the attraction zone around a star.
    private double getAttractionRadius(Star star) {

        return star.getDisplayRadius()
                * ATTRACTION_RADIUS_MULTIPLIER;
    }

    // Adds extra attraction near stars.
    private Velocity calculateAttraction(
            Planet planet,
            Star star) {

        double dx = star.l.x - planet.l.x;
        double dy = star.l.y - planet.l.y;

        double distanceSquared = dx * dx + dy * dy;

        if (distanceSquared < 1e-12) {
            return new Velocity(0, 0);
        }

        double distancePixels =
                Math.sqrt(distanceSquared) * DISTANCE_SCALE;

        double zoneRadius = getAttractionRadius(star);

        if (distancePixels >= zoneRadius) {
            return new Velocity(0, 0);
        }

        double proximity =
                1.0 - distancePixels / zoneRadius;

        double growthFactor = Math.min(
                4.0,
                star.mass / star.type.mass
        );

        Velocity gravity =
                Physics.calculateGravity(planet, star);

        double extraStrength =
                EXTRA_GRAVITY_STRENGTH
                * proximity
                * growthFactor;

        double dragStrength =
                ORBITAL_DRAG
                * proximity
                * growthFactor;

        double relativeVx = planet.v.vx - star.v.vx;
        double relativeVy = planet.v.vy - star.v.vy;

        return new Velocity(
                gravity.vx * extraStrength
                        - relativeVx * dragStrength,

                gravity.vy * extraStrength
                        - relativeVy * dragStrength
        );
    }

    // Calculates gravity between all bodies.
    private void applyGravity() {

        List<Velocity> accelerations =
                new ArrayList<>();

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

                if (distanceSquared < 1e-12) {
                    continue;
                }

                Velocity acceleration =
                        Physics.calculateGravity(body, source);

                ax += acceleration.vx;
                ay += acceleration.vy;

                if (body instanceof Planet
                        && source instanceof Star) {

                    Velocity extra = calculateAttraction(
                            (Planet) body,
                            (Star) source
                    );

                    ax += extra.vx;
                    ay += extra.vy;
                }
            }

            accelerations.add(
                    new Velocity(ax, ay)
            );
        }

        // Move all the bodies.
        for (int i = 0; i < bodies.size(); i++) {

            Physics.updateBody(
                    bodies.get(i),
                    accelerations.get(i),
                    DT
            );
        }
    }

    // Stars consume planets.
    private void handleStarCollisions() {

        Iterator<Body> iterator = bodies.iterator();

        while (iterator.hasNext()) {

            Body body = iterator.next();

            if (!(body instanceof Planet)) {
                continue;
            }

            Planet planet = (Planet) body;

            for (Body other : bodies) {

                if (!(other instanceof Star)) {
                    continue;
                }

                Star star = (Star) other;

                double distance = Math.hypot(
                        star.l.x - planet.l.x,
                        star.l.y - planet.l.y
                );

                double touchDistance =
                        (star.getDisplayRadius()
                        + planet.type.displayRadius)
                        / DISTANCE_SCALE;

                if (distance <= touchDistance) {

                    addExplosion(planet);

                    star.absorbPlanet(planet);

                    iterator.remove();
                    break;
                }
            }
        }
    }

    // Draws everything on the screen.
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Background stars.
        for (BackgroundStar star : backgroundStars) {
            star.draw(g2, getWidth(), getHeight());
        }

        int centreX = getWidth() / 2;
        int centreY = getHeight() / 2;

        // Draw the celestial bodies.
        for (Body body : bodies) {

            int x = centreX
                    + (int) Math.round(
                            body.l.x * DISTANCE_SCALE
                    );

            int y = centreY
                    + (int) Math.round(
                            body.l.y * DISTANCE_SCALE
                    );

            int radius = getBodyRadius(body);

            if (body instanceof BlackHole) {

                BlackHoleRenderer.draw(g2, x, y, radius);

            } else {

                g2.setColor(body.colour());

                g2.fillOval(
                        x - radius,
                        y - radius,
                        radius * 2,
                        radius * 2
                );
            }

            g2.setColor(Color.WHITE);

            g2.drawString(
                    body.name,
                    x + radius + 4,
                    y - radius - 4
            );
        }

        // Draw the pop animations.
        for (ExplosionEffect effect : explosions) {

            effect.draw(
                    g2,
                    centreX,
                    centreY,
                    DISTANCE_SCALE
            );
        }

        // Display simulation statistics.
        SimulationAnalytics.Summary stats =
                SimulationAnalytics.analyze(bodies);

        g2.setColor(Color.WHITE);

        g2.drawString(
                "Active Celestial Bodies: " + bodies.size(),
                20, 30
        );

        g2.drawString(
                "Stars: " + stats.stars
                        + " | Planets: " + stats.planets
                        + " | Black Holes: " + stats.blackHoles,
                20, 50
        );

        g2.drawString(
                String.format(
                        "Total Mass: %.2e | Kinetic Energy: %.2e",
                        stats.totalMass,
                        stats.kineticEnergy
                ),
                20, 70
        );
    }
}
