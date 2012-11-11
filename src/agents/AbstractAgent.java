package agents;

/**
 * @author Jaroslaw Pawlak
 * 
 * This class should contain methods required by the simulation
 */
public abstract class AbstractAgent implements AgentDelegate {
    
    private int flag = -1;    
    
    protected AgentDelegate[] visibleAgents;
    
    public final void setVisibleAgents(AgentDelegate[] agents) {
        visibleAgents = agents;
    }

    public abstract int getNewFlag(int round);
    
    /**
     * Subclass of this class should not call this method.
     * This method should be called only by the simulation.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int getFlag() {
        return flag;
    }
}
