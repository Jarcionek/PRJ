package network.simulation.agents;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * Compared to ThirdAgent2F, its chance changes in 50% steps.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2Fc5 extends ThirdAgent2F {

    public ThirdAgent2Fc5(int maxFlags) {
        super(maxFlags);
    }

    @Override
    protected double up(double chance) {
        return chance + 0.5d;
    }

    @Override
    protected double down(double chance) {
        return chance - 0.5d;
    }

}
