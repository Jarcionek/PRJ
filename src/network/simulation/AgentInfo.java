package network.simulation;

/**
 * @author Jaroslaw Pawlak
 */
public class AgentInfo {
    
    public final AbstractAgent agent;
    public final int id;

    public AgentInfo(AbstractAgent agent, int id) {
        this.agent = agent;
        this.id = id;
    }
    
}
