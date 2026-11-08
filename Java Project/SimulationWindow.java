
import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimulationWindow extends JFrame {

    SimulationWindow(String title, List<? extends Body> bodies) {
        super(title);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        SimulationPanel simulationPanel =
                new SimulationPanel(bodies);

        JPanel controls = new JPanel();

        // 1. Choose whether to place a star or planet.
        JComboBox<String> objectTypes =
                new JComboBox<>(new String[]{"Star", "Planet"});

        // 2. Choose the type of star.
        JComboBox<StarType> starTypes =
                new JComboBox<>(StarType.values());

        // 3. Choose the type of planet.
        JComboBox<PlanetType> planetTypes =
                new JComboBox<>(PlanetType.values());

        // When object selection changes.
        objectTypes.addActionListener(e -> {

            String selected =
                    (String) objectTypes.getSelectedItem();

            boolean planetMode = "Planet".equals(selected);

            simulationPanel.setPlanetMode(planetMode);

            // Only enable the relevant type selector.
            starTypes.setEnabled(!planetMode);
            planetTypes.setEnabled(planetMode);
        });

        // When star type changes.
        starTypes.addActionListener(e -> {

            StarType selected =
                    (StarType) starTypes.getSelectedItem();

            simulationPanel.setSelectedStarType(selected);
        });

        // When planet type changes.
        planetTypes.addActionListener(e -> {

            PlanetType selected =
                    (PlanetType) planetTypes.getSelectedItem();

            simulationPanel.setSelectedPlanetType(selected);
        });

        // Start in star mode.
        starTypes.setEnabled(true);
        planetTypes.setEnabled(false);

        // Add controls to the top of the window.
        controls.add(new JLabel("Object:"));
        controls.add(objectTypes);

        controls.add(new JLabel("Star type:"));
        controls.add(starTypes);

        controls.add(new JLabel("Planet type:"));
        controls.add(planetTypes);

        add(controls, BorderLayout.NORTH);
        add(simulationPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    void start() {
        setVisible(true);
    }
}
