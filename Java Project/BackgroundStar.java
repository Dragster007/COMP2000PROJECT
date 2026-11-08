
import java.awt.Color;
import java.awt.Graphics2D;

public class BackgroundStar {

    private final int x;
    private final int y;
    private final int size;
    private final int brightness;

    public BackgroundStar(int x, int y, int size, int brightness) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.brightness = brightness;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(
                brightness,
                brightness,
                brightness
        ));

        g2.fillOval(x, y, size, size);
    }
}