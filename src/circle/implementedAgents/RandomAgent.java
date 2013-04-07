package circle.implementedAgents;

import circle.agents.AbstractAgent;
import java.util.Random;
import circle.main.Flag;

/**
 * @author Jaroslaw Pawlak
 */
public class RandomAgent extends AbstractAgent {

    @Override
    public int getNewFlag(int round) {
        return new Random().nextInt(Flag.COUNT);
    }

}
