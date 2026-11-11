
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.Random;

public class ExplosionEffect {

    private static final int FRAMES = 18;
    private static final int PARTICLES = 16;

    private final double x;
    private final double y;
    private final Color color;

    private final double[] angles = new double[PARTICLES];
    private final double[] speeds = new double[PARTICLES];

    private int age = 0;

    public ExplosionEffect(double x, double y, Color color) {

        this.x = x;
        this.y = y;
        this.color = color;

        Random random = new Random();

        // Give every particle a different direction.
        for (int i = 0; i < PARTICLES; i++) {

            angles[i] =
                    2 * Math.PI * i / PARTICLES
                    + random.nextDouble() * 0.3;

            speeds[i] =
                    1.1 + random.nextDouble() * 1.8;
        }
    }

    public void update() {
        age++;
    }

    public boolean finished() {
        return age >= FRAMES;
    }

    public void draw(
            Graphics2D g2,
            int centreX,
            int centreY,
            double scale) {

        int screenX =
                centreX + (int) Math.round(x * scale);

        int screenY =
                centreY + (int) Math.round(y * scale);

        float alpha = Math.max(
                0f,
                1f - (float) age / FRAMES
        );

        Composite old = g2.getComposite();

        g2.setComposite(
                AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        alpha
                )
        );

        g2.setColor(color);

        // Spread the particles away from the collision.
        for (int i = 0; i < PARTICLES; i++) {

            double radius = age * speeds[i];

            int px = screenX
                    + (int) (Math.cos(angles[i]) * radius);

            int py = screenY
                    + (int) (Math.sin(angles[i]) * radius);

            g2.fillOval(px - 2, py - 2, 4, 4);
        }

        g2.setComposite(old);
    }
}
