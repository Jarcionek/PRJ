package network.GUI.simulation;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Jaroslaw Pawlak
 */
class InitialisationSettings {
    
    final int indexAgent;
    final int indexInfected;
    final int indexFlags;
    final int indexConsensus;
    final boolean checkboxIncludeInfected;
    
    private final boolean[] selection;

    InitialisationSettings(int indexBehaviour, int indexInfectedBehaviour,
            int indexFlags, int indexConsensus, boolean checkboxIncludeInfected,
            boolean[] selection) {
        this.indexAgent = indexBehaviour;
        this.indexInfected = indexInfectedBehaviour;
        this.indexFlags = indexFlags;
        this.indexConsensus = indexConsensus;
        this.checkboxIncludeInfected = checkboxIncludeInfected;
        this.selection = selection;
    }
    
    Iterable<Integer> getSelected() {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private boolean hasNext = false;
                    private int current = 0;

                    {
                        for (int i = 0; i < selection.length; i++) {
                            if (selection[i]) {
                                current = i;
                                hasNext = true;
                                break;
                            }
                        }
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public Integer next() {
                        if (hasNext) {
                            int returnValue = current;
                            hasNext = false;
                            for (int i = current + 1; i < selection.length; i++) {
                                if (selection[i]) {
                                    current = i;
                                    hasNext = true;
                                    break;
                                }
                            }
                            return returnValue;
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public void remove() {}
                };
            }
        };
    }
    
}
