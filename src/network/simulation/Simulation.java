package network.simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import network.creator.Network;

/**
 * @author Jaroslaw Pawlak
 */
public class Simulation {
    
    public final static boolean DIFFERENTIATION = false;
    public final static boolean COLOURING = true;
    
    private final Network network;
    private final AgentInfo[] agentsInfo;
    private final boolean[] infected;
    private final boolean consensus;
    private final boolean includeInfected;
    private final List<int[]> history;
    
    /**
     * This shows the number of the next round.
     */
    private int round = 0;
    
    public Simulation(Network network, Class agentClass, int maxFlags,
                boolean consensus, int[] initialRound, boolean history) {
        checkAgentClass(agentClass);
        
        this.network = network;
        this.agentsInfo = new AgentInfo[network.getNumberOfNodes()];
        this.infected = null;
        this.includeInfected = true; // this value doesn't really matter
        this.consensus = consensus;
        this.history = history? new LinkedList<int[]>() : null;
        
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
        
        doInitialisationRound(initialRound, maxFlags);
    }
    
    public Simulation(Network network, Class agentClass, int maxFlags,
                boolean consensus, boolean history) {
        this(network, agentClass, maxFlags, consensus, null, history);
    }
    
    public Simulation(Network network, Class[] agentsClass, boolean infected[],
                int maxFlags, boolean includeInfected, boolean consensus,
                int[] initialRound, boolean history) {
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
        
        this.network = network;
        this.agentsInfo = new AgentInfo[network.getNumberOfNodes()];
        this.infected = infected;
        this.includeInfected = includeInfected;
        this.consensus = consensus;
        this.history = history? new LinkedList<int[]>() : null;
        
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
        
        doInitialisationRound(initialRound, maxFlags);
    }
    
    public Simulation(Network network, Class[] agentsClass, boolean infected[],
                int maxFlags, boolean includeInfected, boolean consensus,
                boolean history) {
        this(network, agentsClass, infected, maxFlags, includeInfected,
                consensus, null, history);
    }
    
////// INITIALISATION (PRIVATE METHODS CALLED ONLY IN CONSTRUCTORS) ////////////
    
    private void doInitialisationRound(int[] initialRound, int maxFlags) {
        if (initialRound == null) {
            nextRound();
            return;
        }
        
        // check length
        if (agentsInfo.length != initialRound.length) {
            throw new IllegalArgumentException("Number of flags for initial "
                    + "round is different than number of agents. Agents = "
                    + agentsInfo.length + ", initialRound.length = "
                    + initialRound.length);
        }

        // check values
        for (int i : initialRound) {
            if (i < 0 || i > maxFlags) {
                throw new IllegalArgumentException("Illegal flag value = " + i);
            }
        }

        // assign values
        for (int i = 0; i < initialRound.length; i++) {
            agentsInfo[i].agent.setFlag(initialRound[i]);
        }
        round = 1;
        
        addCurrentRoundToHistory();
    }
    
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
    
////// PRIVATE METHODS /////////////////////////////////////////////////////////
    
    private boolean isColouredIncludeInfected() {
        for (AgentInfo ai : agentsInfo) {
            for (AgentDelegate neighbour : ai.agent.neighbours) {
                if (ai.agent.getFlag() != neighbour.getFlag()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isDifferentiatedIncludeInfected() {
        for (AgentInfo ai : agentsInfo) {
            for (AgentDelegate neighbour : ai.agent.neighbours) {
                if (ai.agent.getFlag() == neighbour.getFlag()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isColouredIgnoreInfected() {
        if (infected == null) {
            return isColouredIncludeInfected();
        }
        
        for (AgentInfo ai : agentsInfo) {
            if (infected[ai.id]) {
                continue;
            }
            for (AgentInfo neighbour : ai.neighbours) {
                if (infected[neighbour.id]) {
                    continue;
                }
                if (ai.agent.getFlag() != neighbour.agent.getFlag()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean isDifferentiatedIgnoreInfected() {
        if (infected == null) {
            return isDifferentiatedIncludeInfected();
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
    
    private void addCurrentRoundToHistory() {
        if (history == null) {
            return;
        }
        
        int[] current = new int[agentsInfo.length];
        for (int i = 0; i < current.length; i++) {
            current[i] = agentsInfo[i].agent.getFlag();
        }
        history.add(current);
    }

////// PUBLIC METHODS //////////////////////////////////////////////////////////
    
    /**
     * Returns the network (not a copy).
     */
    public Network getNetwork() {
        return network;
    }
    
    public boolean isConsensus() {
        if (consensus == COLOURING) {
            return includeInfected? isColouredIncludeInfected()
                                  : isColouredIgnoreInfected();
        } else { // consensus == DIFFERENTIATION
            return includeInfected? isDifferentiatedIncludeInfected()
                                  : isDifferentiatedIgnoreInfected();
        }
    }
    
    /**
     * Returns true if and only if at least one agent has been marked
     * as infected.
     * @return true if simulation contains infections, false otherwise
     */
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
    
    /**
     * Returns a copy. May be null if there are no infections.
     */
    public boolean[] getInfected() {
        return infected == null? null : Arrays.copyOf(infected, infected.length);
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
        
        addCurrentRoundToHistory();
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
    
    public boolean isHistoryEnabled() {
        return history != null;
    }
    
    /**
     * Returns an unmodifiable history.
     */
    public List<int[]> getHistoryRound() {
        return Collections.unmodifiableList(history);
    }
    
}
