import java.awt.Color;

public class Star extends Body {
    final StarType type;

    Star(String name, StarType type, Location l, Velocity v) {
        super(name, type.mass, l, type.radius, v);
        this.type = type;
    }

    // Retains compatibility with the original constructor.
    Star(String name, double mass, Location l,
         double radius, Velocity v) {
        super(name, mass, l, radius, v);
        this.type = StarType.G;
    }

    @Override
    Color colour() {
        return type.color;
    }
}