package main;

import agents.AbstractAgent;
import agents.AgentInfo;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jaroslaw Pawlak
 */
public class CircleSimulation {
    
    //TODO move it to main, or maybe to the constructor?
    public static final boolean HISTORY_ENABLED = true;
    
    private final Class agentClass;
    private final int visibility;
    private final AgentInfo[] agents;
    
    /**
     * This should be equal to the number of rounds.
     */
    private int round = 0;
    
    private List<int[]> history;
    
    public CircleSimulation(int numberOfAgents, int visibility, Class agentClass) {
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
        this.agentClass = agentClass;
        
        if (numberOfAgents < Flag.COUNT) {
            throw new IllegalArgumentException("There should be at least "
                    + "as many agents as flags: agents = " + numberOfAgents
                    + ", flags = " + Flag.COUNT);
        }
        
        // check visibility
        if (visibility > numberOfAgents / 2 || visibility < 0) {
            throw new IllegalArgumentException("Illegal visibility: "
                    + "visibility = " + visibility + ". Should be positive "
                    + "and no greater than numberOfAgents/2");
        }
        
        // check if consensus possible
        if (numberOfAgents % Flag.COUNT != 0) {
            throw new IllegalArgumentException("Consensus impossible: "
                    + "numberOfAgents = " + numberOfAgents
                    + ", flags = " + Flag.COUNT);
        }
        
        agents = new AgentInfo[numberOfAgents];
        this.visibility = visibility;
        
        // create agents
        for (int i = 0; i < agents.length; i++) {
            AbstractAgent agent;
            try {
                agent = (AbstractAgent) agentClass.getConstructor().newInstance();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Could not create an agent "
                        + "of " + agentClass + ": " + ex);
            }
            agents[i] = new AgentInfo(agent, i);
        }
        
        // connect agents
        for (int i = 0; i < agents.length; i++) {
            AgentInfo[] visibleAgents = new AgentInfo[visibility * 2 + 1];
            for (int j = -visibility; j <= visibility; j++) {
                visibleAgents[j + visibility] = agents[(i + j + agents.length) % agents.length];
            }
            agents[i].setVisibleAgents(visibleAgents);
        }
        
        if (HISTORY_ENABLED) {
            history = new LinkedList<int[]>();
        }
        
        firstRound();
    }
    
    /**
     * Called in the constructor.
     */
    private void firstRound() {
        if (round != 0) {
            throw new RuntimeException("firstRound() call during round " + round);
        }
        
        int[] flags = getFirstRoundFlags();
        
        // check if overriden
        if (flags == null) {
            nextRound();
            return;
        }
        
        if (HISTORY_ENABLED) {
            history.add(flags);
        }
        
        // check length
        if (flags.length != agents.length) {
            throw new RuntimeException("firstRoundFlags() returns an array "
                    + "of different length than the number of agents");
        }
        
        // check flags
        for (int i = 0; i < flags.length; i++) {
            if (flags[i] < 0 || flags[i] >= Flag.COUNT) {
                throw new RuntimeException("firstRoundFlags() returns an array "
                        + "containing invalid flag: i = " + i + ", flag = "
                        + flags[i] + ", Flag.COUNT = " + Flag.COUNT);
            }
        }
        
        // apply
        for (int i = 0; i < flags.length; i++) {
            agents[i].agent.setFlag(flags[i]);
        }
        round++;
    }

    /**
     * Override it to define flags raised in the first round. This will override
     * agents' behaviour for the first round that is usually random choice.
     */
    protected int[] getFirstRoundFlags() {
        return null;
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
        
        if (HISTORY_ENABLED) {
            history.add(newFlags);
        }
    }
    
    public final int getRoundNumber() {
        return round;
    }
    
    public final boolean isConsensus() {
        for (int i = 1; i < agents.length; i++) {
            if (agents[i - 1].agent.getFlag() == agents[i].agent.getFlag()) {
                return false;
            }
        }
        return agents[0].agent.getFlag() != agents[agents.length - 1].agent.getFlag();
    }
    
    public AgentInfo getAgentInfo(int i) {
        return agents[i];
    }
    
    public CircleSimulation getNew() {
        return new CircleSimulation(agents.length, visibility, agentClass);
    }
    
    public int getNumberOfAgents() {
        return agents.length;
    }
    
    public int getVisibility() {
        return visibility;
    }
    
    public String getAgentsType() {
        HashMap map = new HashMap<String, Integer>();
        for (int i = 0; i < agents.length; i++) {
            String type = agents[i].agent.getClass().toString();
            type = type.replace("class ", "");
            if (map.keySet().contains(type)) {
                map.put(type, (Integer) map.get(type) + 1);
            } else {
                map.put(type, 1);
            }
        }
        
        return map.toString();
    }
    
    /**
     * Returns the flags raised in given round (as a copy).
     */
    public int[] getRoundFlags(int round) {
        if (!HISTORY_ENABLED) {
            throw new FeatureDisabledException("Cannot access the history");
        }
        
        if (round < 0 || round > history.size()) {
            throw new IllegalArgumentException("No such round in the history: "
                    + "round = " + round + ", history size = " + history.size());
        }
        
        int[] copy = new int[agents.length];
        System.arraycopy(history.get(round), 0, copy, 0, agents.length);
        return copy;
    }
    
    public void historyModifyLastRound(int agent, int flag) {
        if (!HISTORY_ENABLED) {
            return;
        }
        
        if (flag < 0 || flag >= Flag.COUNT) {
            throw new IllegalArgumentException("No such flag");
        }
        
        history.get(history.size() - 1)[agent] = flag;
    }
}
