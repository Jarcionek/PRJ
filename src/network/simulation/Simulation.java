package network.simulation;

import network.creator.Network;

/**
 * @author Jaroslaw Pawlak
 */
public class Simulation {
    
    private AgentInfo[] agents;
    
    private int round = 0;

    public Simulation(Network network, Class agentClass, int maxFlags) {
        // check if given class is descended of AbstractAgent
        boolean ok = false;
        Class superClass = agentClass.getSuperclass();
        while (superClass != null) {
            if (superClass == AbstractAgent.class) {
                ok = true;
                break;
            }
            superClass = superClass.getSuperclass();
        }
        if (!ok) {
            throw new IllegalArgumentException(agentClass + " is not a descended "
                    + "of " + AbstractAgent.class);
        }
        
        // create fields
        agents = new AgentInfo[network.getNumberOfNodes()];
        
        // create agents
        for (int i = 0; i < agents.length; i++) {
            AbstractAgent agent;
            try {
                agent = (AbstractAgent) agentClass.getConstructor(int.class)
                                                         .newInstance(maxFlags);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Could not create an agent "
                        + "of " + agentClass + ": " + ex);
            }
            agents[i] = new AgentInfo(agent, i);
        }
        
        // connect agents
        for (int i = 0; i < agents.length; i++) {
            int[] neighbours = network.getNeighboursIDs(i);
            agents[i].agent.neighbours = new AgentDelegate[neighbours.length];
            for (int j = 0; j < neighbours.length; j++) {
                agents[i].agent.neighbours[j] = agents[neighbours[j]].agent;
            }
        }
    }
    
    private boolean isConsensus() {
        for (AgentInfo ai : agents) {
            for (AgentDelegate neighbour : ai.agent.neighbours) {
                if (ai.agent.getFlag() == neighbour.getFlag()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public final void nextRound() {
        int[] newFlags = new int[agents.length];
        for (int i = 0; i < agents.length; i++) {
            newFlags[i] = agents[i].agent.getNewFlag(round);
        }
        
        for (int i = 0; i < agents.length; i++) {
            agents[i].agent.setFlag(newFlags[i]);
        }
        round++;
        
//        if (HISTORY_ENABLED) {
//            history.add(newFlags);
//        }
    }
    
    public final int getFlag(int id) {
        if (id < 0 || id >= agents.length) {
            throw new IllegalArgumentException("id out of range! id = " + id);
        }
        
        return agents[id].agent.getFlag();
    }
    
}
