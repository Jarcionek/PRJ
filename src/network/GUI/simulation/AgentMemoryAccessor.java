package network.GUI.simulation;

import network.simulation.AgentInfo;
import util.MemoryAccessor;

/**
 * @author Jaroslaw Pawlak
 */
class AgentMemoryAccessor extends MemoryAccessor {
    
    private AgentInfo ai;

    AgentMemoryAccessor(AgentInfo ai) {
        super();
        setAgent(ai);
    }

    final void setAgent(AgentInfo ai) {
        this.ai = ai;
        setObject(ai.agent);
    }

    @Override
    public String getTitle() {
        return "Agent " + ai.id + " memory ("
                                    + ai.agent.getClass().getSimpleName() + ")";
    }
    
}
