package network.GUI.simulation;

import javax.swing.JFrame;
import network.creator.Network;
import network.simulation.Simulation;

/**
 * @author Jaroslaw Pawlak
 */
public class SimulationWindow extends JFrame {

    private static final String TITLE = "Simulation";
    
    final Network network;
    Simulation simulation;
    
    public SimulationWindow(Network network, String networkName) {
        super(TITLE + ": " + networkName);
        this.network = network;
        
        this.setContentPane(new InitialisationPane(this));
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    void startSimulation(Class[] agents, int flags) {
        simulation = new Simulation(network, agents[0], flags);
        this.setContentPane(new ContentPane(this));
        this.revalidate();
        this.repaint();
    }
    
}
