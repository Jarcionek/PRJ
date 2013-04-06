package network.GUI.simulation;

import java.awt.Dimension;
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

    void startSimulationWithVariousAgents(Class[] agents, boolean[] infected,
                                                                    int flags) {
        simulation = new Simulation(network, agents, infected, flags);
        
        startSimulation(infected);
    }

    void startSimulationWithAllAgentsTheSame(Class agent, int flags) {
        simulation = new Simulation(network, agent, flags);
        
        startSimulation(null);
    }
    
    private void startSimulation(boolean[] infected) {
        ContentPane contentPane = new ContentPane(this, infected);
        this.setContentPane(contentPane);
        
        Dimension size = this.getSize();
        size.width += contentPane.getExtraWidth();
        size.height += contentPane.getExtraHeight();
        this.setSize(size);
        
        this.revalidate();
        this.repaint();
    }
    
}
