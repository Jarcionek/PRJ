package circle.main;

import circle.agents.*;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    
    public static final boolean MEMORY_ACCESSOR_ENABLED = true;
            
    private static final int AGENTS = 20;
    private static final int VISIBILITY = 1;
    private static final Class AGENT = ConsistentAgent.class;
    
    public static void main(String[] args) {
        CircleSimulation sim = new CircleSimulation(AGENTS, VISIBILITY, AGENT);
        new SimulationGUI(sim);
    }

}
