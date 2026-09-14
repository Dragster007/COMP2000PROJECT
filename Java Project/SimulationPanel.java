import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.*;

//Draws the celestial bodies
public class SimulationPanel extends JPanel {

    //Pixels per Unit of Distance
    private static final double DISTANCE_SCALE = 0.14;
    //Body Scale
    private static final double SIZE_SCALE = 0.3;
    private static final int MIN_RADIUS_PX = 2;
    private int substeps = 100;   // physics updates per animation frame
    private double dt = 3000;

    private final List<Body> bodies;

    SimulationPanel(List<? extends Body> bodies){
        this.bodies = new ArrayList<>(bodies);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(1000,600));
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        //The Sun sits at (0,0) in the model, so the middle of the panel is the origin.
        int centreX=getWidth()/2;
        int centreY=getHeight()/2;

        for (Body body : bodies){
            int x=centreX + (int) Math.round(body.l.x * DISTANCE_SCALE);
            int y=centreY + (int) Math.round(body.l.y * DISTANCE_SCALE);
            int r=Math.max(MIN_RADIUS_PX,(int) Math.round(body.radius * SIZE_SCALE));

            g2.setColor(body.colour());
            g2.fillOval(x-r,y-r,r*2,r*2);
            g2.drawString(body.name,x+r+4,y-r-4);
        }
    }
    {
        addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        // use the same scale paintComponent uses, so the meteor
        // lands where you actually clicked
        double simX = (e.getX() - cx) / DISTANCE_SCALE;
        double simY = (e.getY() - cy) / DISTANCE_SCALE;

        try {
            Meteor meteor = new Meteor(
                    "Meteor",
                    0.5,
                    new Location(simX, simY),
                    2,
                    new Velocity(0, 0)
            );
            bodies.add(meteor);
            repaint();   // <-- actually redraw so the meteor appears
        } catch (InvalidBodyException ex) {
            System.err.println("Could not create meteor: " + ex.getMessage());
        }
    }
});
    }

    private void step() {
        for (int s = 0; s < substeps; s++) {
            List<Velocity> accelerations = new ArrayList<>();
            for (Body b : bodies) {
                double ax = 0, ay = 0;
                for (Body other : bodies) {
                    if (other == b) continue;
                    Velocity a = Physics.calculateGravity(b, other);
                    ax += a.vx;
                    ay += a.vy;
                }
                accelerations.add(new Velocity(ax, ay));
            }
            for (int i = 0; i < bodies.size(); i++) {
                Physics.updateBody(bodies.get(i), accelerations.get(i), dt);
            }
        }
        checkMeteorCollisions();
    }

    // Checks every meteor against every planet; on overlap, the planet
    // changes color (via collide()) and the meteor is removed.
    private void checkMeteorCollisions() {
        List<Body> toRemove = new ArrayList<>();

        for (Body b : bodies) {
            if (!(b instanceof Meteor)) continue;
            Meteor meteor = (Meteor) b;

            for (Body other : bodies) {
                if (!(other instanceof Planet)) continue;
                Planet planet = (Planet) other;

                double dx = meteor.l.x - planet.l.x;
                double dy = meteor.l.y - planet.l.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // radii are in pixel units (used directly for drawing),
                // so convert the collision threshold back into simulation units
                double collisionDistance = (meteor.radius + planet.radius) / DISTANCE_SCALE;

                if (distance < collisionDistance) {
                    planet.collide();
                    toRemove.add(meteor);
                }
            }
        }

        bodies.removeAll(toRemove);
    }
}
