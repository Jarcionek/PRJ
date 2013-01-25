package network.creator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * @author Jaroslaw Pawlak
 */
public class GUI extends JFrame {
        
    private static final int S = 20; // oval sizes
    private static final int X_CHANGE = -8;
    private static final int Y_CHANGE = -28;

    private final Network network;
    
    private Node selectedNode = null;
    
    private DrawablePanel drawPane;
    
    public GUI(Network network)  {
        super("Network Creator");
        this.network = network;
        
        MListener mlistener = new MListener();
        this.addMouseListener(mlistener);
        
        drawPane = new DrawablePanel();
        this.setContentPane(drawPane);
        this.setSize(800, 600);
        this.setVisible(true);
        
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
    
    
    
    private class MListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() >= 2) {
                Dimension size = drawPane.getSize();
                int x = e.getX() + X_CHANGE;
                int y = e.getY() + Y_CHANGE;
                
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
            final int x = e.getX() + X_CHANGE;
            final int y = e.getY() + Y_CHANGE;
                
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

                    menu.show(drawPane, x, y);
                }
            }
            
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int x = e.getX() + X_CHANGE;
            int y = e.getY() + Y_CHANGE;
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
