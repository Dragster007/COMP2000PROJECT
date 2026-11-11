
import java.util.List;
import java.util.function.Consumer;

public class BlackHoleCollisionStrategy implements CollisionStrategy {

    private final double distanceScale;
    private final Consumer<Body> onDestroyed;

    public BlackHoleCollisionStrategy() {
        this(0.14, body -> {});
    }

    public BlackHoleCollisionStrategy(
            double distanceScale,
            Consumer<Body> onDestroyed) {

        this.distanceScale = distanceScale;
        this.onDestroyed = onDestroyed;
    }

    @Override
    public void resolveCollisions(List<Body> bodies) {

        boolean collisionFound = true;

        while (collisionFound) {

            collisionFound = false;

            for (int i = 0; i < bodies.size(); i++) {

                if (!(bodies.get(i) instanceof BlackHole)) {
                    continue;
                }

                BlackHole blackHole = (BlackHole) bodies.get(i);

                for (int j = 0; j < bodies.size(); j++) {

                    Body other = bodies.get(j);

                    if (other == blackHole) {
                        continue;
                    }

                    double dx = blackHole.l.x - other.l.x;
                    double dy = blackHole.l.y - other.l.y;

                    double distance = Math.hypot(dx, dy);

                    double otherRadius;

                    if (other instanceof Star) {

                        otherRadius =
                                ((Star) other).getDisplayRadius();

                    } else if (other instanceof Planet) {

                        otherRadius =
                                ((Planet) other).type.displayRadius;

                    } else if (other instanceof BlackHole) {

                        otherRadius =
                                ((BlackHole) other).getDisplayRadius();

                    } else {

                        otherRadius = other.radius * distanceScale;
                    }

                    double collisionDistance =
                            (blackHole.getDisplayRadius() + otherRadius)
                            / distanceScale;

                    if (distance > collisionDistance) {
                        continue;
                    }

                    // The heavier black hole survives.
                    if (other instanceof BlackHole) {

                        BlackHole second = (BlackHole) other;

                        if (second.mass > blackHole.mass) {

                            second.absorb(blackHole);

                            onDestroyed.accept(blackHole);

                            bodies.remove(blackHole);

                        } else {

                            blackHole.absorb(second);

                            onDestroyed.accept(second);

                            bodies.remove(second);
                        }

                    } else {

                        // Show a pop when a body is consumed.
                        onDestroyed.accept(other);

                        blackHole.absorb(other);

                        bodies.remove(other);
                    }

                    collisionFound = true;
                    break;
                }

                if (collisionFound) {
                    break;
                }
            }
        }
    }
}
