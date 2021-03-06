package network.simulation;

/**
 * This class should contain only methods required by the simulation.
 * 
 * @author Jaroslaw Pawlak
 */
public abstract class AbstractAgent implements AgentDelegate {
    
    /**
     * Flag raised by the agent in the last round - this should be set
     * by the simulation and not by the agent itself.
     */
    private int flag = -1;    
    
    protected final int maxFlags;
            
    public AbstractAgent(int maxFlags) {
        this.maxFlags = maxFlags;
    }
    
    /**
     * This agent's neighbours (should not contain this agent).
     */
    protected AgentDelegate[] neighbours;

    /**
     * Simulation calls this method to ask the agent what flag it wants
     * to raise in the next round.
     */
    public abstract int getNewFlag(int round);
    
    /**
     * Subclass of this class should not call this method.
     * This method should be called only by the simulation.
     */
    final void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public final int getFlag() {
        return flag;
    }
}
