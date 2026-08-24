
    public class Planet extends Body{
        

        Planet(String name , double mass, Location l,double radius, Velocity v){
            super(name, mass,l,radius,v);


        }
        
        
    
    void collide(){
        System.out.print("Planet destroyed");
    }
    void stable(){
        System.out.print("Planet stable ");
    }
}  
