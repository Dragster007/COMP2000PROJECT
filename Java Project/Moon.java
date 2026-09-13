import java.awt.Color;

public class Moon extends Body {
    Moon(String name , double mass, Location l,double radius, Velocity v){
            super(name, mass,l,radius,v);


        }

    @Override
    Color colour(){
        return new Color(190,190,190);
    }
}
