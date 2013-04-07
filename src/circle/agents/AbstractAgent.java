package circle.agents;

/**
 * @author Jaroslaw Pawlak
 * 
 * This class should contain methods required by the simulation
 */
public abstract class AbstractAgent implements AgentDelegate {
    
    private int flag = -1;    
    
    protected AgentDelegate[] visibleAgents;
    
    final void setVisibleAgents(AgentDelegate[] agents) {
        visibleAgents = agents;
    }

    public abstract int getNewFlag(int round);
    
    final void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public final int getFlag() {
        return flag;
    }
}
