package network.GUI.simulation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import network.creator.Node;

/**
 * @author Jaroslaw Pawlak
 */
class ContentPane extends JPanel {

    private final SimulationWindow window;
    
    private final SimulationDrawablePane drawPane;
    
    private final JLabel roundLabel;
    private final JLabel isConsensusLabel;
    private final JLabel firstConsensusLabel;
    private final JButton nextRoundButton;
    private final JButton untilConsensusButton;
    
    private final AgentMemoryAccessor agentMemoryAccessor;
    
    ContentPane(SimulationWindow window) {
        super(new BorderLayout());
        this.window = window;
        
        drawPane = new SimulationDrawablePane(window.simulation, window.network);
        
        roundLabel = new JLabel("Round: " + window.simulation.getRound());
        isConsensusLabel = new JLabel("Consensus: NO");
        firstConsensusLabel = new JLabel("First consensus at round: N/A");
        nextRoundButton = new JButton("Next round");
        untilConsensusButton = new JButton("Play until consensus");
        
        agentMemoryAccessor = new AgentMemoryAccessor(
                                             window.simulation.getAgentInfo(0));
        
        customiseComponents();
        createLayout();
    }
    
    private void customiseComponents() {
        drawPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Dimension size = drawPane.getSize();
                int x = e.getX();
                int y = e.getY();

                Node n = window.network.findClosestNode(size, x, y, C.S);
                if (n != null) {
                    drawPane.setSelectionID(n.id());
                    agentMemoryAccessor.setAgent(window.simulation.getAgentInfo(n.id()));
                    agentMemoryAccessor.revalidate();
                }
            }
            
        });
        
        nextRoundButton.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                window.simulation.nextRound();
                update();
            }
            
        });
        
        untilConsensusButton.addActionListener(new ActionListener() {
            
            private static final long TIMEOUT = 5000;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                long start = System.currentTimeMillis();
                do {
                    window.simulation.nextRound();
                    if (System.currentTimeMillis() - start > TIMEOUT) {
                        //TODO popup message?
                        break;
                    }
                } while (!window.simulation.isConsensus()); 
                update();
            }
            
        });
    }

    private void createLayout() {
        JPanel simulationButtons = new JPanel(new GridLayout(0, 1, 3, 3));
        simulationButtons.add(roundLabel);
        simulationButtons.add(isConsensusLabel);
        simulationButtons.add(firstConsensusLabel);
        simulationButtons.add(nextRoundButton);
        simulationButtons.add(untilConsensusButton);
        
        JPanel eastPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        eastPanel.add(simulationButtons);
        
        add(drawPane, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);
        add(agentMemoryAccessor, BorderLayout.SOUTH);
    }
    
    private void update() {
        roundLabel.setText("Round: " + window.simulation.getRound());
        isConsensusLabel.setText("Consensus: "
                + (window.simulation.isConsensus()? "YES" : "NO"));
        if (firstConsensusLabel.getText().contains("N/A")
                && window.simulation.isConsensus()) {
            firstConsensusLabel.setText("First consensus at round: "
                    + window.simulation.getRound());
        }
        window.repaint();
    }
    
    /**
     * Returns a width of side panels.
     */
    int getExtraWidth() {
        return getPreferredSize().width - drawPane.getPreferredSize().width;
    }
    
    /**
     * Returns a heigh of side panels.
     */
    int getExtraHeight() {
        return agentMemoryAccessor.getPreferredSize().height;
    }
}
