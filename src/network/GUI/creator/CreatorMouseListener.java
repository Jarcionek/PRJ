package network.GUI.creator;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import network.creator.Node;

/**
 * @author Jaroslaw Pawlak
 */
class CreatorMouseListener extends MouseAdapter {
    
    private final MainWindow window;
    private final JPanel drawPane;

    private int lastX;
    private int lastY;
    
    CreatorMouseListener(MainWindow window, JPanel drawPane) {
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

        Node n = window.network.findClosestNode(size, x, y, C.S);

        // add new node
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (n == null) {
                x = fixRange(x, size.width, C.S / 2);
                y = fixRange(y, size.height, C.S / 2);
                if (window.network.findClosestNode(size, x, y, C.S) == null) {
                    double dx = (double) x / size.width;
                    double dy = (double) y / size.height;
                    window.network.addNode(dx, dy);
                    window.nodeColor = null;
                    window.modified = true;
                    window.updateTitle();
                    window.repaint();
                }
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
                window.selectionId = -1;
                window.modified = true;
                window.updateTitle();
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
        Node n = window.network.findClosestNode(drawPane.getSize(), x, y, C.S);

        // just deselect
        if (n == null) {
            if (window.selectionId != -1) {
                window.selectionId = -1;
                window.repaint();
            }

        // select new (if new is different than currect)
        } else {
            if (n.id() != window.selectionId) {
                window.selectionId = n.id();
                window.repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        
        if (window.selectionId == -1) {
            window.repaint(); // to remove drawn line
            return;
        }

        // connect/disconnect
        if (e.getButton() == MouseEvent.BUTTON1) {
            Node n = window.network.findClosestNode(drawPane.getSize(), x, y, C.S);

            // create new node and connect to selected to it
            if (n == null) {
                Dimension size = drawPane.getSize();
                x = fixRange(x, size.width, C.S / 2);
                y = fixRange(y, size.height, C.S / 2);
                n = window.network.findClosestNode(drawPane.getSize(), x, y, C.S);
                
                // check whether there is no node at the position of a new one
                // the previous check was for the mouse click position
                if (n == null) {
                    double dx = (double) x / size.width;
                    double dy = (double) y / size.height;
                    window.network.addNode(dx, dy);
                    int id = window.network.getNumberOfNodes() - 1;
                    window.network.connectNodes(window.selectionId, id);
                    window.selectionId = id;
                    window.nodeColor = null;
                    window.modified = true;
                    window.updateTitle();
                }
                
            //(dis)connect nodes
            } else {
                if (n.id() != window.selectionId) {
                    if (window.network.isConnected(window.selectionId, n.id())) {
                        window.network.disconnectNodes(window.selectionId, n.id());
                    } else {
                        window.network.connectNodes(window.selectionId, n.id());
                    }
                    window.selectionId = n.id();
                    window.nodeColor = null;
                    window.modified = true;
                    window.updateTitle();
                }
            }
            
        // move node
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (!window.isAdvancedMovingEnabled()) {
                moveNode(x, y);
            }
        }

        window.repaint(); // to remove drawn line
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (window.selectionId == -1) {
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
     * Moves selected node to the given (x, y) position on a draw pane.
     */
    private void moveNode(int x, int y) {
        Dimension size = drawPane.getSize();
        x = fixRange(x, size.width, C.S / 2);
        y = fixRange(y, size.height, C.S / 2);
        
        double dx = (double) x / size.width;
        double dy = (double) y / size.height;
        
        window.network.moveNode(window.selectionId, dx, dy);
        window.modified = true;
        window.updateTitle();
    }

    /**
     * Returns the closest integer value in range [margin; size - margin].
     * If margin > size / 2, this method's behaviour is unknown, no exception
     * will be thrown.
     * @param value value to check
     * @param size range
     * @param margin margin
     * @return the closest integer to 'value' in [margin; size - margin]
     */
    private static int fixRange(int value, int size, int margin) {
        return Math.max(margin, Math.min(value, size - margin));
    }
}