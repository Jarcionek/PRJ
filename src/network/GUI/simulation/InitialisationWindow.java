package network.GUI.simulation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import network.GUI.Constants;
import network.creator.Network;
import network.creator.Node;
import network.painter.GraphPainter;
import network.simulation.Simulation;
import network.simulation.agents.LCFAgentND;
import network.simulation.agents.LeastCommonFlagAgent;
import network.simulation.agents.RandomAgent;
import network.simulation.agents.WeightedAgent;

/**
 * @author Jaroslaw Pawlak
 */
public class InitialisationWindow extends JFrame implements Constants {

    //TODO find all classes within a package or all subclasses of a class
    //     This is not possible in standard Java Reflections API
    private static final Class[] agentClasses = new Class[] {
        WeightedAgent.class,
        LCFAgentND.class,
        LeastCommonFlagAgent.class,
        RandomAgent.class,
    };

    private static final String[] agentNames = new String[agentClasses.length];
    static {
        for (int i = 0; i < agentNames.length; i++) {
            agentNames[i] = agentClasses[i].getSimpleName();
        }
    }
    
    
    
    private static InitialisationSettings settings = null;
    
    private final String networkName;
    private final Network network;
    private final boolean[] selection;
    
    public InitialisationWindow(Network network, String networkName) {
        super("Simulation initialisation: " + networkName);
        
        this.networkName = networkName;
        this.network = network;
        this.selection = new boolean[network.getNumberOfNodes()];
        
        final InitialisationPane initPane = new InitialisationPane();
        initPane.setSettings(settings);
        this.setContentPane(initPane);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                settings = initPane.getSettings();
            }
        });
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
////// CLASSES /////////////////////////////////////////////////////////////////
    
    private class InitialisationPane extends JPanel {

        private final JLabel labelAgent = new JLabel("Agent behaviour:");
        private final JComboBox<String> listAgent = new JComboBox<String>(agentNames);
        private final JLabel labelInfected = new JLabel("Infected agent behaviour:");
        private final JComboBox<String> listInfected = new JComboBox<String>(agentNames);
        private final JButton buttonStart = new JButton("Start");
        private final JLabel labelFlags = new JLabel("Number of flags:");
        private final JComboBox<String> listFlags = new JComboBox<String>();
        private final JLabel labelConsensus = new JLabel("Consensus:");
        private final JComboBox<String> listConsensus = new JComboBox<String>();
        private final JCheckBox checkboxConsiderInfected = new JCheckBox("Include infections", false);

        private final AbstractDrawablePane drawPane = new AbstractDrawablePane(network) {

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        Dimension size = drawPane.getSize();
                        Node n = network.findClosestNode(size, e.getX(), e.getY(), D);
                        if (n != null) {
                            selection[n.id()] = !selection[n.id()];
                            drawPane.repaint();
                        }
                    }
                });
            }
            
            @Override
            Color getFlag(int id) {
                return Color.lightGray;
            }

            @Override
            boolean containsInfections() {
                return true;
            }

            @Override
            boolean isInfected(int id) {
                return selection[id];
            }

            @Override
            int getSelectionID() {
                return -1;
            }
            
        };

        InitialisationPane() {
            super(new BorderLayout());

            // COMPONENTS
            for (int i = MIN_FLAG; i <= MAX_FLAG; i++) {
                listFlags.addItem("" + i);
            }

            int flags = GraphPainter.getRequiredColors(network);
            if (!network.containsEdgesInteresctions()) {
                flags = Math.min(4, flags);
                listFlags.setSelectedItem("" + flags);
            } else {
                if (flags > MAX_FLAG) {
                    //TODO some information that consensus may not be achievable?
                    // GraphPainter is not perfect so it does not have to be true
                    listFlags.setSelectedItem("" + MAX_FLAG);
                } else {
                    listFlags.setSelectedItem("" + flags);
                }
            }
            listFlags.setToolTipText("recommended: " + flags);
            listFlags.setMaximumRowCount(10);

            buttonStart.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Class[] agents = new Class[selection.length];
                    boolean anythingSelected = false;

                    for (int i = 0; i < selection.length; i++) {
                        if (selection[i]) {
                            anythingSelected = true;
                            agents[i] = agentClasses[listInfected.getSelectedIndex()];
                        } else {
                            agents[i] = agentClasses[listAgent.getSelectedIndex()];
                        }
                    }

                    int flags = listFlags.getSelectedIndex() + MIN_FLAG;
                    Simulation simulation;
                    
                    if (anythingSelected) {
                        boolean includeInfected = checkboxConsiderInfected.isSelected();
                        simulation = new Simulation(network, agents, selection,
                                flags, includeInfected, consensus(), true);
                    } else {
                        simulation = new Simulation(network, agents[0], flags,
                                consensus(), true);
                    }
                    
                    settings = getSettings();
                    startSimulation(simulation);
                }
            });

            listConsensus.addItem("Differentiation");
            listConsensus.addItem("Colouring");

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
            bottom.add(labelConsensus, c);
            c.insets = new Insets(0, 5, 0, 0);
            bottom.add(listConsensus, c);
            c.insets = new Insets(0, 25, 0, 0);
            bottom.add(checkboxConsiderInfected, c);
            bottom.add(buttonStart, c);
            topPanel.add(bottom);


            this.add(topPanel, BorderLayout.NORTH);
            this.add(drawPane, BorderLayout.CENTER);
        }

        private InitialisationSettings getSettings() {
            return new InitialisationSettings(listAgent.getSelectedIndex(),
                    listInfected.getSelectedIndex(), listFlags.getSelectedIndex(),
                    listConsensus.getSelectedIndex(), checkboxConsiderInfected.isSelected(),
                    selection);
        }

        private void setSettings(InitialisationSettings settings) {
            if (settings == null) {
                return;
            }
            listAgent.setSelectedIndex(settings.indexAgent);
            listInfected.setSelectedIndex(settings.indexInfected);
            listFlags.setSelectedIndex(settings.indexFlags);
            listConsensus.setSelectedIndex(settings.indexConsensus);
            checkboxConsiderInfected.setSelected(settings.checkboxIncludeInfected);
            for (Integer i : settings.getSelected()) {
                if (i >= network.getNumberOfNodes()) {
                    break;
                }
                selection[i] = true;
            }
        }

        private boolean consensus() {
            // 0 is differentiation, 1 is colouring
            return listConsensus.getSelectedIndex() == 0?
                    Simulation.DIFFERENTIATION : Simulation.COLOURING;
        }

        private void startSimulation(Simulation simulation) {
            settings = getSettings();
            
            new SimulationWindow(simulation, networkName);
            
            InitialisationWindow.this.dispose();
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
