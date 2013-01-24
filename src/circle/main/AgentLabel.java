package circle.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

/**
 * @author Jaroslaw Pawlak
 */
public class AgentLabel extends JLabel {
    
    private static final String FONT_NAME = "Lucida Console";
    private static final int FONT_STYLE = Font.BOLD;
    private static final int BORDER = 2;
    
    private final int id;
    
    public AgentLabel(int id) {
        super("" + id);
        
        this.id = id;
        
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER)
                ));
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setOpaque(true);
        
        setSize(25);
        setBackground(Color.white);
    }
    
    public final void setFlag(int i) {
        this.setBackground(Flag.getColor(i));
    }
    
    public final void setSize(int size) {
        this.setFont(new Font(FONT_NAME, FONT_STYLE, size / 2));
        this.setPreferredSize(new Dimension(size, size));
    }
    
    public final void setPosition(int x, int y) {
        int w = this.getPreferredSize().width;
        int h = this.getPreferredSize().height;
        this.setBounds(x - w / 2, y - h / 2, w, h);
    }
    
    public final void setIdVisible(boolean showID) {
        if (showID) {
            setText("" + id);
        } else {
            setText("");
        }
    }
}
