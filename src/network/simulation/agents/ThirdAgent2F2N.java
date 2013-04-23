package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * This is a replicated behaviour of ThirdAgent in circle simulation.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2F2N extends AbstractAgent {

    private double chance = 1.0d;

    public ThirdAgent2F2N(int maxFlags) {
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
            chance = Math.max(0.0d, down(chance));
        } else {
            chance = Math.min(1.0d, up(chance));
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
    
    protected double up(double chance) {
        return chance + 0.1d;
    }
    
    protected double down(double chance) {
        return chance - 0.1d;
    }

}
