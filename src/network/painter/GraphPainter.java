package network.painter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import network.creator.Network;

/**
 * @author Jaroslaw Pawlak
 */
public class GraphPainter {

    private static final Color[] COLOR = new Color[] {
        new Color(255, 127, 127), // red
        new Color(127, 127, 255), // blue
        Color.GREEN,
        Color.YELLOW,
        Color.ORANGE,
        Color.MAGENTA,
        Color.CYAN,
        Color.PINK,
        Color.GRAY,
        Color.LIGHT_GRAY,
    };

    private Network network;
    private List<Integer> notYetPainted;
    private int[] nodeColor;
    
    private GraphPainter(Network network) {
        int size = network.getNumberOfNodes();
        
        this.network = network;
        
        notYetPainted = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            notYetPainted.add(i);
        }
        
        nodeColor = new int[size];
        Arrays.fill(nodeColor, -1);
        
        while (!notYetPainted.isEmpty()) {
            paint(notYetPainted.get(0));
        }
    }
    
    private void paint(int root) {
        nodeColor[root] = 0;
        
        List<Integer> IDsList = new LinkedList<Integer>();
        IDsList.add(root);
        
        while (!IDsList.isEmpty()) {
            int nodeID = IDsList.get(0);
            Integer[] neighboursIDs = network.adjacentTo(nodeID);
            int[] neighboursFlags = new int[neighboursIDs.length];
            for (int i = 0; i < neighboursFlags.length; i++) {
                neighboursFlags[i] = nodeColor[neighboursIDs[i]];
            }
            nodeColor[nodeID] = getSmallestNumberNotInArray(neighboursFlags);
            notYetPainted.remove(new Integer(nodeID));
            IDsList.remove(0);
            for (int i : network.adjacentTo(nodeID)) {
                if (nodeColor[i] == -1 && !IDsList.contains(i)) {
                    IDsList.add(i);
                }
            }
        }
    }
    
    /**
     * Returns the array of colors (as ints) assigned to nodes
     */
    public static int[] paint(Network network) {
        if (network.getNumberOfNodes() == 0) {
            return new int[0];
        } else {
            return new GraphPainter(network).nodeColor;
        }
    }
    
    /**
     * Returns the smallest non-negative number that is not in the array.
     */
    private static int getSmallestNumberNotInArray(int[] array) {
        int max = array.length;
        outer:
        for (int number = 0; number < max; number++) {
            for (int i : array) {
                if (i == number) {
                    continue outer;
                }
            }
            return number;
        }
        return max;
    }
    
    /**
     * Returns the Color object corresponding to the given index, as in
     * predefined array. If i is smaller 0, returns white. If there are not
     * enough color definitions throws an exception.
     */
    public static Color getColor(int i) {
        if (i < 0) {
            return Color.white;
        } else if (i >= COLOR.length) {
            throw new UnsupportedOperationException("There are not enough "
                    + "color definitions! i = " + i + ", array length = "
                    + COLOR.length);
        }
        return COLOR[i];
    }
    
    public static int getNumberOfDefinedColors() {
        return COLOR.length;
    }
}
