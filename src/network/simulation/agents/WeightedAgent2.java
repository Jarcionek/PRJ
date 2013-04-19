package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;
import util.WeightedRandom;

/**
 * Plays differentiation.
 * 
 * @author Jaroslaw Pawlak
 */
public class WeightedAgent2 extends AbstractAgent {

    public WeightedAgent2(int maxFlags) {
        super(maxFlags);
    }
    
    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(maxFlags);
        }
        
        // if everything was fine last round, play the same flag
        boolean isOk = true;
        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i].getFlag() == this.getFlag()) {
                isOk = false;
            }
        }
        if (isOk) {
            return this.getFlag();
        }
        
        int[] flagCount = new int[maxFlags];
        for (int i = 0; i < neighbours.length; i++) {
            flagCount[neighbours[i].getFlag()]++;
        }
        
        WeightedRandom wr = new WeightedRandom();
        for (int i = 0; i < maxFlags; i++) {
            // there is always a little chance (+1) of raising any flag
            wr.add(i, neighbours.length - flagCount[i] + 1);
        }
        
        return wr.getRandomValue();
    }
    
}
