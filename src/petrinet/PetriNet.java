package petrinet;

import java.util.*;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {

    private Map<T, Integer> current;
    private boolean fair;
    private Semaphore mutex;
    private Queue<WaitingThread<T>> waitingThreadQueue;

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        this.current = new HashMap<>(initial);
        this.fair = fair;

        this.mutex = new Semaphore(1);
        this.waitingThreadQueue = new LinkedList<>();
    }

    private int getTokens(Map<T, Integer> current, T place) {
        if(current.containsKey(place)) {
            return current.get(place);
        }
        return 0;
    }

    private boolean enabled(Map<T, Integer> current, Transition<T> transition) {
        for(T place : transition.getInput().keySet()) {
            if(getTokens(current, place) < transition.getInput().get(place)) {
                return false;
            }
        }
        for(T place : transition.getInhibitor()) {
            if(getTokens(current, place) != 0) {
                return false;
            }
        }
        return true;
    }

    private void fire(Map<T, Integer> current, Transition<T> transition) {
        for(T place : transition.getInput().keySet()) {
            int currentTokens = getTokens(current, place) - transition.getInput().get(place);
            if(currentTokens == 0) {
                current.remove(place);
            }
            else {
                current.put(place, currentTokens);
            }
        }

        for(T place : transition.getReset()) {
            current.remove(place);
        }

        for(T place : transition.getOutput().keySet()) {
            if(transition.getOutput().get(place) != 0) {
                int currentTokens = getTokens(current, place) + transition.getOutput().get(place);
                current.put(place, currentTokens);
            }
        }
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
        Map<T, Integer> current;

        try {
            mutex.acquire();
            current = new HashMap<>(this.current);
        } catch(InterruptedException e) {
            e.printStackTrace();
            return Collections.emptySet();
        } finally {
            mutex.release();
        }

        Set<Map<T, Integer>> reachable = new HashSet<>();
        LinkedList<Map<T, Integer>> placesQueue = new LinkedList<>();
        placesQueue.add(current);

        while(!placesQueue.isEmpty()) {
            Map<T, Integer> now = new HashMap<>(placesQueue.pop());

            if(reachable.contains(now)) {
                continue;
            }

            reachable.add(now);

            for(Transition<T> transition : transitions) {
                Map<T, Integer> next = new HashMap<>(now);
                if(enabled(next, transition)) {
                    fire(next, transition);
                    placesQueue.push(next);
                }
            }
        }

        return reachable;
    }

    private Optional<Transition<T>> findEnabledTransition(Collection<Transition<T>> transitions) {
        for(Transition<T> transition : transitions) {
            if(enabled(current, transition)) {
                return Optional.of(transition);
            }
        }
        return Optional.empty();
    }

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {

        Optional<Transition<T>> enabledTransition;
        Optional<WaitingThread<T>> enabledThread = Optional.empty();

        try {

            mutex.acquire();

            try {
                enabledTransition = findEnabledTransition(transitions);

                if (enabledTransition.isEmpty()) {

                    waitingThreadQueue.add(new WaitingThread<>(transitions));
                    mutex.release();

                    waitingThreadQueue.element().getMutex().acquire();

                    enabledTransition = findEnabledTransition(transitions);
                }

                enabledTransition.ifPresent(tTransition -> fire(current, tTransition));

                for (WaitingThread<T> waitingThread : waitingThreadQueue) {
                    if (findEnabledTransition(waitingThread.getTransitions()).isPresent()) {
                        enabledThread = Optional.of(waitingThread);
                        waitingThreadQueue.remove(waitingThread);
                        break;
                    }
                }
            } finally {
                if (enabledThread.isPresent()) {
                    enabledThread.get().getMutex().release();
                } else {
                    mutex.release();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }

        return enabledTransition.orElse(null);
    }

}