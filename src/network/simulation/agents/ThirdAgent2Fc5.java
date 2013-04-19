package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two agents.
 * 
 * Compared to ThirdAgent2F, its chance changes in 50% steps.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2Fc5 extends AbstractAgent {

    private double chance = 1.0d;

    public ThirdAgent2Fc5(int maxFlags) {
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
        
        if (result != getFlag()) {
            chance -= 0.5d;
            chance = Math.max(0.0d, chance);
        } else {
            chance += 0.5d;
            chance = Math.min(1.0d, chance);
        }
        
        if (new Random().nextInt(1000) > chance * 1000) {
            chance = 1.0d;
            if (result == 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return result;
        }
    }

}