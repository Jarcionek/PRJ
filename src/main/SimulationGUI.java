package main;

import agents.AgentInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * @author Jaroslaw Pawlak
 */
public class SimulationGUI extends JFrame {
    
    private static final DecimalFormat DF = new DecimalFormat("#,###");

    private static int GUIcounter = 1;
    
    private CircleSimulation sim;
    
    private JPanel drawablePanel;
    /**
     * This should show a round number as the position in the history.
     */
    private JLabel roundCounterLabel;
    private JButton startButton;
    private JButton nextRoundButton;
    private JButton resetButton;
    private JCheckBox visibilityCheckBox;
    private JCheckBox idsCheckBox;
    private JCheckBox stateCheckBox;
    private HistoryPanel historyPanel;
    private MemoryAccessor memoryAccessor;
    
    private AgentLabel[] agentLabels;
    
    public SimulationGUI(CircleSimulation sim) {
        super("SimulationGUI " + GUIcounter++);
        
        this.sim = sim;
        
        createGUIComponents();
        createLayout();
        
        setSize(640, 480);
        setMinimumSize(new Dimension(640, 480));
        setLocation(200, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        repositionAgentLabels();
    }
    
    private void createGUIComponents() {
        drawablePanel = new JPanel(null) {
            {
                setOpaque(false);
            }
            @Override
            public void paint(Graphics g) {
                Rectangle rect = g.getClipBounds();
                g.setColor(Color.white);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                if (visibilityCheckBox != null) {
                    repositionAgentLabels();
                    draw(g);
                }
                super.paint(g);
            }
        };
        
        agentLabels = new AgentLabel[sim.getNumberOfAgents()];
        for (int i = 0; i < agentLabels.length; i++) {
            agentLabels[i] = new AgentLabel(i);
            final int fi = i;
            agentLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() != MouseEvent.BUTTON3) {
                        return;
                    }
                    
                    MenuListener ml = new MenuListener() {
                        @Override
                        public void menuSelected(MenuEvent e) {
                            drawablePanel.repaint();
                        }
                        @Override
                        public void menuDeselected(MenuEvent e) {
                            drawablePanel.repaint();
                        }
                        @Override
                        public void menuCanceled(MenuEvent e) {
                            drawablePanel.repaint();
                        }
                    };
                    
                    JPopupMenu menu = new JPopupMenu();
                    
                    JMenuItem memory = new JMenuItem("access memory");
                    memory.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            memoryAccessor.setAgent(sim.getAgentInfo(fi));
                        }
                    });
                    menu.add(memory);
                    
                    JMenu setFlagMenu = new JMenu("set flag");
                    setFlagMenu.addMenuListener(ml);
                    for (int j = 0; j < Flag.COUNT; j++) {
                        final int fj = j;
                        JMenuItem menuItem = new JMenuItem("" + j);
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                sim.getAgentInfo(fi).agent.setFlag(fj);
                                sim.historyModifyLastRound(fi, fj);
                                drawablePanel.repaint();
                            }
                        });
                        setFlagMenu.add(menuItem);
                    }
                    menu.add(setFlagMenu);
                    
                    menu.show(agentLabels[fi], e.getX(), e.getY());
                    drawablePanel.repaint();
                }
            });
            drawablePanel.add(agentLabels[i]);
        }
        
        roundCounterLabel = new JLabel("0");
        
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                nextRoundButton.setEnabled(false);
                resetButton.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        while (!sim.isConsensus()) {
                            sim.nextRound();
                            /* //TODO why is there minus one? uniform it!
                             * after initialisation round, simulation round counter should be 0
                             * in other words, init. round should not be counted:
                             * after init. round the counter will show 0, this is:
                             * - the number of last round
                             * - the total number of normal rounds played
                             */
                            roundCounterLabel.setText(DF.format(sim.getRoundNumber() - 1));
                        }
                        drawablePanel.repaint();
                        Toolkit.getDefaultToolkit().beep();
                        startButton.setEnabled(true);
                        nextRoundButton.setEnabled(true);
                        resetButton.setEnabled(true);
                        if (Main.MEMORY_ACCESSOR_ENABLED) {
                            memoryAccessor.update();
                        }
                    }
                }.start();
            }
        });
        
        //TODO uniform updating - create method update that will update everything that is neccessary
        nextRoundButton = new JButton("Next round");
        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!sim.isConsensus()) {
                    sim.nextRound();
                    drawablePanel.repaint();
                    roundCounterLabel.setText(DF.format(sim.getRoundNumber() - 1));
                    if (Main.MEMORY_ACCESSOR_ENABLED) {
                        memoryAccessor.update();
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sim = sim.getNew();
                historyPanel.reset();
                drawablePanel.repaint();
                roundCounterLabel.setText(DF.format(sim.getRoundNumber() - 1));
                if (Main.MEMORY_ACCESSOR_ENABLED) {
                    memoryAccessor.update();
                }
            }
        });
        
        visibilityCheckBox = new JCheckBox("Display visibility");
        visibilityCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawablePanel.repaint();
            }
        });
        
        idsCheckBox = new JCheckBox("Display agents' ids");
        idsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawablePanel.repaint();
            }
        });
        
        stateCheckBox = new JCheckBox("Display state", true);
        stateCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawablePanel.repaint();
            }
        });
        
        historyPanel = new HistoryPanel();
        
        if (Main.MEMORY_ACCESSOR_ENABLED) {
            memoryAccessor = new MemoryAccessor(sim.getAgentInfo(0));
        }
    }
    
    private void createLayout() {
        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        
        buttonsPanel.add(roundCounterLabel, c);
        buttonsPanel.add(startButton, c);
        buttonsPanel.add(nextRoundButton, c);
        buttonsPanel.add(resetButton, c);
        buttonsPanel.add(visibilityCheckBox, c);
        buttonsPanel.add(idsCheckBox, c);
        buttonsPanel.add(stateCheckBox, c);
        buttonsPanel.add(historyPanel, c);
        contentPane.add(drawablePanel, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.EAST);
        if (Main.MEMORY_ACCESSOR_ENABLED) {
            contentPane.add(memoryAccessor, BorderLayout.SOUTH);
        }
        
        setContentPane(contentPane);
    }
    
    /**
     * Updates the positions of Agent JLabels
     */
    private void repositionAgentLabels() {
        Dimension d = drawablePanel.getSize();
        
        Point centre = new Point(d.width / 2, d.height / 2);
        int radius = Math.min(d.width, d.height) * 3 / 4 / 2;
        double alpha = Math.PI * 2 / sim.getNumberOfAgents();
        int size = (int) (radius * Math.sin(alpha / 2));
        
        int[] flags = null;
        if (historyPanel.isHistoryEnabled()) {
            flags = sim.getRoundFlags(historyPanel.getRound());
        }
        
        for (int i = 0; i < sim.getNumberOfAgents(); i++) {
            int x = (int) (centre.x - radius * Math.cos(i * alpha));
            int y = (int) (centre.y - radius * Math.sin(i * alpha));
            
            if (historyPanel.isHistoryEnabled()) {
                agentLabels[i].setFlag(flags[i]);
            } else {
                agentLabels[i].setFlag(sim.getAgentInfo(i).agent.getFlag());
            }
            agentLabels[i].setSize(size);
            agentLabels[i].setPosition(x - size / 2, y - size / 2);
            agentLabels[i].setIdVisible(idsCheckBox.isSelected());
        }
    }
    
    /**
     * Draws connections between agents, their states, etc.
     */
    private void draw(Graphics g) {
        Rectangle rect = g.getClipBounds();
        
        Point centre = new Point(rect.width / 2, rect.height / 2);
        int radius = Math.min(rect.width, rect.height) * 3 / 4 / 2;
        double alpha = Math.PI * 2 / sim.getNumberOfAgents();
        int size = (int) (radius * Math.sin(alpha / 2));
                
        if (visibilityCheckBox.isSelected()) {
            g.setColor(Color.black);
            for (int i = 0; i < sim.getNumberOfAgents(); i++) {
                AgentInfo agentInfo = sim.getAgentInfo(i);
                for (int j = 0; j < agentInfo.getVisibleAgents().length; j++) {
                    AgentInfo agentInfo2 = agentInfo.getVisibleAgents()[j];
                    int x1 = (int) (centre.x - radius * Math.cos(i * alpha));
                    int y1 = (int) (centre.y - radius * Math.sin(i * alpha));
                    int x2 = (int) (centre.x - radius * Math.cos(agentInfo2.id * alpha));
                    int y2 = (int) (centre.y - radius * Math.sin(agentInfo2.id * alpha));
                    g.drawLine(x1 - size / 2, y1 - size / 2, x2 - size / 2, y2 - size / 2);
                }
            }
        }
        
        if (stateCheckBox.isSelected()) {
            Graphics2D g2d = (Graphics2D) g;
            for (int i = 0; i < sim.getNumberOfAgents(); i++) {
                int flag = historyPanel.isHistoryEnabled()? 
                        sim.getRoundFlags(historyPanel.getRound())[i]
                        : sim.getAgentInfo(i).agent.getFlag();
                if ((i + flag) % 2 == 0) {
                    g2d.setColor(Color.green);
                } else {
                    g2d.setColor(Color.yellow);
                }
                g2d.setStroke(new BasicStroke(5));

                int x1 = (int) (centre.x - (radius + size) * Math.cos(i * alpha - alpha / 2));
                int y1 = (int) (centre.y - (radius + size) * Math.sin(i * alpha - alpha / 2));
                int x2 = (int) (centre.x - (radius + size) * Math.cos(i * alpha + alpha / 2));
                int y2 = (int) (centre.y - (radius + size) * Math.sin(i * alpha + alpha / 2));

                g2d.drawLine(x1 - size / 2, y1 - size / 2, x2 - size / 2, y2 - size / 2);
            }
        }
    }

    /* //TODO separate history panel
     * - as separate class
     * - as separate window/draw panel (copy/reuse code)
     * - only read from simulation, does not influence original draw panel
     * - gets update from main window if simulation changes (proceeds to next round)
     */
    private class HistoryPanel extends JPanel {

        private JCheckBox enabled;
        private JLabel roundLabel;
        private JLabel roundNumber;
        private JButton previous;
        private JButton next;
        private JButton first;
        private JButton last;

        public HistoryPanel() {
            super(new GridBagLayout());

            setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                    "History", TitledBorder.CENTER, TitledBorder.TOP));

            enabled = new JCheckBox("Enabled:", false);
            enabled.setHorizontalTextPosition(JCheckBox.LEFT);
            enabled.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    roundLabel.setEnabled(enabled.isSelected());
                    roundNumber.setEnabled(enabled.isSelected());
                    previous.setEnabled(enabled.isSelected());
                    next.setEnabled(enabled.isSelected());
                    first.setEnabled(enabled.isSelected());
                    last.setEnabled(enabled.isSelected());
                    repositionAgentLabels();
                    drawablePanel.repaint();
                    if (Main.MEMORY_ACCESSOR_ENABLED) {
                        memoryAccessor.setEnabled(!enabled.isSelected());
                    }
                }
            });

            roundLabel = new JLabel("Round: ");
            roundLabel.setEnabled(false);
            roundLabel.setHorizontalAlignment(JLabel.RIGHT);

            roundNumber = new JLabel("0");
            roundNumber.setEnabled(false);

            previous = new JButton("Previous");
            previous.setEnabled(false);
            previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = Integer.parseInt(roundNumber.getText());
                    if (i <= 0) {
                        Toolkit.getDefaultToolkit().beep();
                        roundNumber.setText("0");
                    } else {
                        roundNumber.setText("" + (i - 1));
                        repositionAgentLabels();
                        drawablePanel.repaint();
                    }
                }
            });

            next = new JButton("Next");
            next.setEnabled(false);
            next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int i = Integer.parseInt(roundNumber.getText());
                    if (i >= sim.getRoundNumber() - 1) {
                        Toolkit.getDefaultToolkit().beep();
                        roundNumber.setText("" + (sim.getRoundNumber() - 1));
                    } else {
                        roundNumber.setText("" + (i + 1));
                        repositionAgentLabels();
                        drawablePanel.repaint();
                    }
                }
            });
            
            first = new JButton("First");
            first.setEnabled(false);
            first.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    roundNumber.setText("0");
                    repositionAgentLabels();
                    drawablePanel.repaint();
                }
            });
            
            last = new JButton("Last");
            last.setEnabled(false);
            last.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    roundNumber.setText("" + (sim.getRoundNumber() - 1));
                    repositionAgentLabels();
                    drawablePanel.repaint();
                }
            });

            createLayout();
        }

        private void createLayout() {
            GridBagConstraints c = new GridBagConstraints();
            c.gridy = GridBagConstraints.RELATIVE;

            c.gridx = 0;
            c.gridwidth = 2;
            add(enabled, c);

            c.fill = GridBagConstraints.BOTH;
            c.gridwidth = 1;
            add(roundLabel, c);
            c.gridx = 1;
            add(roundNumber, c);

            c.insets = new Insets(3, 0, 0, 0);
            c.gridx = 0;
            add(previous, c);
            c.gridx = 1;
            add(next, c);
            
            c.insets = new Insets(0, 0, 0, 0);
            c.gridx = 0;
            add(first, c);
            c.gridx = 1;
            add(last, c);
        }

        public boolean isHistoryEnabled() {
            return enabled.isSelected();
        }

        public int getRound() {
            return Integer.parseInt(roundNumber.getText());
        }

        private void reset() {
            roundNumber.setText("0");
        }

    }
    
}
