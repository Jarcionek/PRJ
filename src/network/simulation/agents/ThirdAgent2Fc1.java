package network.simulation.agents;

/**
 * Plays differentiation. Uses two flags only. Needs exactly two neighbours.
 * 
 * Compared to ThirdAgent2F, its chance changes in 20% steps.
 * 
 * @author Jaroslaw Pawlak
 */
public class ThirdAgent2Fc1 extends ThirdAgent2F {

    public ThirdAgent2Fc1(int maxFlags) {
        super(maxFlags);
    }

    @Override
    protected double up(double chance) {
        return chance + 0.2d;
    }

    @Override
    protected double down(double chance) {
        return chance - 0.2d;
    }
    
}
