package network.GUI;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import network.creator.Edge;
import network.creator.Network;
import network.creator.Node;
import network.painter.GraphPainter;

/**
 * @author Jaroslaw Pawlak
 */
public class MainWindow extends JFrame {
    
    static final String TITLE = "Network Creator";

    // main functionality
    private final DrawablePanel drawPane = new DrawablePanel();
    Network network = new Network();
    int selectionId = -1;
    
    // graph painter
    int[] nodeColor = null;
    
    // modes
    private Mode mode = Mode.CREATOR;
    private final CreatorMenuBar creator;

    public MainWindow() {
        super();

        this.setJMenuBar(creator = new CreatorMenuBar(this));
        
        updateTitle();

        this.setContentPane(drawPane);
        this.setSize(800, 600);
        this.setVisible(true);
        
        CreatorMouseListener mlistener = new CreatorMouseListener(this, drawPane);
        drawPane.addMouseListener(mlistener);
        drawPane.addMouseMotionListener(mlistener);
        
        ToolTipManager.sharedInstance().setInitialDelay(200);
        
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                switch (mode) {
                    case CREATOR: creator.exit(); break;
                }
            }
        });
    }
    
    /**
     * To be called whenever a network is modified. This will update
     * CreatorMenuBar 'modified' state and will cause the window
     * to request new window title from CreatorMenuBar.
     */
    void networkModified() {
        creator.networkModified();
        this.updateTitle();
    }
    
    final void updateTitle() {
        switch (mode) {
            case CREATOR: this.setTitle(creator.getTitle()); break;
        }
    }
    
    boolean isAntiAliasingEnabled() {
        return creator.isAntiAliasingEnabled();
    }
    
    boolean isAdvancedMovingEnabled() {
        return creator.isAdvancedMovingEnabled();
    }
    
    //TODO below - to another file, above - stays here
    
    private class DrawablePanel extends JPanel {

        public DrawablePanel() {
            super(null);
            setOpaque(false);
        }
        
        @Override
        public void paint(Graphics g) {
            if (creator.isColoringEnabled()) {
                // paint the graph if feature enabled but not yet painted
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
            if (isAntiAliasingEnabled()) {
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);
            }
            // white background
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, w, h);
            
            // draw edges
            g2d.setColor(Color.black);
            for (Edge e : network.getEdges(w, h)) {
                g2d.drawLine(e.x1, e.y1, e.x2, e.y2);
            }
            
            // draw nodes and their labels
            g2d.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = this.getFontMetrics(g2d.getFont());
            for (Node n : network) {
                if (creator.isColoringEnabled()) {
                    g2d.setColor(GraphPainter.getColor(nodeColor[n.id()]));
                } else {
                    g2d.setColor(Color.green); 
                }
                g2d.fillOval((int) (n.x() * w) - C.S / 2,
                             (int) (n.y() * h) - C.S / 2,
                             C.S, C.S);
                g2d.setColor(Color.black);
                String id = "" + n.id();
                g2d.drawString(id, (int) (n.x() * w) - fm.stringWidth(id) / 2,
                               (int) (n.y() * h) + g2d.getFont().getSize() / 2);
            }
            
            // highlight selection
            if (selectionId != -1) {
                Point p = network.getPosition(selectionId, w, h);
                g2d.setColor(Color.red);
                g2d.drawOval(p.x - C.S/2, p.y - C.S/2, C.S, C.S);
            }
            
            super.paint(g2d);
            g.drawImage(image, 0, 0, null);
        }
    }
}
