package agents;

import java.util.Random;
import main.Flag;

/**
 * @author Jaroslaw Pawlak
 * 
 * Agent number four.
 * 
 * This agent remembers flags raised in one previous round by all its neighbours
 * at calculates their consistency. Then it should try to adjust to whatever
 * state is larger and more consistent - however it doesn't work as expected yet.
 */
public class ConsistentAgent extends AbstractAgent {

    private int[] consistency;
    private int[] previousFlag;
    
    private void init() {
        consistency = new int[visibleAgents.length];
        for (int i = 0; i < consistency.length; i++) {
            consistency[i] = 10;
        }
        
        previousFlag = new int[visibleAgents.length];
    }
    
    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            init();
            return new Random().nextInt(Flag.COUNT);
        }
        
        // update consistency
        for (int i = 0; i < visibleAgents.length; i++) {
            if (visibleAgents[i].getFlag() == previousFlag[i]) {
                consistency[i]++;
                if (consistency[i] > 20) {
                    consistency[i] = 20;
                }
            } else {
                consistency[i]--;
                if (consistency[i] < 0) {
                    consistency[i] = 0;
                }
            }
        }
        
        int myIndex = (visibleAgents.length - 1) / 2;
        
        if (consistency[myIndex] == 0) {
            return getFlag(); // raise the same flag as previously
        }
        
        int result;
        
        int greenStateConsistency = 0;
        int yellowStateConsistency = 0;

        for (int i = 0; i < visibleAgents.length; i++) {
            if (i == myIndex) {
                continue;
            }
            
            if ((i + visibleAgents[i].getFlag()) % 2 == 0) {
                greenStateConsistency += consistency[i];
            } else {
                yellowStateConsistency += consistency[i];
            } 
        }
        
        if (greenStateConsistency == yellowStateConsistency) {
            result = new Random().nextInt(Flag.COUNT);
        } else if (greenStateConsistency > yellowStateConsistency) {
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

        return result;
    }

}
