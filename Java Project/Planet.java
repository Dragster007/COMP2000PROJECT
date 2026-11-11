import java.awt.Color;

public class Planet extends Body {
    final PlanetType type;
    private int health;
    boolean hasLife;

    Planet(String name, PlanetType type, Location l, Velocity v) {
        super(name, type.mass, l, type.radius, v);

        this.type = type;
        this.health = type.maxHealth;
        this.hasLife = false;
    }

    // Keep the original constructors available.
    Planet(String name, double mass, Location l, double radius,
           Velocity v, boolean hasLife) {
        super(name, mass, l, radius, v);

        this.type = PlanetType.ROCKY;
        this.health = type.maxHealth;
        this.hasLife = hasLife;
    }

    Planet(String name, double mass, Location l,
           double radius, Velocity v) {
        this(name, mass, l, radius, v, false);
    }

    int getHealth() {
        return health;
    }

    void damage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Damage cannot be negative."
            );
        }

        health = Math.max(0, health - amount);

        if (health == 0) {
            hasLife = false;
        }
    }

    @Override
    Color colour() {
        if (health == 0) {
            return Color.GRAY;
        }

        if (hasLife) {
            return new Color(60, 200, 90);
        }

        return type.color;
    }
}