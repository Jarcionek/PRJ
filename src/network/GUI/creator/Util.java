package network.GUI.creator;

import java.awt.Dimension;
import network.creator.Network;
import network.creator.Node;

/**
 * @author Jaroslaw Pawlak
 */
class Util {

    private Util() {}
    
    /**
     * @param network network
     * @param size sizes of a drawable panel where the network is drawn
     * @param x position of mouse click on a drawable pane (in pixels)
     * @param y position of mouse click on a drawable pane (in pixels)
     * @return closest node or null if there or null if not found
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
    
    /**
     * Returns the closest integer value in range [margin; size - margin].
     * If margin > size / 2, this method's behaviour is unknown, no exception
     * will be thrown.
     * @param value value to check
     * @param size range
     * @param margin margin
     * @return the closest integer to 'value' in [margin; size - margin]
     */
    static int fixRange(int value, int size, int margin) {
        return Math.max(margin, Math.min(value, size - margin));
    }
}
