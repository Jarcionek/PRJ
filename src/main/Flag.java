package main;

import java.awt.Color;

/**
 * @author Jaroslaw Pawlak
 */
public abstract class Flag {
    
    private static final Color[] COLOR = {
        new Color(255, 127, 127), // red
        new Color(127, 127, 255), // blue
    };
    
    public static final int COUNT = COLOR.length;
    
    private Flag() {}
    
    public static Color getColor(int i) {
        if (i < 0 || i >= COUNT) {
            throw new IllegalArgumentException("flag out of range: "
                    + "i = " + i + ", max = " + COUNT);
        }
        return COLOR[i];
    }
    
}