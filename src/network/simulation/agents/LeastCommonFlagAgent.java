package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;
import network.simulation.AgentDelegate;

/**
 * Deterministic Agent that raises random flag from among those flags
 * that was raised the least number of times by its neighbours in the previous
 * round. For example:
 * 
 * maximum number of different flags is 4
 * agent A has 3 neighbours who raised flags 0, 1, 2
 * agent B has 2 neighbours who raised flags 0, 3
 * agent C has 4 neighbours who raised flags 0, 1, 2, 3
 * 
 * agent A will raise flag 3
 * agent B will raise flag 1 or 2
 * agent C will raise flag 0, 1, 2 or 3
 * 
 * @author Jaroslaw Pawlak
 */
public class LeastCommonFlagAgent extends AbstractAgent {

    public LeastCommonFlagAgent(int maxFlags) {
        super(maxFlags);
    }
    
    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(maxFlags);
        }
        
        boolean ok = true;
        for (AgentDelegate ad : neighbours) {
            if (ad.getFlag() == this.getFlag()) {
                ok = false;
                break;
            }
        }
        if (ok) {
            return this.getFlag();
        }
        
        // neighboursFlags[flag] = number of agents that raised this flag
        int[] neighboursFlags = new int[maxFlags];
        for (AgentDelegate ad : neighbours) {
            neighboursFlags[ad.getFlag()]++;
        }
        
        // how many times was the least common flag raised
        int minFlag = Integer.MAX_VALUE;
        for (int i = 0; i < neighboursFlags.length; i++) {
            if (neighboursFlags[i] < minFlag) {
                minFlag = neighboursFlags[i];
            }
        }
        
        // how many different flags were raised in the same quantity
        int count = 0;
        for (int f : neighboursFlags) {
            if (f == minFlag) {
                count++;
            }
        }
        
        // indices[i] = flag
        int[] indices = new int[count];
        int j = 0;
        for (int i = 0 ; i < neighboursFlags.length; i++) {
            if (neighboursFlags[i] == minFlag) {
                indices[j++] = i;
            }
        }
        
        return indices[new Random().nextInt(count)];
    }
    
}
