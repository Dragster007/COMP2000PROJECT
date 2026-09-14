import java.awt.Color;

public class Star  extends Body{


    Star(String name , double mass, Location l, double radius,Velocity v ){
        super(name,mass,l,radius,v);
    }

    @Override
    Color colour(){
        return new Color(255,214,102);
    }
}
