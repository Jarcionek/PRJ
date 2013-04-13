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

    //TODO add InitialisationWindow and extract it from SimulationWindow
    //     so the simulation GUI can be started by providing just Simulation
    
    private static final String TITLE = "Simulation";
    
    private static InitialisationSettings settings = null;
    private static int lastX = Integer.MIN_VALUE;
    private static int lastY = 0;
    
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
            public void windowClosing(WindowEvent e) {
                lastX = getX();
                lastY = getY();
                saveInitialisationSettings();
            }
        });
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        
        if (lastX == Integer.MIN_VALUE) {
            this.setLocationByPlatform(true);
        } else {
            this.setLocation(lastX, lastY);
        }
        
        this.setVisible(true);
    }

    void startSimulationWithVariousAgents(Class[] agents, boolean[] infected,
            int flags, boolean consensus, boolean includeInfected) {
        
        simulation = new Simulation(network, agents, infected, flags,
                includeInfected, consensus, true);
        
        startSimulation(infected);
    }

    void startSimulationWithAllAgentsTheSame(Class agent, int flags,
            boolean consensus) {
        
        simulation = new Simulation(network, agent, flags, consensus, true);
        
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
