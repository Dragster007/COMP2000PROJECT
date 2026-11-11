
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class BlackHoleRenderer {

    public static void draw(
            Graphics2D graphics,
            int x,
            int y,
            int radius) {

        Graphics2D g2 = (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Makes the disk tilt slightly upwards.
        g2.rotate(-0.12, x, y);

        double r = radius;

        Color black = Color.BLACK;
        Color orange = new Color(255, 166, 66);
        Color yellow = new Color(255, 217, 131);
        Color white = new Color(255, 250, 225);

        // Draw the glow behind the black hole.
        g2.setColor(orange);
        g2.setStroke(new BasicStroke(
                Math.max(2f, radius * 0.13f)
        ));

        g2.drawArc(
                (int) (x - r * 1.18),
                (int) (y - r * 1.18),
                (int) (r * 2.36),
                (int) (r * 2.36),
                5,
                170
        );

        // Outer part of the accretion disk.
        g2.setColor(black);
        g2.fill(new Ellipse2D.Double(
                x - r * 2.2,
                y - r * 0.48,
                r * 4.4,
                r * 0.96
        ));

        g2.setColor(orange);
        g2.fill(new Ellipse2D.Double(
                x - r * 2.08,
                y - r * 0.37,
                r * 4.16,
                r * 0.74
        ));

        g2.setColor(yellow);
        g2.fill(new Ellipse2D.Double(
                x - r * 1.92,
                y - r * 0.27,
                r * 3.84,
                r * 0.54
        ));

        // Black centre.
        g2.setColor(black);
        g2.fill(new Ellipse2D.Double(
                x - r,
                y - r,
                r * 2,
                r * 2
        ));

        // Light around the bottom of the black hole.
        g2.setColor(white);
        g2.setStroke(new BasicStroke(
                Math.max(2f, radius * 0.14f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        g2.drawArc(
                (int) (x - r * 1.12),
                (int) (y - r * 1.12),
                (int) (r * 2.24),
                (int) (r * 2.24),
                195,
                145
        );

        // Front part of the disk.
        Path2D frontDisk = new Path2D.Double();

        frontDisk.moveTo(x - r * 2.15, y + r * 0.15);

        frontDisk.curveTo(
                x - r * 1.4, y + r * 0.05,
                x + r * 0.8, y - r * 0.05,
                x + r * 2.05, y - r * 0.25
        );

        frontDisk.curveTo(
                x + r * 2.25, y - r * 0.12,
                x + r * 1.4, y + r * 0.20,
                x - r * 1.85, y + r * 0.38
        );

        frontDisk.closePath();

        g2.setColor(black);
        g2.setStroke(new BasicStroke(
                Math.max(3f, radius * 0.23f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        g2.draw(frontDisk);

        g2.setColor(orange);
        g2.fill(frontDisk);

        // Bright streak across the front.
        g2.setColor(white);
        g2.setStroke(new BasicStroke(
                Math.max(1f, radius * 0.09f),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        g2.drawArc(
                (int) (x - r * 1.8),
                (int) (y - r * 0.35),
                (int) (r * 3.6),
                (int) (r * 0.7),
                190,
                155
        );

        g2.dispose();
    }
}
