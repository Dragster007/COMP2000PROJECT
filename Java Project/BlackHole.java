
import java.awt.Color;

public class BlackHole extends Body {

    // Controls how large the black hole looks.
    private int displayRadius;

    BlackHole(
            String name,
            double mass,
            Location l,
            double radius,
            Velocity v) {

        super(name, mass, l, radius, v);

        displayRadius = 25;
    }

    // The black hole gains mass when it consumes another body.
    public void absorb(Body body) {

        if (body == this) {
            return;
        }

        mass += body.mass;

        // Increases the black hole's size.
        radius += body.radius;

        // Makes the growth visible on screen.
        if (body instanceof Star) {

            Star star = (Star) body;

            displayRadius += Math.max(
                    1,
                    star.getDisplayRadius() / 2
            );

        } else if (body instanceof Planet) {

            Planet planet = (Planet) body;

            displayRadius += Math.max(
                    1,
                    planet.type.displayRadius / 2
            );

        } else {

            displayRadius += 2;
        }

        System.out.println(
                name + " consumed " + body.name
        );

        System.out.println(
                "Black hole mass: " + mass
        );

        System.out.println(
                "Black hole radius: " + radius
        );
    }

    // Returns the size used when drawing the black hole.
    public int getDisplayRadius() {
        return displayRadius;
    }

    @Override
    Color colour() {
        return Color.BLACK;
    }
}
