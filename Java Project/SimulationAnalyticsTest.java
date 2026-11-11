
import java.util.ArrayList;
import java.util.List;

public class SimulationAnalyticsTest {

    public static void main(String[] args) {
        List<Body> bodies = new ArrayList<>();

        for (int i = 0; i < 80; i++) {
            bodies.add(new Body(
                    "Test " + i,
                    2.0,
                    new Location(i, 0),
                    1.0,
                    new Velocity(3.0, 4.0)
            ));
        }

        SimulationAnalytics.Summary parallel =
                SimulationAnalytics.analyze(bodies);

        if (!parallel.parallelUsed) {
            throw new AssertionError(
                    "Expected parallel processing");
        }

        if (Math.abs(parallel.totalMass - 160.0) > 1e-9) {
            throw new AssertionError("Incorrect total mass");
        }

        if (Math.abs(parallel.kineticEnergy - 2000.0) > 1e-9) {
            throw new AssertionError("Incorrect kinetic energy");
        }

        SimulationAnalytics.Summary sequential =
                SimulationAnalytics.analyze(bodies.subList(0, 2));

        if (sequential.parallelUsed) {
            throw new AssertionError(
                    "Small collections should use sequential processing");
        }

        if (Math.abs(sequential.kineticEnergy - 50.0) > 1e-9) {
            throw new AssertionError(
                    "Incorrect sequential kinetic energy");
        }

        System.out.println(
                "PASS: Sequential and parallel analytics work correctly."
        );
    }
}
