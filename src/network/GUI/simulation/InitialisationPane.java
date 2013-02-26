package network.GUI.simulation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import network.creator.Node;
import network.graphUtil.Edge;
import network.simulation.agents.LeastCommonFlagAgent;
import network.simulation.agents.RandomAgent;

/**
 * @author Jaroslaw Pawlak
 */
class InitialisationPane extends JPanel {
    
    //TODO find all classes withing a package or all subclasses of a class
    //     This is not possible in standard Java Reflections API
    private static final Class[] agentClasses = new Class[] {
        LeastCommonFlagAgent.class,
        RandomAgent.class,
    };
    
    private static final String[] agentNames = new String[agentClasses.length];
    static {
        for (int i = 0; i < agentNames.length; i++) {
            agentNames[i] = agentClasses[i].getSimpleName();
        }
    }
    
    private final SimulationWindow window;
    private final boolean[] selection; //TODO implement multiple-node selection
    
    private final JLabel labelAgent = new JLabel("Agent behaviour:");
    private final JComboBox<String> listAgent = new JComboBox<String>(agentNames);
    private final JLabel labelInfected = new JLabel("Infected agent behaviour:");
    private final JComboBox<String> listInfected = new JComboBox<String>(agentNames);
    private final JButton buttonStart = new JButton("Start");
    private final JLabel labelFlags = new JLabel("Number of flags:");
    private final JComboBox<String> listFlags = new JComboBox<String>();
    
    private final InitDrawablePane drawPane = new InitDrawablePane();
    
    InitialisationPane(final SimulationWindow window) {
        super(new BorderLayout());
        
        this.window = window;
        selection = new boolean[window.network.getNumberOfNodes()];
        
        // COMPONENTS
        for (int i = C.MIN_FLAG; i <= C.MAX_FLAG; i++) {
            listFlags.addItem("" + i);
        }
        listFlags.setSelectedItem("4");
        listFlags.setMaximumRowCount(10);
        
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Class[] agents = new Class[selection.length];
                for (int i = 0; i < selection.length; i++) {
                    if (selection[i]) {
                        agents[i] = agentClasses[listInfected.getSelectedIndex()];
                    } else {
                        agents[i] = agentClasses[listAgent.getSelectedIndex()];
                    }
                }
                window.startSimulation(agents, listFlags.getSelectedIndex() + C.MIN_FLAG);
            }
        });
        
        //TODO implement infected behaviours handling
        labelInfected.setEnabled(false);
        listInfected.setEnabled(false);
        
        // LAYOUT
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBorder(new CompoundBorder(new BottomBorder(),
                                              new EmptyBorder(3, 3, 3, 3)));
        
        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        top.add(labelAgent, c);
        c.insets = new Insets(0, 5, 0, 0);
        top.add(listAgent, c);
        c.insets = new Insets(0, 25, 0, 0);
        top.add(labelInfected, c);
        c.insets = new Insets(0, 5, 0, 0);
        top.add(listInfected, c);
        topPanel.add(top);
        
        JPanel bottom = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridy = 1;
        bottom.add(labelFlags, c);
        c.insets = new Insets(0, 5, 0, 0);
        bottom.add(listFlags, c);
        c.insets = new Insets(0, 25, 0, 0);
        bottom.add(buttonStart, c);
        topPanel.add(bottom);
        
        
        this.add(topPanel, BorderLayout.NORTH);
        this.add(drawPane, BorderLayout.CENTER);
    }
    
    private class InitDrawablePane extends JPanel {
        
        @Override
        public void paint(Graphics g) {
            Dimension size = this.getBounds().getSize();
            int w = size.width;
            int h = size.height;

            Graphics2D g2d = (Graphics2D) g;
            
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, w, h);
            
            // edges
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.black);
            for (Edge e : window.network.getEdges(w, h)) {
                g2d.drawLine(e.x1(), e.y1(), e.x2(), e.y2());
            }
            
            // nodes
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = this.getFontMetrics(g2d.getFont());
            for (Node n : window.network) {
                // ovals
                g2d.setColor(Color.lightGray);
                g2d.fillOval((int) (n.x() * w) - C.S / 2,
                             (int) (n.y() * h) - C.S / 2, C.S, C.S);
                // labels
                g2d.setColor(Color.black);
                String id = "" + n.id();
                g2d.drawString(id, (int) (n.x() * w) - fm.stringWidth(id) / 2,
                               (int) (n.y() * h) + g2d.getFont().getSize() / 2);
                // selection
                if (selection[n.id()]) {
                    g2d.setColor(Color.red);
                    g2d.drawOval((int) (n.x() * w) - C.S/2,
                                 (int) (n.y() * h) - C.S/2, C.S, C.S);
                }
            }
        }
        
    }
    
    private class BottomBorder extends EtchedBorder {
        
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(0, 0, 2, 0);
            }
            
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                g.setColor(getHighlightColor(c));
                g.drawLine(0, h-2, w-1, h-2);
                g.setColor(getShadowColor(c));
                g.drawLine(0, h-1, w-1, h-1);
            }
            
    }

}
