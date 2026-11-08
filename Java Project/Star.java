
import java.awt.Color;

public class Star extends Body {

    final StarType type;

    // NEW: Additional visible radius from consumed planets.
    private int absorbedDisplayRadius = 0;

    Star(String name, StarType type, Location l, Velocity v) {
        super(name, type.mass, l, type.radius, v);
        this.type = type;
    }

    // Original constructor retained for compatibility.
    Star(String name, double mass, Location l,
         double radius, Velocity v) {

        super(name, mass, l, radius, v);
        this.type = StarType.G;
    }

    // NEW: Star consumes a planet.
    public void absorbPlanet(Planet planet) {

        // Add the planet's mass.
        this.mass += planet.mass;

        // Add the planet's physical simulation radius.
        this.radius += planet.radius;

        // Add its displayed radius for visible growth.
        absorbedDisplayRadius += planet.type.displayRadius;

        System.out.println(
                name + " consumed " + planet.name
        );

        System.out.println(
                "New star mass: " + mass
        );

        System.out.println(
                "New star radius: " + radius
        );
    }

    // NEW: Calculate how large the star looks.
    public int getDisplayRadius() {

        int originalRadius = Math.max(
                6,
                (int) Math.round(type.radius * 6)
        );

        return originalRadius + absorbedDisplayRadius;
    }

    @Override
    Color colour() {
        return type.color;
    }
}
