package network.GUI.creator;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import network.GUI.Constants;
import network.creator.Node;
import network.graphUtil.Edge;
import network.painter.GraphPainter;

/**
 * @author Jaroslaw Pawlak
 */
class CreatorDrawablePanel extends JPanel implements Constants {

    private final MainWindow window;
    private final CreatorMenuBar creator;
    
    CreatorDrawablePanel(MainWindow window, CreatorMenuBar creator) {
        super(null);
        this.window = window;
        this.creator = creator;
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        if (creator.isColoringEnabled()) {
            // paint the graph if feature enabled but not yet painted
            if (window.nodeColor == null) {
                window.nodeColor = GraphPainter.paint(window.network);
            }
            
            // if more colors required than defined
            int maxFlag = -1;
            for (int i : window.nodeColor) {
                if (i > maxFlag) {
                    maxFlag = i;
                }
            }
            if (maxFlag >= GraphPainter.getNumberOfDefinedColors()) {
                creator.setColoringEnabled(false);
                Toolkit.getDefaultToolkit().beep();
            }
        }

        // get drawing area size
        int w = this.getBounds().width;
        int h = this.getBounds().height;

        // create image
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // anti-aliasing
        if (creator.isAntiAliasingEnabled()) {
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
        }
        // white background
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, w, h);

        // draw edges
        g2d.setColor(Color.black);
        for (Edge e : window.network.getEdges(w, h)) {
            g2d.drawLine(e.x1(), e.y1(), e.x2(), e.y2());
        }

        // draw nodes and their labels
        g2d.setFont(FONT);
        FontMetrics fm = this.getFontMetrics(g2d.getFont());
        for (Node n : window.network) {
            if (creator.isColoringEnabled()) {
                g2d.setColor(GraphPainter.getColor(window.nodeColor[n.id()]));
            } else {
                g2d.setColor(Color.green); 
            }
            g2d.fillOval((int) (n.x() * w) - D / 2,
                         (int) (n.y() * h) - D / 2,
                         D, D);
            g2d.setColor(Color.black);
            String id = "" + n.id();
            g2d.drawString(id, (int) (n.x() * w) - fm.stringWidth(id) / 2,
                               (int) (n.y() * h) + g2d.getFont().getSize() / 2 - 1);
        }

        // highlight selection
        if (window.selectionId != -1) {
            Point p = window.network.getPosition(window.selectionId, w, h);
            g2d.setColor(SELECTION);
            g2d.drawOval(p.x - D/2, p.y - D/2, D, D);
        }

        // show edges intersections
        if (creator.isIntersectionsHighlightingEnabled()) {
            int s = 7;
            g2d.setColor(INTERSECTIONS);
            for (Edge e1 : window.network.getEdges(w, h)) {
                for (Edge e2 : window.network.getEdges(w, h)) {
                    if (!e1.equals(e2) && !Edge.arePolygonalChain(e1, e2)) {
                        Point p = Edge.intersect(e1, e2);
                        if (p != null) {
                            g2d.fillOval(p.x - s/2, p.y - s/2, s, s);
                        }
                    }
                }
            }
        }

        g.drawImage(image, 0, 0, null);
    }
}
