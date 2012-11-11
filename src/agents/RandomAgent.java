package agents;

import java.util.Random;
import main.Flag;

/**
 * @author Jaroslaw Pawlak
 */
public class RandomAgent extends AbstractAgent {

    @Override
    public int getNewFlag(int round) {
        return new Random().nextInt(Flag.COUNT);
    }

}
