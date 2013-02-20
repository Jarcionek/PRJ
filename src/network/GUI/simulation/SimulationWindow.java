package network.GUI.simulation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import network.creator.Network;
import network.simulation.Simulation;
import network.simulation.agents.LeastCommonFlagAgent;
import network.simulation.agents.RandomAgent;

/**
 * @author Jaroslaw Pawlak
 */
public class SimulationWindow extends JFrame {

    private static final String TITLE = "Simulation";
    
    private final Network network;
    private final Simulation simulation;
    
    // fields
    private final SimulationDrawablePane drawPane;;
    private final JButton nextRoundButton;
    
    public SimulationWindow(Network network, String networkName) {
        super(TITLE + ": " + networkName);
        this.network = network;
        this.simulation = new Simulation(network, LeastCommonFlagAgent.class, 2); //TODO number of flags
        
        drawPane = new SimulationDrawablePane(simulation, network);
        nextRoundButton = new JButton("next round");
        customiseComponents();
        createLayout();
        
        this.setSize(800, 600);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void customiseComponents() {
        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulation.nextRound();
                SimulationWindow.this.repaint();
            }
        });
    }

    private void createLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(drawPane, BorderLayout.CENTER);
        contentPanel.add(nextRoundButton, BorderLayout.SOUTH);
        setContentPane(contentPanel);
    }
    
}
