import java.awt.Color;

public enum StarType {
    G("Yellow dwarf", 1000000, 1.0, 5800, 3.0,
            new Color(255, 219, 94)),

    K("Orange dwarf", 700000, 0.20, 4500, 2.2,
            new Color(255, 157, 66)),

    M("Red dwarf", 200000, 0.008, 3000, 1.2,
            new Color(255, 100, 80));

    final String label;
    final double mass;
    final double luminosity;
    final int temperature;
    final double radius;
    final Color color;

    StarType(String label, double mass, double luminosity,
             int temperature, double radius, Color color) {
        this.label = label;
        this.mass = mass;
        this.luminosity = luminosity;
        this.temperature = temperature;
        this.radius = radius;
        this.color = color;
    }
}