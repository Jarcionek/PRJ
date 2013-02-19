package network.simulation;

/**
 * @author Jaroslaw Pawlak
 */
public class AgentInfo {
    
    public final AbstractAgent agent;
    public final int id;
    
//    public double x;
//    public double y;

    public AgentInfo(AbstractAgent agent, int id) {
        this.agent = agent;
        this.id = id;
    }

//    public AgentInfo(AbstractAgent agent, int id, double x, double y) {
//        this.agent = agent;
//        this.id = id;
//        this.x = x;
//        this.y = y;
//    }
    
}
