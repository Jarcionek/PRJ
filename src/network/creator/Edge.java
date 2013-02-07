package network.creator;

/**
 * This class is not part of network structure. It is used only to iterate over
 * all edges and hence contains integer coordinates defining where the edges
 * should be displayed.
 * 
 * @author Jaroslaw Pawlak
 */
public class Edge {
    
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;

    Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    
}
