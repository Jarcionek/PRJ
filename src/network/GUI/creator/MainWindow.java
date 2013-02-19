package network.GUI.creator;

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
    
    // creator
    private final CreatorMenuBar creator;
    private final CreatorDrawablePanel creatorDrawPanel;
    int[] nodeColor = null; // graph painter
    
    boolean modified = false;

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
                creator.exit();
            }
        });
    }
    
    final void updateTitle() {
        String title = creator.getTitle();
        if (modified) {
            title += "*";
        }
        this.setTitle(title);
    }
    
    boolean isAntiAliasingEnabled() {
        return creator.isAntiAliasingEnabled();
    }
    
    boolean isAdvancedMovingEnabled() {
        return creator.isAdvancedMovingEnabled();
    }
    
}
