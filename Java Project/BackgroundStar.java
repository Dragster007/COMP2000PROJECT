
import java.awt.Color;
import java.awt.Graphics2D;

public class BackgroundStar {

    // Position stored as a percentage (0.0 to 1.0).
    private final double x;
    private final double y;

    private final int size;
    private final int brightness;

    public BackgroundStar(
            double x,
            double y,
            int size,
            int brightness) {

        this.x = x;
        this.y = y;
        this.size = size;
        this.brightness = brightness;
    }

    public void draw(
            Graphics2D g2,
            int width,
            int height) {

        // Convert relative positions into screen pixels.
        int screenX = (int) (x * width);
        int screenY = (int) (y * height);

        g2.setColor(new Color(
                brightness,
                brightness,
                brightness
        ));

        g2.fillOval(
                screenX,
                screenY,
                size,
                size
        );
    }
}
