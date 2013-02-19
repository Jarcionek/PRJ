package network.GUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import network.creator.Node;
import network.graphUtil.Edge;
import network.painter.GraphPainter;
import network.simulation.Simulation;
import network.simulation.agents.RandomAgent;

/**
 * @author Jaroslaw Pawlak
 */
class SimulationDrawablePane extends JPanel {
    
    private final MainWindow window;
    
    private Simulation simulation;
    
    // buffers
    private Dimension lastSize;
    private BufferedImage edges;
    private BufferedImage labels;
    
    SimulationDrawablePane(MainWindow window) {
        super(null);
        this.window = window;
        
        simulation = new Simulation(window.network, RandomAgent.class);
        simulation.nextRound();
        setOpaque(false);
    }
    
    void createNewPane() {
        simulation = new Simulation(window.network, RandomAgent.class);
        simulation.nextRound();
        lastSize = null;
    }
    
    @Override
    public void paint(Graphics g) {
        Dimension size = this.getBounds().getSize();
        int w = size.width;
        int h = size.height;
        
        // buffer
        if (!size.equals(lastSize)) {
            Graphics2D g2d;
            // edges
            edges = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            g2d = edges.createGraphics();
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.black);
            for (Edge e : window.network.getEdges(w, h)) {
                g2d.drawLine(e.x1(), e.y1(), e.x2(), e.y2());
            }
            
            // labels
            labels = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            g2d = labels.createGraphics();
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.black);
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = this.getFontMetrics(g2d.getFont());
            for (Node n : window.network) {
                String id = "" + n.id();
                g2d.drawString(id, (int) (n.x() * w) - fm.stringWidth(id) / 2,
                               (int) (n.y() * h) + g2d.getFont().getSize() / 2);
            }
            
            lastSize = size;
        }
        
        // colors
        BufferedImage flags = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flags.createGraphics();
        for (Node n : window.network) {
            g2d.setColor(GraphPainter.getColor(simulation.getFlag(n.id())));
            g2d.fillOval((int) (n.x() * w) - C.S / 2,
                         (int) (n.y() * h) - C.S / 2,
                         C.S, C.S);
        }
        
        // finalise
        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.drawImage(edges, 0, 0, null);
        g.drawImage(flags, 0, 0, null);
        g.drawImage(labels, 0, 0, null);
    }
}
