package network.GUI.simulation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import network.GUI.Constants;
import network.creator.Node;
import network.painter.GraphPainter;

/**
 * @author Jaroslaw Pawlak
 */
class SimulationContentPane extends JPanel implements Constants {

    private final SimulationWindow window;
    
    private final AbstractDrawablePane drawPane;
    
    private final JLabel roundLabel;
    private final JLabel isConsensusLabel;
    private final JLabel firstConsensusLabel;
    private final JButton nextRoundButton;
    private final JButton untilConsensusButton;
    private final JButton historyButton;
    
    private final AgentMemoryAccessor agentMemoryAccessor;
    
    private int selectionID = -1;
    
    SimulationContentPane(SimulationWindow window) {
        super(new BorderLayout());
        this.window = window;
        
        boolean isConsensus = window.simulation.isConsensus();
        
        drawPane = new AbstractDrawablePane(window.network) {

            private final boolean[] infected;
            
            {
                infected = SimulationContentPane.this.window.simulation
                        .getInfected();
            }
            
            @Override
            Color getFlag(int id) {
                return GraphPainter.getColor(SimulationContentPane.this.window
                        .simulation.getFlag(id));
            }

            @Override
            boolean containsInfections() {
                return SimulationContentPane.this.window.simulation
                        .containsInfection();
            }

            @Override
            boolean isInfected(int id) {
                return infected[id];
            }

            @Override
            int getSelectionID() {
                return selectionID;
            }
        };
        
        roundLabel = new JLabel("Round: " + window.simulation.getRound());
        isConsensusLabel = new JLabel("Consensus: " + (isConsensus? "YES" : "NO"));
        firstConsensusLabel = new JLabel("First consensus at round: "
                                                  + (isConsensus? "0" : "N/A"));
        nextRoundButton = new JButton("Next round");
        untilConsensusButton = new JButton("Play until consensus");
        historyButton = new JButton("History");
        
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

                Node n = window.network.findClosestNode(size, x, y, D);
                
                int lastSelectionID = selectionID;
                
                if (n != null) {
                    selectionID = n.id();
                    agentMemoryAccessor.setAgent(window.simulation.getAgentInfo(n.id()));
                    agentMemoryAccessor.revalidate();
                } else {
                    selectionID = -1;
                }
                
                if (lastSelectionID != selectionID) {
                    repaint();
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
                        //TODO popup message? timeout
                        break;
                    }
                } while (!window.simulation.isConsensus()); 
                update();
            }
            
        });
        
        historyButton.setEnabled(window.simulation.isHistoryEnabled());
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame f = new HistoryWindow(window.network,
                        window.simulation.getInfected(),
                        window.simulation.getHistory(),
                        "History: " + window.networkName);
                f.setSize(window.getSize());
                f.setLocationRelativeTo(window);
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
        simulationButtons.add(historyButton);
        
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
