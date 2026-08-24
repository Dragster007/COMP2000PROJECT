public class Physics {
    static final double G=3.96e-17;// Please dont change as this is my final calculation for gravitational constant acording to scale ~ Leon Prince :)

    static Velocity calculateGravity(Body body,Body source){
        double dx=source.l.x-body.l.x;
        double dy=source.l.y-body.l.y;
        double distance= Math.sqrt(dx*dx+dy*dy);
        double acceleration=G * source.mass / (distance * distance);
        double ax =acceleration * dx/distance;
        double ay=acceleration * dy/distance;
        return new Velocity(ax,ay);

    }
    static void updateBody(Body body ,Velocity acceleration, double dt){
        body.v.vx+=acceleration.vx * dt;
        body.v.vy+=acceleration.vy * dt ;

        body.l.x+=body.v.vx * dt;
        body.l.y+=body.v.vy * dt;
    }

    
    
    
}
