import java.util.ArrayList;
import java.util.List;

//A typed group of bodies. Sorts by descending radius to prevent larger
//bodies from covering smaller ones (earlier items are drawn first)
//Prevents adding non-body objects to the list
public class Galaxy<T extends Body> {
    private final List<T> items = new ArrayList<>();

    void add(T body){
        items.add(body); 
    }

    //Returns a sorted list of bodies in descending radius
    List<T> byDescendingRadius(){
        List<T> sorted = new ArrayList<>(items);
        sorted.sort((a, b) -> Double.compare(b.radius, a.radius));
        return sorted;
    }
}
