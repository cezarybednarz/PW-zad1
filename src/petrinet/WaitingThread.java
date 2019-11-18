package petrinet;

import java.util.Collection;
import java.util.concurrent.Semaphore;

public class WaitingThread<T> {

    private Collection<Transition<T>> transitions;
    private Semaphore mutex;

    public WaitingThread(Collection<Transition<T>> transitions) {
        this.transitions = transitions;

        this.mutex = new Semaphore(1);
    }

    public Collection<Transition<T>> getTransitions() { return transitions; }

    public Semaphore getMutex() { return mutex; }
}
