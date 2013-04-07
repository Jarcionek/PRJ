package circle.agents;

/**
 * @author Jaroslaw Pawlak
 * 
 * This class contains all information about the agent that agent should not
 * have access to.
 * 
 * This class should contain only methods that access/modify its fields
 * and not the agent. Agent should be accessed only by using
 * agentInfoObject.agent
 */
public class AgentInfo {
    
    public final AbstractAgent agent;
    public final int id;

    private AgentInfo[] visibleAgents;
    
    public AgentInfo(AbstractAgent agent, int id) {
        this.agent = agent;
        this.id = id;
    }
    
    public final void setVisibleAgents(AgentInfo[] visibleAgents) {
        this.visibleAgents = visibleAgents;
        AgentDelegate[] ad = new AgentDelegate[visibleAgents.length];
        for (int i = 0; i < ad.length; i++) {
            ad[i] = visibleAgents[i].agent;
        }
        this.agent.setVisibleAgents(ad);
    }

    public final AgentInfo[] getVisibleAgents() {
        return visibleAgents;
    }
    
    public final void setFlag(int flag) {
        agent.setFlag(flag);
    }
}

