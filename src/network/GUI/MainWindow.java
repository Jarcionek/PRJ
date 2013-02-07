package network.GUI;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import network.creator.Network;
import network.creator.NetworkStats;
import network.creator.Node;
import network.painter.GraphPainter;

/**
 * @author Jaroslaw Pawlak
 */
public class MainWindow extends JFrame {
    
    private static final String TITLE = "Network Creator";
    
    // drawing
    private final int S = 20; // oval sizes
    private final int x_change;
    private final int y_change;

    // main functionality
    private Network network = new Network();
    private DrawablePanel drawPane = new DrawablePanel();
    private Node selectedNode = null;
    
    // options
    private JCheckBoxMenuItem menuItemAntiAliasing;
    private JCheckBoxMenuItem menuItemAdvancedMoving;
    
    // grpah painter
    private JCheckBoxMenuItem menuItemColor;
    private int[] nodeColor = null;
    
    // save/load
    private File location = null;
    private boolean modified = false;

    public MainWindow()  {
        super();
        updateTitle(false);
        
        MListener mlistener = new MListener();
        this.addMouseListener(mlistener);
        this.addMouseMotionListener(mlistener);
        
        this.setJMenuBar(createMenuBar());

        this.setContentPane(drawPane);
        this.setSize(800, 600);
        this.setVisible(true);
        
        Point p1 = getContentPane().getLocationOnScreen();
        Point p2 = this.getLocationOnScreen();
        x_change = -(p1.x - p2.x);
        y_change = -(p1.y - p2.y);
        
        ToolTipManager.sharedInstance().setInitialDelay(200);
        
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }
    
    private void updateTitle(boolean modified) {
        this.modified = modified;
        String newTitle = TITLE + ": ";
        if (location == null) {
            newTitle += "unnamed";
        } else {
            if (location.getName().contains(".")) {
                // remove extension
                newTitle += location.getName()
                             .substring(0, location.getName().lastIndexOf('.'));
            } else {
                newTitle += location.getName();
            }
        }
        if (modified) {
            newTitle += "*";
        }
        this.setTitle(newTitle);
    }
    
