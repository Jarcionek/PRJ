package network.simulation.agents;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * Compared to ThirdAgent2F, its chance changes in 50% steps.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2F2Nc8 extends ThirdAgent2F2N {

    public ThirdAgent2F2Nc8(int maxFlags) {
        super(maxFlags);
    }

    @Override
    protected double up(double chance) {
        return chance * 2.0d;
    }

    @Override
    protected double down(double chance) {
        return chance / 2.0d;
    }

}
