package network.creator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * @author Jaroslaw Pawlak
 */
public class GUI extends JFrame {
        
    private static final int S = 20; // oval sizes
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
                JMenu menuGenerate = new JMenu("Generate");
                    JMenuItem menuItemRing = new JMenuItem("Ring");
                    JMenuItem menuItemGrid = new JMenuItem("Grid");
                    JMenuItem menuItemHex = new JMenuItem("Hex");
                    JMenuItem menuItemTree = new JMenuItem("Tree");
                    JMenuItem menuItemAllToAll = new JMenuItem("All-to-all");
                JMenuItem menuItemPrint = new JMenuItem("Print to console");
//TODO add mouse behaviour (what do double clicks do?) + extra deleteNode operation that keeps connections
//            JMenu menuOptions = new JMenu("Options");
//                JMenu menuMouseBehaviour = new JMenu("Mouse behaviour");
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
                                "There have to be at least two nodes", 
                                "Create Ring Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Ring Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                network = Network.generateRing(v1);
                GUI.this.repaint();
            }
        });
        
        menuItemGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(GUI.this, "Width:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Width has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(GUI.this, "Height:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Height has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                network = Network.generateGrid(v1, v2);
                GUI.this.repaint();
            }
        });
        
        menuItemHex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(GUI.this, "Width:",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Width has to be positive", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(GUI.this,
                        "Height (odd recommended):",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Height has to be positive", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
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
                GUI.this.repaint();
            }
        });
        
        menuItemTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(GUI.this, "Children:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Children has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(GUI.this,
                        "Levels:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "Levels has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                network = Network.generateFullTree(v1, v2);
                GUI.this.repaint();
            }
        });
        
        menuItemAllToAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String v = JOptionPane.showInputDialog(GUI.this, "Nodes number:",
                        "Create All-to-all Network", JOptionPane.PLAIN_MESSAGE);
                
                int v1;
                try {
                    v1 = Integer.parseInt(v);
                    if (v1 < 2) {
                        JOptionPane.showMessageDialog(GUI.this,
                                "There have to be at least two nodes", 
                                "Create All-to-all Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.this, "Not a number", 
                            "Create All-to-all Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    //TODO JOptionPanes do weird things with drawPanel, should repaint here? (and all other places like this one)
                    return;
                }
                
                network = Network.generateFullyConnectedMesh(v1);
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
                //TODO about/help - remove what is printed on graphics
            }
        });
        menuItemHelp.setEnabled(false); //TODO temporary until behaviour is implemented
        
        menuBar.add(menuNetwork);
            menuNetwork.add(menuGenerate);
                menuGenerate.add(menuItemRing);
                menuGenerate.add(menuItemGrid);
                menuGenerate.add(menuItemHex);
                menuGenerate.add(menuItemTree);
                menuGenerate.add(menuItemAllToAll);
            menuNetwork.add(menuItemPrint);
        menuAbout.add(menuItemHelp);
            menuBar.add(menuAbout);
        
        return menuBar;
    }
    
    
    
    private class MListener implements MouseListener {

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
                    }

                    double dx = (double) x / size.width;
                    double dy = (double) y / size.height;
                    network.addNode(dx, dy);
                    
                // delete node
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (n != null) {
                        network.removeNode(n.id);
                        if (n.equals(selectedNode)) {
                            selectedNode = null;
                        }
                    }
                }
                
                GUI.this.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            final int x = e.getX() + x_change;
            final int y = e.getY() + y_change;
                
            if (e.getButton() == MouseEvent.BUTTON1) {

                //select
                Node newSelecetion = findClosestNode(x, y);

                // just deselect
                if (newSelecetion == null && selectedNode != null) {
                    selectedNode = null;
                    GUI.this.repaint();

                // select new (if new is different than currect)
                } else if (newSelecetion != null && !newSelecetion.equals(selectedNode)) {
                    selectedNode = newSelecetion;
                    GUI.this.repaint();
                }
                
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (e.getClickCount() == 1) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                            GUI.this.repaint();
                        }
                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {}
                    });

                    JMenuItem moveMI = new JMenuItem("move selected here");
                    moveMI.setEnabled(selectedNode != null);
                    moveMI.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Dimension size = drawPane.getSize();
                            network.moveNode(selectedNode.id, x, y);
                            selectedNode.x = (double) x / size.width;
                            selectedNode.y = (double) y / size.height;
                            GUI.this.repaint();
                        }
                    });
                    menu.add(moveMI);

                    menu.show(drawPane, x + 1, y + 1); //a small fix that will be removed
                }
            }
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int x = e.getX() + x_change;
            int y = e.getY() + y_change;
            Node n = findClosestNode(x, y);
            if (n != null && !n.equals(selectedNode) && selectedNode != null) {
                if (network.isConnected(n.id, selectedNode.id)) {
                    network.disconnectNodes(selectedNode.id, n.id);
                } else {
                    network.connectNodes(selectedNode.id, n.id);
                }
                selectedNode = null;
                GUI.this.repaint();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    private class DrawablePanel extends JPanel {

        public DrawablePanel() {
            super(null);
            setOpaque(false);
        }
        
        @Override
        public void paint(Graphics g) {
            Rectangle size = g.getClipBounds();
            g.setColor(Color.white);
            g.fillRect(size.x, size.y, size.width, size.height);
            
            g.setFont(new Font("Arial", Font.BOLD, 13));
            
            { //HELP
                g.setColor(Color.lightGray);
                g.drawString("create node - double left click", 5, 15);
                g.drawString("delete node - double right click", 5, 30);
                g.drawString("select node - left click", 5, 45);
                g.drawString("(dis)connect nodes - drag from one to other", 5, 60);
                g.drawString("move node - first select, "
                        + "then right click at destination", 5, 75);
            }
            
            int w = size.width;
            int h = size.height;
            
            g.setColor(Color.black);
            for (int i = 0; i < network.nodesNumber(); i++) {
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
