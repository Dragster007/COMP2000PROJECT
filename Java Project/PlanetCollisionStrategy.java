
import java.util.List;
import java.util.function.Consumer;

public class PlanetCollisionStrategy implements CollisionStrategy {

    private final double distanceScale;
    private final Consumer<Planet> onDestroyed;

    public PlanetCollisionStrategy(
            double distanceScale,
            Consumer<Planet> onDestroyed) {

        this.distanceScale = distanceScale;
        this.onDestroyed = onDestroyed;
    }

    @Override
    public void resolveCollisions(List<Body> bodies) {

        for (int i = 0; i < bodies.size(); i++) {

            if (!(bodies.get(i) instanceof Planet)) {
                continue;
            }

            for (int j = i + 1; j < bodies.size(); j++) {

                if (!(bodies.get(j) instanceof Planet)) {
                    continue;
                }

                Planet a = (Planet) bodies.get(i);
                Planet b = (Planet) bodies.get(j);

                double dx = b.l.x - a.l.x;
                double dy = b.l.y - a.l.y;

                double distance = Math.hypot(dx, dy);

                double minDistance =
                        (a.type.displayRadius + b.type.displayRadius)
                        / distanceScale;

                if (distance > minDistance) {
                    continue;
                }

                // Find the direction of the collision.
                double nx = distance > 1e-9
                        ? dx / distance : 1.0;

                double ny = distance > 1e-9
                        ? dy / distance : 0.0;

                double relativeSpeed =
                        (b.v.vx - a.v.vx) * nx
                        + (b.v.vy - a.v.vy) * ny;

                // Deflect the planets when they hit.
                if (relativeSpeed < 0) {

                    double impulse =
                            -2.0 * relativeSpeed
                            / (1.0 / a.mass + 1.0 / b.mass);

                    a.v.vx -= impulse * nx / a.mass;
                    a.v.vy -= impulse * ny / a.mass;

                    b.v.vx += impulse * nx / b.mass;
                    b.v.vy += impulse * ny / b.mass;
                }

                // Separate the planets so they don't get stuck.
                double overlap =
                        Math.max(0.0, minDistance - distance)
                        + 0.01;

                double totalMass = a.mass + b.mass;

                a.l.x -= nx * overlap * b.mass / totalMass;
                a.l.y -= ny * overlap * b.mass / totalMass;

                b.l.x += nx * overlap * a.mass / totalMass;
                b.l.y += ny * overlap * a.mass / totalMass;

                // Equal-mass planets both survive.
                if (Math.abs(a.mass - b.mass) < 1e-9) {
                    continue;
                }

                // The smaller planet gets destroyed.
                Planet destroyed =
                        a.mass < b.mass ? a : b;

                onDestroyed.accept(destroyed);
                bodies.remove(destroyed);

                System.out.println(
                        destroyed.name
                        + " was destroyed in a planet collision."
                );

                return;
            }
        }
    }
}
