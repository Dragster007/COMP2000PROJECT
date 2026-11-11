
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

        JComboBox<String> objectTypes =
                new JComboBox<>(new String[]{
                        "Star",
                        "Planet",
                        "Black Hole"
                });

        JComboBox<StarType> starTypes =
                new JComboBox<>(StarType.values());

        JComboBox<PlanetType> planetTypes =
                new JComboBox<>(PlanetType.values());

        // Changes which object gets placed.
        objectTypes.addActionListener(e -> {

            String selected =
                    (String) objectTypes.getSelectedItem();

            simulationPanel.setSelectedObject(selected);

            starTypes.setEnabled("Star".equals(selected));
            planetTypes.setEnabled("Planet".equals(selected));
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

        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton resetButton = new JButton("Reset");

        playButton.addActionListener(e -> simulationPanel.play());

        pauseButton.addActionListener(e -> simulationPanel.pause());

        resetButton.addActionListener(e -> simulationPanel.reset());

        controls.add(new JLabel("Object:"));
        controls.add(objectTypes);

        controls.add(new JLabel("Star type:"));
        controls.add(starTypes);

        controls.add(new JLabel("Planet type:"));
        controls.add(planetTypes);

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
