package network.simulation.agents;

import java.util.Random;

/**
 * Least Common Flag Agent - Non Deterministic
 * <p>
 * This agents extends {@link LeastCommonFlagAgent} and uses its calculation algorithm.
 * This agents have a chance that it will behave reasonable (i.e. it will raise
 * a flag as returned by a super class). If this agent is about to raise
 * different flag than in the previous round, the chance drops by 10%, otherwise
 * it raises by 10%. If agent decides to behave not reasonably, it raises the
 * same flag as in the previous round and the chance is reset to 100%.
 * 
 * @author Jaroslaw Pawlak
 */
public class LCFAgentND extends LeastCommonFlagAgent {

    private double chance = 1.0d;
    
    public LCFAgentND(int maxFlags) {
        super(maxFlags);
    }

    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(maxFlags);
        }
        
        int result = super.getNewFlag(round);
        
        if (result != getFlag()) {
            chance -= 0.1d;
            chance = Math.max(0.0d, chance);
        } else {
            chance += 0.1d;
            chance = Math.min(1.0d, chance);
        }
        
        if (new Random().nextInt(1000) > chance * 1000) {
            chance = 1.0d;
            return getFlag();
        } else {
            return result;
        }
    }

}
