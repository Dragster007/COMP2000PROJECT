import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Galaxy<Body> galaxy = new Galaxy<>();

        SwingUtilities.invokeLater(() -> {
            SimulationWindow window = new SimulationWindow(
                    "Build Your Solar System",
                    galaxy.byDescendingRadius()
            );

            window.start();
        });
    }
}