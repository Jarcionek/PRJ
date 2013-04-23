package network.simulation.agents;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * Compared to ThirdAgent2F, its chance changes in 33% steps.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2F2Nc4 extends ThirdAgent2F2N {

    public ThirdAgent2F2Nc4(int maxFlags) {
        super(maxFlags);
    }

    @Override
    protected double up(double chance) {
        return chance + (1.0d / 3);
    }

    @Override
    protected double down(double chance) {
        return chance - (1.0d / 3);
    }

}
