package main;

import agents.AgentInfo;
import agents.ThirdAgent;
import java.util.HashMap;

/**
 * @author Jaroslaw Pawlak
 */
public class CircleSimulation {
    
    private final int visibility;
    private final AgentInfo[] agents;
    private int round = 0;
    
    public CircleSimulation(int numberOfAgents, int visibility) {
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
            agents[i] = new AgentInfo(new ThirdAgent(), i);
        }
        
        // connect agents
        for (int i = 0; i < agents.length; i++) {
            AgentInfo[] visibleAgents = new AgentInfo[visibility * 2 + 1];
            for (int j = -visibility; j <= visibility; j++) {
                visibleAgents[j + visibility] = agents[(i + j + agents.length) % agents.length];
            }
            agents[i].setVisibleAgents(visibleAgents);
        }
        
        firstRound();
    }
    
    public final void firstRound() {
        if (round != 0) {
            throw new RuntimeException("firstRound() call during round " + round);
        }
        
        int[] flags = getFirstRoundFlags();
        
        // check if overriden
        if (flags == null) {
            nextRound();
            return;
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
    }
    
    public final int getRound() {
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
        return new CircleSimulation(agents.length, visibility);
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
}
