
import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
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

        // Object selection.
        JComboBox<String> objectTypes =
                new JComboBox<>(
                        new String[]{"Star", "Planet"}
                );

        JComboBox<StarType> starTypes =
                new JComboBox<>(StarType.values());

        JComboBox<PlanetType> planetTypes =
                new JComboBox<>(PlanetType.values());

        objectTypes.addActionListener(e -> {

            String selected =
                    (String) objectTypes.getSelectedItem();

            boolean planetMode =
                    "Planet".equals(selected);

            simulationPanel.setPlanetMode(planetMode);

            starTypes.setEnabled(!planetMode);
            planetTypes.setEnabled(planetMode);
        });

        starTypes.addActionListener(e -> {

            StarType selected =
                    (StarType) starTypes.getSelectedItem();

            simulationPanel.setSelectedStarType(selected);
        });

        planetTypes.addActionListener(e -> {

            PlanetType selected =
                    (PlanetType) planetTypes.getSelectedItem();

            simulationPanel.setSelectedPlanetType(selected);
        });

        starTypes.setEnabled(true);
        planetTypes.setEnabled(false);

        // --------------------------------
        // NEW: PLAY, PAUSE, RESET BUTTONS
        // --------------------------------

        JButton playButton =
                new JButton("Play");

        JButton pauseButton =
                new JButton("Pause");

        JButton resetButton =
                new JButton("Reset");

        // Start/resume physics.
        playButton.addActionListener(e -> {
            simulationPanel.play();
        });

        // Stop physics.
        pauseButton.addActionListener(e -> {
            simulationPanel.pause();
        });

        // Clear the simulation.
        resetButton.addActionListener(e -> {
            simulationPanel.reset();
        });

        // --------------------------------
        // ADD CONTROLS TO WINDOW
        // --------------------------------

        controls.add(new JLabel("Object:"));
        controls.add(objectTypes);

        controls.add(new JLabel("Star type:"));
        controls.add(starTypes);

        controls.add(new JLabel("Planet type:"));
        controls.add(planetTypes);

        // NEW buttons.
        controls.add(playButton);
        controls.add(pauseButton);
        controls.add(resetButton);

        add(controls, BorderLayout.NORTH);
        add(simulationPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    void start() {
        setVisible(true);
    }
}
