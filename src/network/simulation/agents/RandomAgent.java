package network.simulation.agents;

import java.util.Random;
import network.simulation.AbstractAgent;

/**
 * @author Jaroslaw Pawlak
 */
public class RandomAgent extends AbstractAgent {

    @Override
    public int getNewFlag(int round) {
        return new Random().nextInt(4); //TODO how many flags are there?
    }

}
