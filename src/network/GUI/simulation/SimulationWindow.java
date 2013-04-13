package network.GUI.simulation;

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
    
    private static int lastX = Integer.MIN_VALUE;
    private static int lastY = 0;
    
    final Network network;
    final Simulation simulation;
    
    public SimulationWindow(Simulation simulation, String networkName) {
        super("Simulation: " + networkName);
        
        this.network = simulation.getNetwork();
        this.simulation = simulation;
        
        SimulationContentPane contentPane = new SimulationContentPane(this);
        this.setContentPane(contentPane);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                lastX = getX();
                lastY = getY();
            }
        });
        
        Dimension size = new Dimension(800, 600);
        size.width += contentPane.getExtraWidth();
        size.height += contentPane.getExtraHeight();
        this.setSize(size);
        
        if (lastX == Integer.MIN_VALUE) {
            this.setLocationByPlatform(true);
        } else {
            this.setLocation(lastX, lastY);
        }
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

}