    private Node findClosestNode(int x, int y) {
        Dimension size = drawPane.getSize();
        
        Node best = null;
        double bestDist = Double.MAX_VALUE;
        
        for (Node n : network) {
            int nx = (int) (n.x() * size.width);
            int ny = (int) (n.y() * size.height);
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
                JMenuItem menuItemSave = new JMenuItem("Save");
                JMenuItem menuItemSaveAs = new JMenuItem("Save as");
                JMenuItem menuItemLoad = new JMenuItem("Load");
                JMenuItem menuItemStats = new JMenuItem("Show statistics");
                JMenuItem menuItemPrint = new JMenuItem("Print to console");
                menuItemColor = new JCheckBoxMenuItem("Color the network");
                JMenuItem menuItemExit = new JMenuItem("Exit");
            JMenu menuOptions = new JMenu("Options");
                menuItemAntiAliasing = new JCheckBoxMenuItem("Anti-aliasing");
                menuItemAdvancedMoving = new JCheckBoxMenuItem("Advanced nodes moving");
            JMenu menuAbout = new JMenu("About");
                JMenuItem menuItemHelp = new JMenuItem("Help");
        
        MenuListener menuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                MainWindow.this.repaint();
            }
            @Override
            public void menuDeselected(MenuEvent e) {
                MainWindow.this.repaint();
            }
            @Override
            public void menuCanceled(MenuEvent e) {
                MainWindow.this.repaint();
            }
        };
        menuNetwork.addMenuListener(menuListener);
        menuGenerate.addMenuListener(menuListener);
        menuOptions.addMenuListener(menuListener);
        menuAbout.addMenuListener(menuListener);

        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                network = new Network();
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(false);
                MainWindow.this.repaint();
            }
        });
        
        menuItemRing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(MainWindow.this, "Nodes number:",
                        "Create Ring Network", JOptionPane.PLAIN_MESSAGE);
                                
                if (s == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v;
                try {
                    v = Integer.parseInt(s);
                    if (v < 3) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "There have to be at least three nodes", 
                                "Create Ring Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Ring Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                network = Network.generateRing(v);
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(true);
                MainWindow.this.repaint();
            }
        });

        menuItemStar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(MainWindow.this, "Nodes number:",
                        "Create Star Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "There have to be at least one node", 
                                "Create Star Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Star Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                network = Network.generateStar(v1);
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(true);
                MainWindow.this.repaint();
            }
        });
        
        menuItemGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(MainWindow.this, "Width:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Width has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(MainWindow.this, "Height:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Height has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                network = Network.generateGrid(v1, v2);
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(true);
                MainWindow.this.repaint();
            }
        });
        
        menuItemHex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(MainWindow.this, "Width:",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 2) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Width has to be at least 2", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(MainWindow.this,
                        "Height (odd recommended):",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Height has to be positive", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                int c = JOptionPane.showConfirmDialog(MainWindow.this,
                        "Extra connections on sides?",
                        "Create Hex Network", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                
                boolean more;
                if (c == JOptionPane.YES_OPTION) {
                    more = true;
                } else if (c == JOptionPane.NO_OPTION) {
                    more = false;
                } else {
                    MainWindow.this.repaint();
                    return;
                }
                
                network = Network.generateHex(v1, v2, more);
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(true);
                MainWindow.this.repaint();
            }
        });
        
        menuItemTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(MainWindow.this, "Children:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Children has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(MainWindow.this,
                        "Levels:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Levels has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                network = Network.generateFullTree(v1, v2);
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(true);
                MainWindow.this.repaint();
            }
        });
        
        menuItemAllToAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(MainWindow.this, "Nodes number:",
                        "Create All-to-all Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s == null) {
                    MainWindow.this.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s);
                    if (v1 < 2) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "There have to be at least two nodes", 
                                "Create All-to-all Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        MainWindow.this.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Not a number", 
                            "Create All-to-all Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    MainWindow.this.repaint();
                    return;
                }
                
                network = Network.generateFullyConnectedMesh(v1);
                nodeColor = null;
                selectedNode = null;
                location = null;
                updateTitle(true);
                MainWindow.this.repaint();
            }
        });
        
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAs();
            }
        });
        
        menuItemLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
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
            
                JOptionPane.showMessageDialog(MainWindow.this, ta, "Help", JOptionPane.PLAIN_MESSAGE);
                MainWindow.this.repaint();
            }
        });
        
        menuItemPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(network.toStringAdjacencyOnly());
            }
        });
        
        menuItemColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuItemColor.isSelected()) {
                    nodeColor = GraphPainter.paint(network);
                    int maxFlag = -1;
                    for (int i : nodeColor) {
                        if (i > maxFlag) {
                            maxFlag = i;
                        }
                    }
                    if (maxFlag >= GraphPainter.getNumberOfDefinedColors()) {
                        menuItemColor.setSelected(false);
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "There are not enough color definitions "
                                + "to color this graph!",
                                "Color the graph - error", JOptionPane.WARNING_MESSAGE);
                    }
                }
                MainWindow.this.repaint();
            }
        });
        
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        
        menuItemAntiAliasing.setSelected(true);
        menuItemAntiAliasing.setToolTipText("Disable to increase performance");
                
        menuItemAdvancedMoving.setSelected(true);
        menuItemAdvancedMoving.setToolTipText("Disable to increase performance");
        
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
            
                JOptionPane.showMessageDialog(MainWindow.this, ta, "Help", JOptionPane.PLAIN_MESSAGE);
                MainWindow.this.repaint();
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
            menuNetwork.add(menuItemSave);
            menuNetwork.add(menuItemSaveAs);
            menuNetwork.add(menuItemLoad);
            menuNetwork.add(new JSeparator());
            menuNetwork.add(menuItemStats);
            menuNetwork.add(menuItemPrint);
            menuNetwork.add(menuItemColor);
            menuNetwork.add(new JSeparator());
            menuNetwork.add(menuItemExit);
        menuBar.add(menuOptions);
            menuOptions.add(menuItemAntiAliasing);
            menuOptions.add(menuItemAdvancedMoving);
        menuBar.add(menuAbout);
            menuAbout.add(menuItemHelp);
        
        return menuBar;
    }
    
    /**
     * Returns true if saved
     */
    private boolean saveAs() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(Network.FILE_FILTER);
        int choice = jfc.showSaveDialog(MainWindow.this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            if (!f.getName().substring(f.getName().lastIndexOf('.') + 1)
                    .equalsIgnoreCase(Network.EXTENSION)) {
                f = new File(f.getPath() + "." + Network.EXTENSION);
            }
            if (network.save(f)) {
                location = f;
                updateTitle(false);
                return true;
            } else {
                JOptionPane.showMessageDialog(MainWindow.this, "Could not save the network!",
                                 TITLE + " - error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Returns true if saved
     */
    private boolean save() {
        if (location != null) {
            if (network.save(location)) {
                updateTitle(false);
                return true;
            } else {
                JOptionPane.showMessageDialog(MainWindow.this, "Could not save the network!",
                                 TITLE + " - error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return saveAs();
    }
    
    private void load() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(Network.FILE_FILTER);
        int choice = jfc.showOpenDialog(MainWindow.this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            Network loaded = Network.load(jfc.getSelectedFile());
            if (loaded == null) {
                JOptionPane.showMessageDialog(MainWindow.this,
                        "Could not load the network!",
                        TITLE + " - error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                network = loaded;
                nodeColor = null;
                selectedNode = null;
                location = jfc.getSelectedFile();
                updateTitle(false);
                MainWindow.this.repaint();
            }
        }
    }
    
    private void exit() {
        if (!modified) {
            MainWindow.this.dispose();
            return;
        }

        int c = JOptionPane.showConfirmDialog(MainWindow.this,
                "Network has been modified, do you want to save it?", TITLE,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (c == JOptionPane.YES_OPTION) {
            if (save()) {
                MainWindow.this.dispose();
            }
        } else if (c == JOptionPane.NO_OPTION) {
            MainWindow.this.dispose();
        }
    }
    
    
    
    
    private class MListener implements MouseListener, MouseMotionListener {
        
        private int lastX;
        private int lastY;
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                Dimension size = drawPane.getSize();
                int x = e.getX() + x_change;
                int y = e.getY() + y_change;
                
                Node n = findClosestNode(x, y);
                
                // add new node
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (n == null) {
                        x = Math.max(S / 2, Math.min(x, size.width - S / 2));
                        y = Math.max(S / 2, Math.min(y, size.height - S / 2));
                        double dx = (double) x / size.width;
                        double dy = (double) y / size.height;
                        network.addNode(dx, dy);
                        nodeColor = null;
                        updateTitle(true);
                        MainWindow.this.repaint();
                    }
                    
                // delete node
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (n != null) {
                        if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) > 0) {
                            network.removeNodeKeepConnections(n.id());
                        } else {
                            network.removeNode(n.id());
                        }
                        nodeColor = null;
                        selectedNode = null;
                        updateTitle(true);
                        MainWindow.this.repaint();
                    }
                    
                }
                
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            final int x = e.getX() + x_change;
            final int y = e.getY() + y_change;
            lastX = e.getX();
            lastY = e.getY();
            Node newSelecetion = findClosestNode(x, y);

            // just deselect
            if (newSelecetion == null) {
                if (selectedNode != null) {
                    selectedNode = null;
                    MainWindow.this.repaint();
                }

            // select new (if new is different than currect)
            } else {
                if (!newSelecetion.equals(selectedNode)) {
                    selectedNode = newSelecetion;
                    MainWindow.this.repaint();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int x = e.getX() + x_change;
            int y = e.getY() + y_change;
            
            // connect/disconnect
            if (e.getButton() == MouseEvent.BUTTON1) {
                Node n = findClosestNode(x, y);
                if (n != null && !n.equals(selectedNode) && selectedNode != null) {
                    if (network.isConnected(selectedNode.id(), n.id())) {
                        network.disconnectNodes(selectedNode.id(), n.id());
                    } else {
                        network.connectNodes(selectedNode.id(), n.id());
                    }
                    nodeColor = null;
                    updateTitle(true);
                }
                
            // move node
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                if (selectedNode != null && !menuItemAdvancedMoving.isSelected()) {
                    //TODO repetition 1
                    Dimension size = drawPane.getSize();
                    x = Math.max(S / 2, Math.min(x, size.width - S / 2));
                    y = Math.max(S / 2, Math.min(y, size.height - S / 2));
                    selectedNode = new Node(selectedNode.id(),
                                            (double) x / size.width,
                                            (double) y / size.height);
                    network.moveNode(selectedNode.id(), selectedNode.x(), selectedNode.y());
                    updateTitle(true);
                }
            }
            
            MainWindow.this.repaint(); // to remove drawn line
        }

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectedNode == null) {
                return;
            }
            
            int mask = MouseEvent.BUTTON1_DOWN_MASK
                            | MouseEvent.BUTTON3_DOWN_MASK;

            Color color = null;
            if ((e.getModifiersEx() & mask) == MouseEvent.BUTTON1_DOWN_MASK) {
                color = Color.black;
            } else if ((e.getModifiersEx() & mask)
                                              == MouseEvent.BUTTON3_DOWN_MASK) {
                if (menuItemAdvancedMoving.isSelected()) {
                    int x = e.getX() + x_change;
                    int y = e.getY() + y_change;
                    //TODO repetition 1
                    Dimension size = drawPane.getSize();
                    x = Math.max(S / 2, Math.min(x, size.width - S / 2));
                    y = Math.max(S / 2, Math.min(y, size.height - S / 2));
                    selectedNode = new Node(selectedNode.id(),
                                            (double) x / size.width,
                                            (double) y / size.height);
                    network.moveNode(selectedNode.id(), selectedNode.x(), selectedNode.y());
                    updateTitle(true);
                    MainWindow.this.repaint();
                } else {
                    color = Color.red;
                }
            }

            if (color != null) {
                BufferedImage image = new BufferedImage(
                                                   drawPane.getBounds().width,
                                                   drawPane.getBounds().height,
                                                   BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();

                // anti-aliasing
                if (menuItemAntiAliasing.isSelected()) {
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);
                }

                // drawing
                g2d.setColor(color);
                g2d.drawLine(e.getX() + x_change, e.getY() + y_change,
                            lastX + x_change,    lastY + y_change);
                lastX = e.getX();
                lastY = e.getY();

                drawPane.getGraphics().drawImage(image, 0, 0, null);
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
            // paint the graph if feature enabled but not yet painted
            if (menuItemColor.isSelected()) {
                if (nodeColor == null) {
                    nodeColor = GraphPainter.paint(network);
                }
                
                // if more colors required than defined
                int maxFlag = -1;
                for (int i : nodeColor) {
                    if (i > maxFlag) {
                        maxFlag = i;
                    }
                }
                if (maxFlag >= GraphPainter.getNumberOfDefinedColors()) {
                    menuItemColor.setSelected(false);
                    Toolkit.getDefaultToolkit().beep();
                }
            }
            
            // get drawing area size
            int w = drawPane.getBounds().width;
            int h = drawPane.getBounds().height;
            
            // create image
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // anti-aliasing
            if (menuItemAntiAliasing.isSelected()) {
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);
            }
            // white background
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, w, h);
            
            // draw edges
            g2d.setColor(Color.black);
            for (int i = 0; i < network.getNumberOfNodes(); i++) {
                int x1 = (int) (network.getNode(i).x() * w);
                int y1 = (int) (network.getNode(i).y() * h);
                for (Integer j : network.adjacentTo(i)) {
                    int x2 = (int) (network.getNode(j).x() * w);
                    int y2 = (int) (network.getNode(j).y() * h);
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
            
            // draw nodes and their labels
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = this.getFontMetrics(g2d.getFont());
            for (Node n : network) {
                if (menuItemColor.isSelected()) {
                    g2d.setColor(GraphPainter.getColor(nodeColor[n.id()]));
                } else {
                    g2d.setColor(Color.green); 
                }
                g2d.fillOval((int) (n.x() * w) - S / 2, (int) (n.y() * h) - S / 2, S, S);
                g2d.setColor(Color.black);
                String id = "" + n.id();
                g2d.drawString(id, (int) (n.x() * w) - fm.stringWidth(id) / 2,
                               (int) (n.y() * h) + g2d.getFont().getSize() / 2);
            }
            
            // highlight selection
            if (selectedNode != null) {
                int rx = (int) (selectedNode.x() * w);
                int ry = (int) (selectedNode.y() * h);
                g2d.setColor(Color.red);
                g2d.drawOval(rx - S/2, ry - S/2, S, S);
            }
            
            super.paint(g2d);
            g.drawImage(image, 0, 0, null);
        }
    }
}
