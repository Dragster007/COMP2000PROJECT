import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Galaxy<Body> galaxy = new Galaxy<>();

        SwingUtilities.invokeLater(() -> {
            SimulationWindow window = new SimulationWindow(
                    "Universe creator",
                    galaxy.byDescendingRadius()
            );

            window.start();
        });
    }
}