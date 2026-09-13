import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;

//Draws the celestial bodies
public class SimulationPanel extends JPanel {

    //Pixels per Unit of Distance
    private static final double DISTANCE_SCALE = 0.14;
    //Body Scale
    private static final double SIZE_SCALE = 0.3;
    private static final int MIN_RADIUS_PX = 2;

    private final List<Body> bodies;

    SimulationPanel(List<Body> bodies){
        this.bodies=bodies;
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
}
