package network.GUI.simulation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
    
    ContentPane(SimulationWindow window) {
        super(new BorderLayout());
        this.window = window;
        
        drawPane = new SimulationDrawablePane(window.simulation, window.network);
        
        roundLabel = new JLabel("Round: " + window.simulation.getRound());
        isConsensusLabel = new JLabel("Consensus: NO");
        firstConsensusLabel = new JLabel("First consensus at round: N/A");
        nextRoundButton = new JButton("Next round");
        untilConsensusButton = new JButton("Play until consensus");
        
        customiseComponents();
        createLayout();
    }
    
    private void customiseComponents() {
        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.simulation.nextRound();
                update();
            }
        });
        untilConsensusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long start = System.currentTimeMillis();
                final long timeout = 5000;
                do {
                    window.simulation.nextRound();
                    if (System.currentTimeMillis() - start > timeout) {
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
     * @return 
     */
    int getExtraHeight() {
        return 0;
    }
}
