package network.GUI.creator;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import network.GUI.simulation.InitialisationWindow;
import network.creator.Network;
import network.creator.NetworkStats;
import network.painter.GraphPainter;

/**
 * @author Jaroslaw Pawlak
 */
class CreatorMenuBar extends JMenuBar {
    
    private final MainWindow window;
    
    // save/load
    private File location = null;
    
    // menu bar
    private JMenu menuNetwork;
        private JMenuItem menuItemNew;
        private JMenu menuGenerate;
            private JMenuItem menuItemRing;
            private JMenuItem menuItemStar;
            private JMenuItem menuItemGrid;
            private JMenuItem menuItemHex;
            private JMenuItem menuItemTree;
            private JMenuItem menuItemAllToAll;
            private JMenuItem menuItemRandom;
        private JMenuItem menuItemSave;
        private JMenuItem menuItemSaveAs;
        private JMenuItem menuItemLoad;
        private JMenuItem menuItemStats;
        private JMenuItem menuItemPrint;
        private JMenuItem menuItemColor;
        private JMenuItem menuItemInterest;
        private JMenuItem menuItemExit;
    private JMenu menuSimulation;
        private JMenuItem menuItemSimulationInit;
    private JMenu menuOptions;
        private JMenuItem menuItemAntiAliasing;
        private JMenuItem menuItemAdvancedMoving;
    private JMenu menuAbout;
        private JMenuItem menuItemHelp;

    /**
     * Constructor.
     * @param window 
     */
    CreatorMenuBar(MainWindow window) {
        this.window = window;
        
        createMenus();
        customiseMenus();
        addMenus();
    }
    
    /**
     * Creates all menus, menu items etc.
     */
    private void createMenus() {
        menuNetwork = new JMenu("Network");
            menuItemNew = new JMenuItem("New");
            menuGenerate = new JMenu("Generate");
                menuItemRing = new JMenuItem("Ring");
                menuItemStar = new JMenuItem("Star");
                menuItemGrid = new JMenuItem("Grid");
                menuItemHex = new JMenuItem("Hex");
                menuItemTree = new JMenuItem("Tree");
                menuItemAllToAll = new JMenuItem("All-to-all");
                menuItemRandom = new JMenuItem("Random");
            menuItemSave = new JMenuItem("Save");
            menuItemSaveAs = new JMenuItem("Save as");
            menuItemLoad = new JMenuItem("Load");
            menuItemStats = new JMenuItem("Show statistics");
            menuItemPrint = new JMenuItem("Print to console");
            menuItemColor = new JCheckBoxMenuItem("Color the network");
            menuItemInterest = new JCheckBoxMenuItem("Show intersections");
            menuItemExit = new JMenuItem("Exit");
        menuSimulation = new JMenu("Simulation");
            menuItemSimulationInit = new JMenuItem("Initialise");
        menuOptions = new JMenu("Options");
            menuItemAntiAliasing = new JCheckBoxMenuItem("Anti-aliasing");
            menuItemAdvancedMoving = new JCheckBoxMenuItem("Advanced nodes moving");
        menuAbout = new JMenu("About");
            menuItemHelp = new JMenuItem("Help");
    }
    
    /**
     * Add all the menus, menu items etc. to 'this' JMenuBar
     */
    private void addMenus() {
        this.add(menuNetwork);
            menuNetwork.add(menuItemNew);
            menuNetwork.add(menuGenerate);
                menuGenerate.add(menuItemRing);
                menuGenerate.add(menuItemStar);
                menuGenerate.add(menuItemGrid);
                menuGenerate.add(menuItemHex);
                menuGenerate.add(menuItemTree);
                menuGenerate.add(menuItemAllToAll);
                menuGenerate.add(menuItemRandom);
            menuNetwork.add(menuItemSave);
            menuNetwork.add(menuItemSaveAs);
            menuNetwork.add(menuItemLoad);
            menuNetwork.add(new JSeparator());
            menuNetwork.add(menuItemStats);
            menuNetwork.add(menuItemPrint);
            menuNetwork.add(menuItemColor);
            menuNetwork.add(menuItemInterest);
            menuNetwork.add(new JSeparator());
            menuNetwork.add(menuItemExit);
        this.add(menuSimulation);
            menuSimulation.add(menuItemSimulationInit);
        this.add(menuOptions);
            menuOptions.add(menuItemAntiAliasing);
            menuOptions.add(menuItemAdvancedMoving);
        this.add(menuAbout);
            menuAbout.add(menuItemHelp);
    }
    
