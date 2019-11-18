package petrinet;

import java.util.Collection;
import java.util.concurrent.Semaphore;

class WaitingThread<T> {

    private Collection<Transition<T>> transitions;
    private Semaphore mutex;

    WaitingThread(Collection<Transition<T>> transitions) {
        this.transitions = transitions;

        this.mutex = new Semaphore(1);
    }

    Collection<Transition<T>> getTransitions() { return transitions; }

    Semaphore getMutex() { return mutex; }
}
