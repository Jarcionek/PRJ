package agents;

import java.util.Random;
import main.Flag;

/**
 * @author Jaroslaw Pawlak
 * 
 * Given the array of visible agents, this agent adjusts to the larger state.
 * It considers its previous flag.
 * <p>
 * These agents stuck if half of the circle is in one state and the other half
 * is in another state.
 */
public class FirstAgent extends AbstractAgent {

    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(Flag.COUNT);
        }
        
        int greenState = 0;
        int yellowState = 0;
        
        for (int i = 0; i < visibleAgents.length; i++) {
            if ((i + visibleAgents[i].getFlag()) % 2 == 0) {
                greenState++;
            } else {
                yellowState++;
            } 
        }
        
        int myIndex = (visibleAgents.length - 1) / 2;
        
        if (greenState > yellowState) {
            if (myIndex % 2 == 0) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (myIndex % 2 == 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
