package network.simulation;

import network.creator.Network;

/**
 * @author Jaroslaw Pawlak
 */
public class Simulation {
    
    private final AgentInfo[] agentsInfo;
    private final boolean[] infected;
    
    /**
     * This shows the number of the next round.
     */
    private int round = 0;

    public Simulation(Network network, Class agentClass, int maxFlags) {
        checkAgentClass(agentClass);
        
        agentsInfo = new AgentInfo[network.getNumberOfNodes()];
        infected = null;
        
        // create agents
        for (int i = 0; i < agentsInfo.length; i++) {
            AbstractAgent agent;
            try {
                agent = (AbstractAgent) agentClass.getConstructor(int.class)
                                                         .newInstance(maxFlags);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Could not create an agent "
                        + "of " + agentClass + ": " + ex);
            }
            agentsInfo[i] = new AgentInfo(agent, i);
        }
        
        connectAgents(network);
        
        nextRound();
    }
    
    public Simulation(Network network, Class[] agentsClass, boolean infected[],
                                                                 int maxFlags) {
        if (agentsClass == null) {
            throw new NullPointerException("agentsClass array is null");
        }
        if (infected == null) {
            throw new NullPointerException("infected array is null");
        }
        if (network.getNumberOfNodes() != agentsClass.length) {
            throw new IllegalArgumentException("Number of nodes ("
                    + network.getNumberOfNodes() + ") is different than number"
                    + "of agents (" + agentsClass.length + ")");
        }
        if (infected.length != agentsClass.length) {
            throw new IllegalArgumentException("infected length ("
                    + infected.length + ") is different than number"
                    + "of agents (" + agentsClass.length + ")");
        }
        
        for (Class c : agentsClass) {
            checkAgentClass(c);
        }
        
        agentsInfo = new AgentInfo[network.getNumberOfNodes()];
        this.infected = infected;
        
        // create agents
        for (int i = 0; i < agentsInfo.length; i++) {
            AbstractAgent agent;
            try {
                agent = (AbstractAgent) agentsClass[i].getConstructor(int.class)
                                                         .newInstance(maxFlags);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Could not create an agent "
                        + "of " + agentsClass[i] + ": " + ex);
            }
            agentsInfo[i] = new AgentInfo(agent, i);
        }
        
        connectAgents(network);
        
        nextRound();
    }
    
////// INITIALISATION //////////////////////////////////////////////////////////
    
    /**
     * Checks if given class is descended of AbstractAgent.
     */
    private void checkAgentClass(Class agentClass) {
        if (agentClass == null) {
            throw new NullPointerException("agentClass is null");
        }
        
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
    }
    
    private void connectAgents(Network network) {
        for (int i = 0; i < agentsInfo.length; i++) {
            int[] neighbours = network.getNeighboursIDs(i);
            agentsInfo[i].neighbours = new AgentInfo[neighbours.length];
            agentsInfo[i].agent.neighbours = new AgentDelegate[neighbours.length];
            for (int j = 0; j < neighbours.length; j++) {
                agentsInfo[i].neighbours[j] = agentsInfo[neighbours[j]];
                agentsInfo[i].agent.neighbours[j] = agentsInfo[neighbours[j]].agent;
            }
        }
    }
    
////// PUBLIC METHODS //////////////////////////////////////////////////////////
    
    public boolean isConsensus() {
        for (AgentInfo ai : agentsInfo) {
            for (AgentDelegate neighbour : ai.agent.neighbours) {
                if (ai.agent.getFlag() == neighbour.getFlag()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isConsensusIgnoreInfected() {
        if (infected == null) {
            return isConsensus();
        }
        
        for (AgentInfo ai : agentsInfo) {
            if (infected[ai.id]) {
                continue;
            }
            for (AgentInfo neighbour : ai.neighbours) {
                if (infected[neighbour.id]) {
                    continue;
                }
                if (ai.agent.getFlag() == neighbour.agent.getFlag()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isConsensus(boolean includeInfected) {
        return includeInfected? isConsensus() : isConsensusIgnoreInfected();
    }
    
    public boolean containsInfection() {
        if (infected != null) {
            for (boolean b : infected) {
                if (b) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public final void nextRound() {
        int[] newFlags = new int[agentsInfo.length];
        for (int i = 0; i < agentsInfo.length; i++) {
            newFlags[i] = agentsInfo[i].agent.getNewFlag(round);
        }
        
        for (int i = 0; i < agentsInfo.length; i++) {
            agentsInfo[i].agent.setFlag(newFlags[i]);
        }
        round++;
        
//        if (HISTORY_ENABLED) {
//            history.add(newFlags);
//        }
    }
    
    public final int getFlag(int id) {
        if (id < 0 || id >= agentsInfo.length) {
            throw new IllegalArgumentException("id out of range! id = " + id);
        }
        
        return agentsInfo[id].agent.getFlag();
    }
    
    /**
     * Returns the number of last round played. Initialisation round has number 0.
     * This is in fact a number of normal rounds played.
     */
    public final int getRound() {
        return round - 1;
    }
    
    public AgentInfo getAgentInfo(int i) {
        return agentsInfo[i];
    }
    
}
