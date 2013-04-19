package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;
import util.WeightedRandom;

/**
 * @author Jaroslaw Pawlak
 */
public class WeightedAgent extends AbstractAgent {

    public WeightedAgent(int maxFlags) {
        super(maxFlags);
    }
    
    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(maxFlags);
        }
        
        int[] flagCount = new int[maxFlags];
        for (int i = 0; i < neighbours.length; i++) {
            flagCount[neighbours[i].getFlag()]++;
        }
        
        WeightedRandom wr = new WeightedRandom();
        for (int i = 0; i < maxFlags; i++) {
            wr.add(i, neighbours.length - flagCount[i]);
        }
        
        return wr.getRandomValue();
    }
    
}
