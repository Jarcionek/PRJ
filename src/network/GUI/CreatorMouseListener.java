package network.GUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import network.creator.Node;

/**
 * @author Jaroslaw Pawlak
 */
class CreatorMouseListener implements MouseListener, MouseMotionListener {
    
    private final MainWindow window;
    private final JPanel drawPane;

    private int lastX;
    private int lastY;
    
    public CreatorMouseListener(MainWindow window, JPanel drawPane) {
        this.window = window;
        this.drawPane = drawPane;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() < 2) {
            return;
        }
        
        Dimension size = drawPane.getSize();
        int x = e.getX();
        int y = e.getY();

        Node n = Util.findClosestNode(window.network, size, x, y);

        // add new node
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (n == null) {
                x = Util.fixRange(x, size.width, C.S / 2);
                y = Util.fixRange(y, size.height, C.S / 2);
                double dx = (double) x / size.width;
                double dy = (double) y / size.height;
                window.network.addNode(dx, dy);
                window.nodeColor = null;
                window.updateTitle(true);
                window.repaint();
            }

        // delete node
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (n != null) {
                if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) > 0) {
                    window.network.removeNodeKeepConnections(n.id());
                } else {
                    window.network.removeNode(n.id());
                }
                window.nodeColor = null;
                window.selectedNode = null;
                window.updateTitle(true);
                window.repaint();
            }

        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        lastX = e.getX();
        lastY = e.getY();
        Node newSelection = Util.findClosestNode(window.network,
                                                      drawPane.getSize(), x, y);

        // just deselect
        if (newSelection == null) {
            if (window.selectedNode != null) {
                window.selectedNode = null;
                window.repaint();
            }

        // select new (if new is different than currect)
        } else {
            if (!newSelection.equals(window.selectedNode)) {
                window.selectedNode = newSelection;
                window.repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // connect/disconnect
        if (e.getButton() == MouseEvent.BUTTON1) {
            Node n = Util.findClosestNode(window.network, drawPane.getSize(),
                                                                          x, y);
            
            if (n != null && !n.equals(window.selectedNode)
                                               && window.selectedNode != null) {
                if (window.network.isConnected(window.selectedNode.id(), n.id())) {
                    window.network.disconnectNodes(window.selectedNode.id(), n.id());
                } else {
                    window.network.connectNodes(window.selectedNode.id(), n.id());
                }
                window.nodeColor = null;
                window.updateTitle(true);
            }

        // move node
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (window.selectedNode != null && !window.isAdvancedMovingEnabled()) {
                moveNode(x, y);
            }
        }

        window.repaint(); // to remove drawn line
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (window.selectedNode == null) {
            return;
        }

        int mask = MouseEvent.BUTTON1_DOWN_MASK
                        | MouseEvent.BUTTON3_DOWN_MASK;

        Color color = null;
        if ((e.getModifiersEx() & mask) == MouseEvent.BUTTON1_DOWN_MASK) {
            color = Color.black;
        } else if ((e.getModifiersEx() & mask)
                                            == MouseEvent.BUTTON3_DOWN_MASK) {
            if (window.isAdvancedMovingEnabled()) {
                int x = e.getX();
                int y = e.getY();
                moveNode(x, y);
                window.repaint();
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
            if (window.isAntiAliasingEnabled()) {
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
            }

            // drawing
            g2d.setColor(color);
            g2d.drawLine(e.getX(), e.getY(), lastX, lastY);
            lastX = e.getX();
            lastY = e.getY();

            drawPane.getGraphics().drawImage(image, 0, 0, null);
        }
    }
    
    /**
     * Position (after shift) on drawPane.
     */
    private void moveNode(int x, int y) {
        Dimension size = drawPane.getSize();
        x = Util.fixRange(x, size.width, C.S / 2);
        y = Util.fixRange(y, size.height, C.S / 2);
        
        int id = window.selectedNode.id();
        double dx = (double) x / size.width;
        double dy = (double) y / size.height;
        
        window.selectedNode = new Node(id, dx, dy);
        window.network.moveNode(id, dx, dy);
        window.updateTitle(true);
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}