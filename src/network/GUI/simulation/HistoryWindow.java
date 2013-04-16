package network.GUI.simulation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import network.creator.Network;
import network.painter.GraphPainter;

/**
 * @author Jaroslaw Pawlak
 */
public class HistoryWindow extends JFrame {
    
    private final JSlider slider;
    private final AbstractDrawablePane drawPane;
    
    private final Dictionary<Integer, JLabel> labels;
    
    private int currentRound = 0;
    
    public HistoryWindow(Network network, final boolean[] infected,
            final List<int[]> history, String name) {
        super("History: " + name + " (" + history.size() + " rounds)");
        
        slider = new JSlider(SwingConstants.HORIZONTAL, 0, history.size() - 1, 0) {};
        drawPane = new AbstractDrawablePane(network) {

            @Override
            Color getFlag(int id) {
                return GraphPainter.getColor(history.get(currentRound)[id]);
            }

            @Override
            boolean containsInfections() {
                return infected != null;
            }

            @Override
            boolean isInfected(int id) {
                return infected[id];
            }

            @Override
            int getSelectionID() {
                return -1;
            }
            
        };
        
        labels = new Hashtable<Integer, JLabel>();
        labels.put(0, new JLabel("0"));
        labels.put(history.size() - 1, new JLabel("" + (history.size() - 1)));
        
        customiseComponents();
        createLayout();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void customiseComponents() {
        slider.setMajorTickSpacing(slider.getMaximum());
        slider.setMinorTickSpacing(1);
        slider.setPaintTrack(true);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setLabelTable(labels);
        slider.addChangeListener(new ChangeListener() {
            private int lastValue = 0;
            @Override
            public void stateChanged(ChangeEvent e) {
                int currentValue = slider.getValue();
                if (lastValue != currentValue) {
                    if (lastValue != slider.getMinimum()
                            && lastValue != slider.getMaximum()) {
                        labels.remove(lastValue);
                        slider.setLabelTable(labels);
                    }
                    if (currentValue != slider.getMinimum()
                            && currentValue != slider.getMaximum()) {
                        labels.put(currentValue, new JLabel("" + currentValue));
                        slider.setLabelTable(labels);
                    }
                    currentRound = slider.getValue();
                    slider.revalidate();
                    slider.repaint();
                    drawPane.repaint();
                    lastValue = currentValue;
                }
            }
        });
    }

    private void createLayout() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(drawPane, BorderLayout.CENTER);
        contentPane.add(slider, BorderLayout.SOUTH);
        setContentPane(contentPane);
    }
    
}
