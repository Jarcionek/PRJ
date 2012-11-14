package main;

import agents.RandomAgent;
import agents.ThirdAgent;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    
    public static void main(String[] args) {
        
        int agents = 20;
        int visibility = 1;

        CircleSimulation simulation
                = new CircleSimulation(agents, visibility, ThirdAgent.class);
        new SimulationGUI(simulation);
    }

}
