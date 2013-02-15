package network.GUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import network.creator.Network;

/**
 * @author Jaroslaw Pawlak
 */
public class MainWindow extends JFrame {
    
    static final String TITLE = "Network Creator";

    // main functionality
    Network network = new Network();
    int selectionId = -1;
    private Mode mode = Mode.CREATOR;
    
    // creator
    private final CreatorMenuBar creator;
    private final CreatorDrawablePanel creatorDrawPanel;
    int[] nodeColor = null; // graph painter

    public MainWindow() {
        super();

        creator = new CreatorMenuBar(this);
        creatorDrawPanel = new CreatorDrawablePanel(this, creator);
        
        this.setJMenuBar(creator);
        
        updateTitle();
        
        this.setContentPane(creatorDrawPanel);
        this.setSize(800, 600);
        this.setVisible(true);
        
        CreatorMouseListener mlistener = new CreatorMouseListener(this, creatorDrawPanel);
        creatorDrawPanel.addMouseListener(mlistener);
        creatorDrawPanel.addMouseMotionListener(mlistener);
        
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
    
}
