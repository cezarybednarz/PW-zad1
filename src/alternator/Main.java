package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static class Worker implements Runnable {

        private PetriNet<String> current;
        private Collection<Transition<String>> transitions;
        private boolean interrupted;

        public Worker(PetriNet<String> current, Collection<Transition<String>> transitions) {
            this.current = current;
            this.transitions = transitions;

            this.interrupted = false;
        }

        @Override
        public void run() {
            try {
                while(!interrupted) {
                    current.fire(transitions);
                }
            } catch (InterruptedException e) {
                interrupted = true;
                Thread t = Thread.currentThread();
                t.interrupt();
                System.err.println(t.getName() + " przerwany");
            }
        }
    }

    public static void main(String[] args) {

        HashMap<String, Integer> initial = new HashMap<>();

        initial.put("A", 1);
        initial.put("B", 1);
        initial.put("C", 1);
        initial.put("Sem", 1); // semafor

        PetriNet<String> petrinet = new PetriNet<>(initial, false);

        HashMap<String, Integer> PinA = new HashMap<>();
        HashMap<String, Integer> PoutA = new HashMap<>();
        HashMap<String, Integer> KinA = new HashMap<>();
        HashMap<String, Integer> KoutA = new HashMap<>();


        HashMap<String, Integer> PinB = new HashMap<>();
        HashMap<String, Integer> PoutB = new HashMap<>();
        HashMap<String, Integer> KinB = new HashMap<>();
        HashMap<String, Integer> KoutB = new HashMap<>();


        HashMap<String, Integer> PinC = new HashMap<>();
        HashMap<String, Integer> PoutC = new HashMap<>();
        HashMap<String, Integer> KinC = new HashMap<>();
        HashMap<String, Integer> KoutC = new HashMap<>();

        // in critical section max one token

        //A
        PinA.put("A", 1);
        PinA.put("Sem", 1);
        PoutA.put("Crit", 1);
        PoutA.put("PA", 1);

        KinA.put("Crit", 1);
        KinA.put("PA", 1);
        KoutA.put("Sem", 1);
        KoutA.put("A", 1);

        // B
        PinA.put("B", 1);
        PinA.put("Sem", 1);
        PoutA.put("Crit", 1);
        PoutA.put("PB", 1);

        KinB.put("Crit", 1);
        KinB.put("PB", 1);
        KoutB.put("Sem", 1);
        KoutB.put("B", 1);

        //C
        PinC.put("C", 1);
        PinC.put("Sem", 1);
        PoutC.put("Crit", 1);
        PoutC.put("PC", 1);

        KinC.put("Crit", 1);
        KinC.put("PC", 1);
        KoutC.put("Sem", 1);
        KoutC.put("C", 1);



        Transition<String> PtranA = new Transition<>(PinA, Collections.emptySet(),
                Collections.emptySet(), PoutA);
        Transition<String> KtranA = new Transition<>(KinA, Collections.emptySet(),
                Collections.emptySet(), KoutA);

        Transition<String> PtranB = new Transition<>(PinB, Collections.emptySet(),
                Collections.emptySet(), PoutB);
        Transition<String> KtranB = new Transition<>(KinB, Collections.emptySet(),
                Collections.emptySet(), KoutB);

        Transition<String> PtranC = new Transition<>(PinC, Collections.emptySet(),
                Collections.emptySet(), PoutC);
        Transition<String> KtranC = new Transition<>(KinC, Collections.emptySet(),
                Collections.emptySet(), KoutC);


        Collection<Transition<String>> allTransitions = new ArrayList<>();
        allTransitions.add(PtranA);
        allTransitions.add(KtranA);
        allTransitions.add(PtranB);
        allTransitions.add(KtranB);
        allTransitions.add(PtranC);
        allTransitions.add(KtranC);

        // all reachable states in my petri net
        Set<Map<String, Integer>> reachableStates = petrinet.reachable(allTransitions);

        System.out.println("reachable states in petri net (we can see that its finite):\n " + reachableStates + "\n");
        System.out.println("checking if there is maximum of one token in critical section and in other places:");

        boolean okNumberOfTokens = true;

        for(Map<String, Integer> state : reachableStates) {
            for(String key : state.keySet()) {
                if(state.get(key) > 1) {
                    okNumberOfTokens = false;
                }
            }
        }

        if(okNumberOfTokens) {
            System.out.println("Correct! there is max one token in critical section");
        }

        // checking multi threading
        Collection<Transition<String>> AThreadTran = new ArrayList<>();
        AThreadTran.add(PtranA);
        AThreadTran.add(KtranA);

        Collection<Transition<String>> BThreadTran = new ArrayList<>();
        BThreadTran.add(PtranB);
        BThreadTran.add(KtranB);

        Collection<Transition<String>> CThreadTran = new ArrayList<>();
        CThreadTran.add(PtranC);
        CThreadTran.add(KtranC);

        Thread A = new Worker(petrinet, AThreadTran);

    }
}
