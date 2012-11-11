package main;

import agents.AgentInfo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * @author Jaroslaw Pawlak
 */
public class SimulationGUI extends JFrame {
    
    private static final DecimalFormat DF = new DecimalFormat("#,###");

    private static int counter = 1;
    
    private CircleSimulation sim;
    
    private JPanel drawablePanel;
    private JLabel roundCounterLabel;
    private JButton startButton;
    private JButton nextRoundButton;
    private JButton resetButton;
    private JCheckBox visibilityCheckBox;
    private JCheckBox idsCheckBox;
    private JCheckBox stateCheckBox;
    
    private AgentLabel[] agentLabels;
    
    public SimulationGUI(CircleSimulation sim) {
        super("SimulationGUI " + counter++);
        
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
                    JMenu setFlagMenu = new JMenu("set flag");
                    setFlagMenu.addMenuListener(ml);
                    for (int j = 0; j < Flag.COUNT; j++) {
                        final int fj = j;
                        JMenuItem menuItem = new JMenuItem("" + j);
                        menuItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                sim.getAgentInfo(fi).setFlag(fj);
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
        
        roundCounterLabel = new JLabel("1");
        
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
                            roundCounterLabel.setText(DF.format(sim.getRound()));
                        }
                        drawablePanel.repaint();
                        Toolkit.getDefaultToolkit().beep();
                        startButton.setEnabled(true);
                        nextRoundButton.setEnabled(true);
                        resetButton.setEnabled(true);
                    }
                }.start();
            }
        });
        
        nextRoundButton = new JButton("Next round");
        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!sim.isConsensus()) {
                    sim.nextRound();
                    drawablePanel.repaint();
                    roundCounterLabel.setText(DF.format(sim.getRound()));
                }
                if (sim.isConsensus()) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        
        resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sim = sim.getNew();
                drawablePanel.repaint();
                roundCounterLabel.setText(DF.format(sim.getRound()));
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
        contentPane.add(drawablePanel, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.EAST);
        
        setContentPane(contentPane);
    }
    
    private void repositionAgentLabels() {
        Dimension d = drawablePanel.getSize();
        
        Point centre = new Point(d.width / 2, d.height / 2);
        int radius = Math.min(d.width, d.height) * 3 / 4 / 2;
        double alpha = Math.PI * 2 / sim.getNumberOfAgents();
        int size = (int) (radius * Math.sin(alpha / 2));
        
        for (int i = 0; i < sim.getNumberOfAgents(); i++) {
            int x = (int) (centre.x - radius * Math.cos(i * alpha));
            int y = (int) (centre.y - radius * Math.sin(i * alpha));
            
            agentLabels[i].setFlag(sim.getAgentInfo(i).getFlag());
            agentLabels[i].setSize(size);
            agentLabels[i].setPosition(x - size / 2, y - size / 2);
            agentLabels[i].setIdVisible(idsCheckBox.isSelected());
        }
    }
    
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
                AgentInfo agentInfo = sim.getAgentInfo(i);
                if ((i + agentInfo.getFlag()) % 2 == 0) {
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

}
