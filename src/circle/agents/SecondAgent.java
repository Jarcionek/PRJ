package circle.agents;

import java.util.Random;
import circle.main.Flag;

/**
 * @author Jaroslaw Pawlak
 * 
 * Given the array of visible agents, this agent adjusts to the larger state.
 * However this agent ignores its previously raised flag and in case the states
 * are equal it raises a random flag.
 * <p>
 * These agents stuck if they all raise the same flag - they will keep changing
 * their flags every round.
 */
public class SecondAgent extends AbstractAgent {

    @Override
    public int getNewFlag(int round) {
        if (round == 0) {
            return new Random().nextInt(Flag.COUNT);
        }
        
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
            return new Random().nextInt(Flag.COUNT);
        } else if (greenState > yellowState) {
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
