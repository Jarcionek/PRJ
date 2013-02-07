package network.GUI;

import java.awt.Dimension;
import network.creator.Network;
import network.creator.Node;

/**
 * @author Jaroslaw Pawlak
 */
public class Util {
    
    /**
     * Mouse click position (relative to the drawPane, i.e. after shift)
     */
    static Node findClosestNode(Network network, Dimension size, int x, int y) {
        Node best = null;
        double bestDist = Double.MAX_VALUE;
        
        for (Node n : network) {
            int nx = (int) (n.x() * size.width);
            int ny = (int) (n.y() * size.height);
            if (Math.abs(nx - x) < C.S && Math.abs(ny - y) < C.S) {
                double newDist = (nx-x)*(nx-x) + (ny-y)*(ny-y);
                if (newDist < bestDist) {
                    bestDist = newDist;
                    best = n;
                }
            }
        }
        
        return best;
    }
}
