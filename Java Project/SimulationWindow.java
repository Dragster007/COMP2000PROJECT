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

        SimulationPanel simulationPanel = new SimulationPanel(bodies);

        JPanel controls = new JPanel();

        JComboBox<StarType> starTypes =
                new JComboBox<>(StarType.values());

        starTypes.addActionListener(e -> {
            StarType selected = (StarType) starTypes.getSelectedItem();
            simulationPanel.setSelectedStarType(selected);
        });

        controls.add(new JLabel("Star type:"));
        controls.add(starTypes);

        add(controls, BorderLayout.NORTH);
        add(simulationPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    void start() {
        setVisible(true);
    }
}