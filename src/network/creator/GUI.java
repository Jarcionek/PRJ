package network.creator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * @author Jaroslaw Pawlak
 */
public class GUI extends JFrame {
    
    private final int S = 20; // oval sizes
    private final int x_change;
    private final int y_change;

    private Network network;
    
    private Node selectedNode = null;
    
    private DrawablePanel drawPane;
    
    public GUI(Network network)  {
        super("Network Creator");
        this.network = network;
        
        MListener mlistener = new MListener();
        this.addMouseListener(mlistener);
        this.addMouseMotionListener(mlistener);
        
        this.setJMenuBar(createMenuBar());
        
        drawPane = new DrawablePanel();
        this.setContentPane(drawPane);
        this.setSize(800, 600);
        this.setVisible(true);
        
        Point p1 = getContentPane().getLocationOnScreen();
        Point p2 = this.getLocationOnScreen();
        x_change = -(p1.x - p2.x);
        y_change = -(p1.y - p2.y);
        
        //TODO replace with window listener? what to do on close? ask to save?
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    private Node findClosestNode(int x, int y) {
        Dimension size = drawPane.getSize();
        
        Node best = null;
        double bestDist = Double.MAX_VALUE;
        
        for (Node n : network) {
            int nx = (int) (n.x * size.width);
            int ny = (int) (n.y * size.height);
            if (Math.abs(nx - x) < S && Math.abs(ny - y) < S) {
                double newDist = (nx-x)*(nx-x) + (ny-y)*(ny-y);
                if (newDist < bestDist) {
                    bestDist = newDist;
                    best = n;
                }
            }
        }
        
        return best;
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
            JMenu menuNetwork = new JMenu("Network");
                JMenuItem menuItemNew = new JMenuItem("New");
                JMenu menuGenerate = new JMenu("Generate");
                    JMenuItem menuItemRing = new JMenuItem("Ring");
                    JMenuItem menuItemStar = new JMenuItem("Star");
                    JMenuItem menuItemGrid = new JMenuItem("Grid");
                    JMenuItem menuItemHex = new JMenuItem("Hex");
                    JMenuItem menuItemTree = new JMenuItem("Tree");
                    JMenuItem menuItemAllToAll = new JMenuItem("All-to-all");
                JMenuItem menuItemStats = new JMenuItem("Show statistics");
                JMenuItem menuItemPrint = new JMenuItem("Print to console");
            JMenu menuAbout = new JMenu("About");
                JMenuItem menuItemHelp = new JMenuItem("Help");
        
        MenuListener menuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                GUI.this.repaint();
            }
            @Override
            public void menuDeselected(MenuEvent e) {
                GUI.this.repaint();
            }
            @Override
            public void menuCanceled(MenuEvent e) {
                GUI.this.repaint();
            }
        };
        menuNetwork.addMenuListener(menuListener);
        menuGenerate.addMenuListener(menuListener);
        menuAbout.addMenuListener(menuListener);

        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                network = new Network();
                selectedNode = null;
                GUI.this.repaint();
            }
        });
        
        menuItemRing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String v = JOptionPane.showInputDialog(GUI.this, "Nodes number:",
                        "Create Ring Network", JOptionPane.PLAIN_MESSAGE);
                
                int v1;
                try {
                    v1 = Integer.parseInt(v);
                    if (v1 < 3) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "There have to be at least three nodes", 
                                "Create Ring Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Ring Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                network = Network.generateRing(v1);
                selectedNode = null;
                GUI.this.repaint();
            }
        });

        menuItemStar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(GUI.this, "Nodes number:",
                        "Create Star Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "There have to be at least one node", 
                                "Create Star Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Star Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                network = Network.generateStar(v1);
                selectedNode = null;
                GUI.this.repaint();
            }
        });
        
        menuItemGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(GUI.this, "Width:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Width has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(GUI.this, "Height:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Height has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                network = Network.generateGrid(v1, v2);
                selectedNode = null;
                GUI.this.repaint();
            }
        });
        
        menuItemHex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(GUI.this, "Width:",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Width has to be positive", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(GUI.this,
                        "Height (odd recommended):",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Height has to be positive", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                int c = JOptionPane.showConfirmDialog(GUI.this,
                        "Extra connections on sides?",
                        "Create Hex Network", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                
                boolean more;
                if (c == JOptionPane.YES_OPTION) {
                    more = true;
                } else if (c == JOptionPane.NO_OPTION) {
                    more = false;
                } else {
                    return;
                }
                
                network = Network.generateHex(v1, v2, more);
                selectedNode = null;
                GUI.this.repaint();
            }
        });
        
        menuItemTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(GUI.this, "Children:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Children has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(GUI.this,
                        "Levels:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Levels has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                network = Network.generateFullTree(v1, v2);
                selectedNode = null;
                GUI.this.repaint();
            }
        });
        
        menuItemAllToAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(GUI.this, "Nodes number:",
                        "Create All-to-all Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s == null) {
                    GUI.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s);
                    if (v1 < 2) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "There have to be at least two nodes", 
                                "Create All-to-all Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        GUI.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create All-to-all Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    GUI.this.repaint();
                    return;
                }
                
                network = Network.generateFullyConnectedMesh(v1);
                selectedNode = null;
                GUI.this.repaint();
            }
        });
        
        menuItemStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NetworkStats ns = network.getStatistics();
                JTextArea ta = new JTextArea(
                          "nodes:         " + ns.nodes + "\n"
                        + "edges:         " + ns.edges + "\n"
                        + "min degree:    " + ns.degreeMin + "\n"
                        + "max degree:    " + ns.degreeMax + "\n"
                        + "degree mode:   " + ns.degreeMode + "\n"
                        + "degree mean:   " + ((int) (100 * ns.degreeMean)) / 100d + "\n"
                        + "degree median: " + ns.degreeMedian + "\n"
                );
                ta.setFont(new Font("Lucida Console", Font.PLAIN, 14));
                ta.setEditable(false);
                ta.setBackground(new JPanel().getBackground());
            
                JOptionPane.showMessageDialog(GUI.this, ta, "Help", JOptionPane.PLAIN_MESSAGE);
                GUI.this.repaint();
            }
        });
        
        menuItemPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(network.toStringAdjacencyOnly());
            }
        });
        
        menuItemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea ta = new JTextArea(
                          "CREATE NODE\n"
                        + "Double click with left mouse button.\n"
                        + "\n"
                        + "DELETE NODE\n"
                        + "Double click with right mouse button.\n"
                        + "\n"
                        + "DELETE NODE - KEEP CONNECTIONS\n"
                        + "Deletes node, however if any two nodes A and B\n"
                        + "were connected via deleted node, after deletion\n"
                        + "nodes A and B will be connected directly.\n"
                        + "\n"
                        + "(DIS)CONNECT TWO NODES\n"
                        + "Drag from one node to another while\n"
                        + "keeping left mouse button down.\n"
                        + "\n"
                        + "MOVE NODE\n"
                        + "Press with right mouse button on the node\n"
                        + "and keeping the button down move to the\n"
                        + "new destination."
                );
                ta.setEditable(false);
                ta.setBackground(new JPanel().getBackground());
            
                JOptionPane.showMessageDialog(GUI.this, ta, "Help", JOptionPane.PLAIN_MESSAGE);
                GUI.this.repaint();
            }
        });
        
        menuBar.add(menuNetwork);
            menuNetwork.add(menuItemNew);
            menuNetwork.add(menuGenerate);
                menuGenerate.add(menuItemRing);
                menuGenerate.add(menuItemStar);
                menuGenerate.add(menuItemGrid);
                menuGenerate.add(menuItemHex);
                menuGenerate.add(menuItemTree);
                menuGenerate.add(menuItemAllToAll);
            menuNetwork.add(menuItemStats);
            menuNetwork.add(menuItemPrint);
        menuAbout.add(menuItemHelp);
            menuBar.add(menuAbout);
        
        return menuBar;
    }
    
    
    
    private class MListener implements MouseListener, MouseMotionListener {
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                Dimension size = drawPane.getSize();
                int x = e.getX() + x_change;
                int y = e.getY() + y_change;
                
                Node n = findClosestNode(x, y);
                
                // add new node
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (n != null) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    } else {
                        double dx = (double) x / size.width;
                        double dy = (double) y / size.height;
                        network.addNode(dx, dy);
                    }
                    
                // delete node
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (n == null) {
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    } else {
                        if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) > 0) {
                            network.removeNodeKeepConnections(n.id);
                        } else {
                            network.removeNode(n.id);
                        }
                        selectedNode = null;
                    }
                    
                }
                
                GUI.this.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            final int x = e.getX() + x_change;
            final int y = e.getY() + y_change;
            Node newSelecetion = findClosestNode(x, y);

            // just deselect
            if (newSelecetion == null) {
                if (selectedNode != null) {
                    selectedNode = null;
                    GUI.this.repaint();
                }

            // select new (if new is different than currect)
            } else {
                if (!newSelecetion.equals(selectedNode)) {
                    selectedNode = newSelecetion;
                    GUI.this.repaint();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int x = e.getX() + x_change;
            int y = e.getY() + y_change;
            if (e.getButton() == MouseEvent.BUTTON1) {
                Node n = findClosestNode(x, y);
                if (n != null && !n.equals(selectedNode) && selectedNode != null) {
                    if (network.isConnected(n.id, selectedNode.id)) {
                        network.disconnectNodes(selectedNode.id, n.id);
                    } else {
                        network.connectNodes(selectedNode.id, n.id);
                    }
                }
                selectedNode = null;
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (selectedNode != null) {
                    Dimension size = drawPane.getSize();
                    selectedNode.x = (double) x / size.width;
                    selectedNode.y = (double) y / size.height;
                    network.moveNode(selectedNode.id, selectedNode.x, selectedNode.y);
                }
            }
            
            GUI.this.repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedNode != null) {
                int mask = MouseEvent.BUTTON1_DOWN_MASK
                               | MouseEvent.BUTTON3_DOWN_MASK;
                
                if ((e.getModifiersEx() & mask)
                                              == MouseEvent.BUTTON1_DOWN_MASK) {
                    Graphics g = drawPane.getGraphics();
                    g.drawLine(e.getX() + x_change, e.getY() + y_change,
                               e.getX() + x_change, e.getY() + y_change);
                } else if ((e.getModifiersEx() & mask)
                                              == MouseEvent.BUTTON3_DOWN_MASK) {
                    Graphics g = drawPane.getGraphics();
                    g.setColor(Color.red);
                    g.drawLine(e.getX() + x_change, e.getY() + y_change,
                               e.getX() + x_change, e.getY() + y_change);
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}
    }
    
    private class DrawablePanel extends JPanel {

        public DrawablePanel() {
            super(null);
            setOpaque(false);
        }
        
        @Override
        public void paint(Graphics g) {
            Rectangle size = drawPane.getBounds();
            size.y = 0; // because of JMenuBar
            g.setColor(Color.white);
            g.fillRect(size.x, size.y, size.width, size.height);
            
            g.setFont(new Font("Arial", Font.BOLD, 13));
            
            int w = size.width;
            int h = size.height;
            
            g.setColor(Color.black);
            for (int i = 0; i < network.getNumberOfNodes(); i++) {
                int x1 = (int) (network.getNode(i).x * w);
                int y1 = (int) (network.getNode(i).y * h);
                for (Integer j : network.adjacentTo(i)) {
                    int x2 = (int) (network.getNode(j).x * w);
                    int y2 = (int) (network.getNode(j).y * h);
                    g.drawLine(x1, y1, x2, y2);
                }
            }
            
            FontMetrics fm = this.getFontMetrics(g.getFont());
            
            for (Node n : network) {
                g.setColor(Color.green);
                g.fillOval((int) (n.x * w) - S / 2, (int) (n.y * h) - S / 2, S, S);
                g.setColor(Color.black);
                String id = "" + n.id;
                g.drawString(id, (int) (n.x * w) - fm.stringWidth(id) / 2,
                                 (int) (n.y * h) + g.getFont().getSize() / 2);
            }
            
            if (selectedNode != null) {
                int rx = (int) (selectedNode.x * w);
                int ry = (int) (selectedNode.y * h);
                g.setColor(Color.red);
                g.drawOval(rx - S/2, ry - S/2, S, S);
            }
            
            super.paint(g);
        }
    }
}
