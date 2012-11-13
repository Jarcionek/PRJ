package main;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    
    public static void main(String[] args) {
        
        int agents = 20;
        int visibility = 1;
//test
        new SimulationGUI(new CircleSimulation(agents, visibility));
    }

}
