package search.core;
import java.util.ArrayList;

public interface BestFirstObject<T extends BestFirstObject<T>> {
    // Pre: None
    // Post: Returns all objects that can be generated from this with one move
    public ArrayList<T> getSuccessors();

    public int hashCode();

    public boolean equals(Object other);
    
    public boolean achieves(T goal);
}
