import java.awt.Color;

    public class Planet extends Body{


        Planet(String name , double mass, Location l,double radius, Velocity v){
            super(name, mass,l,radius,v);


        }

    @Override
    Color colour(){
        return new Color(120,170,255);
    }

    void collide(){
        System.out.print("Planet destroyed");
    }
    void stable(){
        System.out.print("Planet stable ");
    }
}
