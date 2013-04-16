package network.GUI;

import java.awt.Color;

/**
 * @author Jaroslaw Pawlak
 */
public interface Constants {
    
    /**
     * Oval sizes (diameter) of nodes drawn in network.
     */
    static final int D = 20;
    
    /**
     * Minimum number of flags available to choose when creating the simulation.
     */
    static final int MIN_FLAG = 2;
    
    /**
     * Maximum number of flags available to choose when creating the simulation.
     * Keep in mind that GraphPainter has to provide at least that many colour
     * definitions.
     */
    static final int MAX_FLAG = 10;
    
    /**
     * Current selection highlighting colour.
     */
    static final Color SELECTION = Color.red;
    
    /**
     * Infected agents highlighting colour.
     */
    static final Color INFECTED = Color.blue;
    
    /**
     * This colour is used to draw points where network edges intersect.
     */
    static final Color INTERSECTIONS = Color.red.darker();
    
    /**
     * This colour is used to draw a path where mouse was dragged in network
     * creator when advanced node moving is disabled.
     */
    static final Color PATH = Color.blue.darker();
}
