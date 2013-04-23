package network.simulation.agents;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * Compared to ThirdAgent2F, its chance is multiplied/divided by 2.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2F2Nc3 extends ThirdAgent2F2N {

    public ThirdAgent2F2Nc3(int maxFlags) {
        super(maxFlags);
    }

    @Override
    protected double up(double chance) {
        return chance * 2;
    }

    @Override
    protected double down(double chance) {
        return chance / 2;
    }

}
