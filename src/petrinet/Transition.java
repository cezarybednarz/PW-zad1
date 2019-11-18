package petrinet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class Transition<T> {

    private Map<T, Integer> input;
    private Collection<T> reset;
    private Collection<T> inhibitor;
    private Map<T, Integer> output;

    private Semaphore mutex = new Semaphore(1);

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        this.input = new HashMap<>(input);
        this.reset = new HashSet<>(reset);
        this.inhibitor = new HashSet<>(inhibitor);
        this.output = new HashMap<>(output);
    }

    Map<T, Integer> getInput() { return input; }

    Collection<T> getReset() { return reset; }

    Collection<T> getInhibitor() { return inhibitor; }

    Map<T, Integer> getOutput() { return output; }
}