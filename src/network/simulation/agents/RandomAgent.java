package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;

/**
 * @author Jaroslaw Pawlak
 */
public class RandomAgent extends AbstractAgent {

    public RandomAgent(int maxFlags) {
        super(maxFlags);
    }
    
    @Override
    public int getNewFlag(int round) {
        return new Random().nextInt(maxFlags);
    }

}
