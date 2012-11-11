package agents;

/**
 * @author Jaroslaw Pawlak
 * 
 * This class contains all information about the agent that agent should not
 * have access to.
 */
public class AgentInfo {
    
    public final AbstractAgent agent;
    public final int id;

    private AgentInfo[] visibleAgents;

    public void setVisibleAgents(AgentInfo[] visibleAgents) {
        this.visibleAgents = visibleAgents;
        AgentDelegate[] ad = new AgentDelegate[visibleAgents.length];
        for (int i = 0; i < ad.length; i++) {
            ad[i] = visibleAgents[i].agent;
        }
        this.agent.setVisibleAgents(ad);
    }
    
    public AgentInfo(AbstractAgent agent, int id) {
        this.agent = agent;
        this.id = id;
    }

    public AgentInfo[] getVisibleAgents() {
        return visibleAgents;
    }
    
    public void setFlag(int flag) {
        agent.setFlag(flag);
    }
    
    public int getFlag() {
        return agent.getFlag();
    }
    
}

