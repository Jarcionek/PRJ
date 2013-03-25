package circle.main;

import circle.agents.AgentInfo;
import util.MemoryAccessor;

/**
 * @author Jaroslaw Pawlak
 */
public class AgentMemoryAccessor extends MemoryAccessor {

    private AgentInfo ai;
    
    public AgentMemoryAccessor(AgentInfo ai) {
        super();
        setAgent(ai);
    }
    
    public final void setAgent(AgentInfo ai) {
        this.ai = ai;
        setObject(ai.agent);
    }

    @Override
    public String getTitle() {
        return "Agent " + ai.id + " memory ("
                                    + ai.agent.getClass().getSimpleName() + ")";
    }
    
}
