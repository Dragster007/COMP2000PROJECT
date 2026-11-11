
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulationAnalytics {

    private static final int PARALLEL_THRESHOLD = 64;

    // Immutable snapshot for safe parallel calculations.
    private static final class BodySnapshot {
        final String type;
        final double mass;
        final double vx;
        final double vy;

        BodySnapshot(Body body) {
            type = body.getClass().getSimpleName();
            mass = body.mass;
            vx = body.v.vx;
            vy = body.v.vy;
        }

        double kineticEnergy() {
            return 0.5 * mass * (vx * vx + vy * vy);
        }
    }

    static final class Summary {
        final long stars;
        final long planets;
        final long blackHoles;
        final double totalMass;
        final double kineticEnergy;
        final boolean parallelUsed;

        Summary(long stars, long planets, long blackHoles,
                double totalMass, double kineticEnergy,
                boolean parallelUsed) {
            this.stars = stars;
            this.planets = planets;
            this.blackHoles = blackHoles;
            this.totalMass = totalMass;
            this.kineticEnergy = kineticEnergy;
            this.parallelUsed = parallelUsed;
        }
    }

    static Summary analyze(List<Body> bodies) {
        // Copy mutable simulation data before processing.
        List<BodySnapshot> snapshots = bodies.stream()
                .map(BodySnapshot::new)
                .collect(Collectors.toList());

        // Group celestial bodies by class using a stream.
        Map<String, Long> counts = snapshots.stream()
                .collect(Collectors.groupingBy(
                        snapshot -> snapshot.type,
                        Collectors.counting()
                ));

        // Calculate total mass using a lambda.
        double totalMass = snapshots.stream()
                .mapToDouble(snapshot -> snapshot.mass)
                .sum();

        // Use parallel processing only for larger collections.
        boolean useParallel =
                snapshots.size() >= PARALLEL_THRESHOLD;

        double kineticEnergy = (useParallel
                ? snapshots.parallelStream()
                : snapshots.stream())
                .mapToDouble(BodySnapshot::kineticEnergy)
                .sum();

        return new Summary(
                counts.getOrDefault("Star", 0L),
                counts.getOrDefault("Planet", 0L),
                counts.getOrDefault("BlackHole", 0L),
                totalMass,
                kineticEnergy,
                useParallel
        );
    }
}
