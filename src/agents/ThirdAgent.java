package agents;

import java.util.Random;
import main.Flag;

/**
 * @author Jaroslaw Pawlak
 * 
 * Given the array of visible agents, this agent adjusts to the larger state.
 * However this agent ignores its previously raised flag and in case the states
 * are equal it raises a random flag.
 * 
 * This agent is unlikely to keep changing its flag every round. The more often
 * it behaves that way, the more probable it become that it will behave differently.
 */
public class ThirdAgent extends AbstractAgent {

    private double chance = 1.0d;
    
    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(Flag.COUNT);
        }
        
        int result;
        
        int greenState = 0;
        int yellowState = 0;
        
        int myIndex = (visibleAgents.length - 1) / 2;
        
        for (int i = 0; i < visibleAgents.length; i++) {
            if (i == myIndex) {
                continue;
            }
            
            if ((i + visibleAgents[i].getFlag()) % 2 == 0) {
                greenState++;
            } else {
                yellowState++;
            } 
        }
        
        if (greenState == yellowState) {
            result = new Random().nextInt(Flag.COUNT);
        } else if (greenState > yellowState) {
            if (myIndex % 2 == 0) {
                result = 0;
            } else {
                result = 1;
            }
        } else {
            if (myIndex % 2 == 0) {
                result = 1;
            } else {
                result = 0;
            }
        }
        
        if (result != getFlag()) {
            chance -= 0.1d;
            chance = Math.max(0.0d, chance);
        } else {
            chance += 0.1d;
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
