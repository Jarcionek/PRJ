package network.GUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
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
    
    // temp
    private JTextField field0;
    private JTextField field1;
    private JTextField field2;
    private JTextField field3;
    // temp end

    public MainWindow() {
        super();

        this.setJMenuBar(creator = new CreatorMenuBar(this));
        
        updateTitle();

        // temp
        JPanel contentPane = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Intersection tests - "
                + "enabled if there are four nodes - "
                + "edges (0, 1) and (2, 3)");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel label0 = new JLabel("0");
        JLabel label1 = new JLabel("1");
        JLabel label2 = new JLabel("2");
        JLabel label3 = new JLabel("3");
        field0 = new JTextField();
        field1 = new JTextField();
        field2 = new JTextField();
        field3 = new JTextField();
        field0.setPreferredSize(new Dimension(50, 20));
        field1.setPreferredSize(new Dimension(50, 20));
        field2.setPreferredSize(new Dimension(50, 20));
        field3.setPreferredSize(new Dimension(50, 20));
        field0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] val = field0.getText().split("\\D+");
                double x = Double.parseDouble(val[0]) / drawPane.getSize().width;
                double y = Double.parseDouble(val[1]) / drawPane.getSize().height;
                network.moveNode(0, x, y);
                MainWindow.this.repaint();
            }
        });
        field1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] val = field1.getText().split("\\D+");
                double x = Double.parseDouble(val[0]) / drawPane.getSize().width;
                double y = Double.parseDouble(val[1]) / drawPane.getSize().height;
                network.moveNode(1, x, y);
                MainWindow.this.repaint();
            }
        });
        field2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] val = field2.getText().split("\\D+");
                double x = Double.parseDouble(val[0]) / drawPane.getSize().width;
                double y = Double.parseDouble(val[1]) / drawPane.getSize().height;
                network.moveNode(2, x, y);
                MainWindow.this.repaint();
            }
        });
        field3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] val = field3.getText().split("\\D+");
                double x = Double.parseDouble(val[0]) / drawPane.getSize().width;
                double y = Double.parseDouble(val[1]) / drawPane.getSize().height;
                network.moveNode(3, x, y);
                MainWindow.this.repaint();
            }
        });
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = GridBagConstraints.RELATIVE;
        c.gridy = 0;
        panel.add(label0, c);
        panel.add(field0, c);
        panel.add(label1, c);
        panel.add(field1, c);
        c.gridy = 1;
        panel.add(label2, c);
        panel.add(field2, c);
        panel.add(label3, c);
        panel.add(field3, c);
        
        contentPane.add(title, BorderLayout.NORTH);
        contentPane.add(drawPane, BorderLayout.CENTER);
        contentPane.add(panel, BorderLayout.SOUTH);
        this.setContentPane(contentPane);
        // temp end
        
//        this.setContentPane(drawPane); // temp
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
            
            // temp
            if (network.getNumberOfNodes() == 4) {
                Dimension size = drawPane.getSize();
                Point p1 = network.getPosition(0, size);
                Point p2 = network.getPosition(1, size);
                Point p3 = network.getPosition(2, size);
                Point p4 = network.getPosition(3, size);
                Point p = Network.intersect(p1, p2, p3, p4);
                if (p != null) {
                    int s = 3;
                    g2d.setColor(Color.magenta);
                    g2d.fillOval(p.x - s/2, p.y - s/2, s, s);
                }
                field0.setText(p1.x + "," + p1.y);
                field1.setText(p2.x + "," + p2.y);
                field2.setText(p3.x + "," + p3.y);
                field3.setText(p4.x + "," + p4.y);
                field0.setEnabled(true);
                field1.setEnabled(true);
                field2.setEnabled(true);
                field3.setEnabled(true);
            } else {
                field0.setEnabled(false);
                field1.setEnabled(false);
                field2.setEnabled(false);
                field3.setEnabled(false);
            }
            // temp end
            
            super.paint(g2d);
            g.drawImage(image, 0, 0, null);
        }
    }
}
