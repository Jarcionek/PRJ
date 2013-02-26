package network.GUI.simulation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author Jaroslaw Pawlak
 */
class ContentPane extends JPanel {

    private final SimulationWindow window;
    
    private final SimulationDrawablePane drawPane;
    private final JButton nextRoundButton;
    
    ContentPane(SimulationWindow window) {
        super(new BorderLayout());
        this.window = window;
        
        drawPane = new SimulationDrawablePane(window.simulation, window.network);
        nextRoundButton = new JButton("next round");
        customiseComponents();
        createLayout();
    }
    
    private void customiseComponents() {
        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.simulation.nextRound();
                window.repaint();
            }
        });
    }

    private void createLayout() {
        add(drawPane, BorderLayout.CENTER);
        add(nextRoundButton, BorderLayout.SOUTH);
    }
}
