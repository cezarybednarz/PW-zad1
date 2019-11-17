package petrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PetriNet<T> {

    public Map<T, Integer> current;
    boolean fair;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        this.current = initial;
        this.fair = fair;
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {

    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {

    }


}