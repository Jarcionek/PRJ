package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * This is a replicated behaviour of SecondAgent in circle simulation.
 * 
 * @author Jaroslaw Pawlak
 */
public class SecondAgent extends AbstractAgent {

    public SecondAgent(int maxFlags) {
        super(maxFlags);
    }
    
    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(2);
        }
        
        int result;
        
        if (neighbours[0].getFlag() != neighbours[1].getFlag()) {
            result = new Random().nextInt(2);
        } else {
            if (neighbours[0].getFlag() == 0) {
                result = 1;
            } else {
                result = 0;
            }
        }
        
        return result;
    }

}
