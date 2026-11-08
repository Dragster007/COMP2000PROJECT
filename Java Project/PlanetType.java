import java.awt.Color;

public enum PlanetType {
    ROCKY("Rocky planet", 30.03, 0.025, 7, 100, true,
            new Color(100, 165, 240)),

    GAS_GIANT("Gas giant", 954.6, 0.12, 13, 300, false,
            new Color(223, 175, 125)),

    ICE_GIANT("Ice giant", 51.5, 0.07, 10, 200, false,
            new Color(99, 220, 229)),

    DWARF("Dwarf planet", 0.6, 0.005, 5, 60, false,
            new Color(177, 153, 154));

    final String label;
    final double mass;
    final double radius;
    final int displayRadius;
    final int maxHealth;
    final boolean lifeEligible;
    final Color color;

    PlanetType(String label, double mass, double radius,
               int displayRadius, int maxHealth,
               boolean lifeEligible, Color color) {
        this.label = label;
        this.mass = mass;
        this.radius = radius;
        this.displayRadius = displayRadius;
        this.maxHealth = maxHealth;
        this.lifeEligible = lifeEligible;
        this.color = color;
    }

    @Override
    public String toString() {
        return label;
    }
}