    /**
     * Add actions listeners etc.
     */
    private void customiseMenus() {
        // add this menu listener to each menu to ensure correct repainting
        MenuListener menuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                window.repaint();
            }
            @Override
            public void menuDeselected(MenuEvent e) {
                window.repaint();
            }
            @Override
            public void menuCanceled(MenuEvent e) {
                window.repaint();
            }
        };
        menuNetwork.addMenuListener(menuListener);
        menuGenerate.addMenuListener(menuListener);
        menuSimulation.addMenuListener(menuListener);
        menuOptions.addMenuListener(menuListener);
        menuAbout.addMenuListener(menuListener);
        
        // adding action listeners below

        menuItemNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.network = new Network();
                window.nodeColor = null;
                window.selectionId = -1;
                location = null;
                window.modified = false; // in newNetworkGenerated() it is true
                window.updateTitle();
                window.repaint();
            }
        });
        
        menuItemRing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(window, "Nodes number:",
                        "Create Ring Network", JOptionPane.PLAIN_MESSAGE);
                                
                if (s == null) {
                    window.repaint();
                    return;
                }
                
                int v;
                try {
                    v = Integer.parseInt(s);
                    if (v < 3) {
                        JOptionPane.showMessageDialog(window,
                                "There have to be at least three nodes", 
                                "Create Ring Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Ring Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateRing(v);
                newNetworkGenerated();
            }
        });

        menuItemStar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(window, "Nodes number:",
                        "Create Star Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s == null) {
                    window.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "There have to be at least one node", 
                                "Create Star Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Star Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateStar(v1);
                newNetworkGenerated();
            }
        });
        
        menuItemGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(window, "Width:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    window.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Width has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(window, "Height:",
                        "Create Grid Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    window.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Height has to be positive", 
                                "Create Grid Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Grid Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateGrid(v1, v2);
                newNetworkGenerated();
            }
        });
        
        menuItemHex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(window, "Width:",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    window.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 2) {
                        JOptionPane.showMessageDialog(window,
                                "Width has to be at least 2", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(window,
                        "Height (odd recommended):",
                        "Create Hex Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    window.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Height has to be positive", 
                                "Create Hex Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Hex Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                int c = JOptionPane.showConfirmDialog(window,
                        "Extra connections on sides?",
                        "Create Hex Network", JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                
                boolean more;
                if (c == JOptionPane.YES_OPTION) {
                    more = true;
                } else if (c == JOptionPane.NO_OPTION) {
                    more = false;
                } else {
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateHex(v1, v2, more);
                newNetworkGenerated();
            }
        });
        
        menuItemTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(window, "Children:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    window.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Children has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(window, "Levels:",
                        "Create Tree Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    window.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Levels has to be positive", 
                                "Create Tree Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Tree Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateFullTree(v1, v2);
                newNetworkGenerated();
            }
        });
        
        menuItemAllToAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(window, "Nodes number:",
                        "Create All-to-all Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s == null) {
                    window.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s);
                    if (v1 < 2) {
                        JOptionPane.showMessageDialog(window,
                                "There have to be at least two nodes", 
                                "Create All-to-all Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create All-to-all Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateFullyConnectedMesh(v1);
                newNetworkGenerated();
            }
        });
        
        menuItemRandom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s1 = JOptionPane.showInputDialog(window, "Nodes:",
                        "Create Random Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s1 == null) {
                    window.repaint();
                    return;
                }
                
                int v1;
                try {
                    v1 = Integer.parseInt(s1);
                    if (v1 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Nodes has to be positive", 
                                "Create Random Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Random Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                String s2 = JOptionPane.showInputDialog(window, "Edges:",
                        "Create Random Network", JOptionPane.PLAIN_MESSAGE);
                
                if (s2 == null) {
                    window.repaint();
                    return;
                }
                
                int v2;
                try {
                    v2 = Integer.parseInt(s2);
                    if (v2 < 1) {
                        JOptionPane.showMessageDialog(window,
                                "Edges has to be positive", 
                                "Create Random Network - Error",
                                JOptionPane.ERROR_MESSAGE);
                        window.repaint();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Not a number", 
                            "Create Random Network - Error",
                            JOptionPane.ERROR_MESSAGE);
                    window.repaint();
                    return;
                }
                
                window.network = Network.generateRandom(v1, v2, 0.01d, 1000);
                newNetworkGenerated();
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
                NetworkStats ns = window.network.getStatistics();
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
            
                JOptionPane.showMessageDialog(window, ta, "Network Statistics",
                        JOptionPane.PLAIN_MESSAGE);
                window.repaint();
            }
        });
        
        menuItemPrint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(window.network.toStringAdjacencyOnly());
            }
        });
        
        menuItemColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkColors();
                window.repaint();
            }
        });
        
        menuItemInterest.setSelected(true);
        menuItemInterest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.repaint();
            }
        });
        
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        
        menuItemSimulationInit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (window.network.getNumberOfNodes() == 0) {
                    //TODO or if network is disjoint
                    JOptionPane.showMessageDialog(window, "You should create "
                            + "the network first!", MainWindow.TITLE + " - error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JFrame f = new InitialisationWindow(window.network, getNetworkName());
                    f.setSize(window.getSize());
                    f.setLocationRelativeTo(window);
                }
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
                        + "Ctrl + double right click.\n"
                        + "Deletes node, however for each two nodes A and B\n"
                        + "that were connected via deleted node, after deletion\n"
                        + "nodes A and B will be connected directly.\n"
                        + "\n"
                        + "(DIS)CONNECT TWO NODES\n"
                        + "Drag from one node to another while\n"
                        + "keeping left mouse button down.\n"
                        + "If there is no node at the destination, a new one will\n"
                        + "be created and connected to previously selected.\n"
                        + "\n"
                        + "MOVE NODE\n"
                        + "Press with right mouse button on the node\n"
                        + "and keeping the button down move to the\n"
                        + "new destination."
                );
                ta.setEditable(false);
                ta.setBackground(new JPanel().getBackground());
            
                JOptionPane.showMessageDialog(window, ta, "Help", JOptionPane.PLAIN_MESSAGE);
                window.repaint();
            }
        });
    }
    
    /**
     * Returns true if saved.
     */
    private boolean saveAs() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(Network.FILE_FILTER);
        int choice = jfc.showSaveDialog(window);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            if (!f.getName().substring(f.getName().lastIndexOf('.') + 1)
                    .equalsIgnoreCase(Network.EXTENSION)) {
                f = new File(f.getPath() + "." + Network.EXTENSION);
            }
            if (window.network.save(f)) {
                location = f;
                window.modified = false;
                window.updateTitle();
                return true;
            } else {
                JOptionPane.showMessageDialog(window, "Could not save the "
                        + "network!", MainWindow.TITLE + " - error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }
    
    /**
     * Returns true if saved. If cannot save, due to error or was never saved
     * before, calls saveAs().
     */
    private boolean save() {
        if (location != null) {
            if (window.network.save(location)) {
                window.modified = false;
                window.updateTitle();
                return true;
            } else {
                JOptionPane.showMessageDialog(window, "Could not save the "
                        + "network!", MainWindow.TITLE + " - error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else {
            return saveAs();
        }
    }
    
    /**
     * Loads the network from file - prompts user with JFileChooser.
     */
    private void load() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(Network.FILE_FILTER);
        int choice = jfc.showOpenDialog(window);
        if (choice == JFileChooser.APPROVE_OPTION) {
            Network loaded = Network.load(jfc.getSelectedFile());
            if (loaded == null) {
                JOptionPane.showMessageDialog(window, "Could not load the "
                        + "network!", MainWindow.TITLE + " - error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                window.network = loaded;
                window.nodeColor = null;
                window.selectionId = -1;
                location = jfc.getSelectedFile();
                window.modified = false;
                window.updateTitle();
                window.repaint();
            }
        }
    }
    
    /**
     * Checks whether the network has been modified but not saved.
     * If yes, asks whether to save or not, then disposes the frame.
     * If no, just disposes the frame.
     */
    void exit() {
        if (!window.modified) {
            window.dispose();
            return;
        }

        int c = JOptionPane.showConfirmDialog(window,"Network has been "
                + "modified, do you want to save it?", MainWindow.TITLE,
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (c == JOptionPane.YES_OPTION) {
            if (save()) {
                window.dispose();
            }
        } else if (c == JOptionPane.NO_OPTION) {
            window.dispose();
        }
    }

    /**
     * Returns a network name (without file extension) or "unnamed" if network
     * was never saved.
     */
    String getNetworkName() {
        if (location == null) {
            return "unnamed";
        } else {
            if (location.getName().contains(".")) {
                // remove extension
                return location.getName()
                             .substring(0, location.getName().lastIndexOf('.'));
            } else {
                return location.getName();
            }
        }
    }
    
    private void newNetworkGenerated() {
        window.nodeColor = null;
        window.selectionId = -1;
        location = null;
        window.modified = true;
        checkColors();
        window.updateTitle();
        window.repaint();
    }

    private void checkColors() {
        if (menuItemColor.isSelected()) {
            window.nodeColor = GraphPainter.paint(window.network);
            int maxFlag = -1;
            for (int i : window.nodeColor) {
                if (i > maxFlag) {
                    maxFlag = i;
                }
            }
            if (maxFlag >= GraphPainter.getNumberOfDefinedColors()) {
                menuItemColor.setSelected(false);
                JOptionPane.showMessageDialog(window,
                        "There are not enough color definitions "
                        + "to color this graph!",
                        "Color the graph - error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    boolean isAntiAliasingEnabled() {
        return menuItemAntiAliasing.isSelected();
    }
    
    boolean isAdvancedMovingEnabled() {
        return menuItemAdvancedMoving.isSelected();
    }
    
    boolean isColoringEnabled() {
        return menuItemColor.isSelected();
    }
    
    void setColoringEnabled(boolean enabled) {
        menuItemColor.setSelected(true);
    }
    
    boolean isIntersectionsHighlightingEnabled() {
        return menuItemInterest.isSelected();
    }
}
