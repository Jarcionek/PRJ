package network.GUI.simulation;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import network.creator.Network;
import network.simulation.Simulation;

/**
 * @author Jaroslaw Pawlak
 */
public class SimulationWindow extends JFrame {

    private static final String TITLE = "Simulation";
    
    private static InitialisationSettings settings = null;
    
    final Network network;
    Simulation simulation;
    
    public SimulationWindow(Network network, String networkName) {
        super(TITLE + ": " + networkName);
        this.network = network;
        
        InitialisationPane initPane = new InitialisationPane(this);
        initPane.setSettings(settings);
        this.setContentPane(initPane);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                saveInitialisationSettings();
            }
        });
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationByPlatform(true);
        this.setVisible(true);
    }

    void startSimulationWithVariousAgents(Class[] agents, boolean[] infected,
                            int flags, int consensus, boolean includeInfected) {
        simulation = new Simulation(network, agents, infected, flags, consensus,
                                                               includeInfected);
        
        startSimulation(infected);
    }

    void startSimulationWithAllAgentsTheSame(Class agent, int flags,
                                       int consensus, boolean includeInfected) {
        simulation = new Simulation(network, agent, flags, consensus,
                                                               includeInfected);
        
        startSimulation(null);
    }
    
    private void startSimulation(boolean[] infected) {
        saveInitialisationSettings();
        
        ContentPane contentPane = new ContentPane(this, infected);
        this.setContentPane(contentPane);
        
        Dimension size = this.getSize();
        size.width += contentPane.getExtraWidth();
        size.height += contentPane.getExtraHeight();
        this.setSize(size);
        
        this.revalidate();
        this.repaint();
    }
    
    private void saveInitialisationSettings() {
        Container c = getContentPane();
        if (c != null && c.getClass() == InitialisationPane.class) {
            settings = ((InitialisationPane) c).getSettings();
        }
    }
    
}
