package network.creator;

/**
 * @author Jaroslaw Pawlak
 */
public class NetworkStats {

    public final int nodes;
    public final int edges;
    public final int degreeMin;
    public final int degreeMax;
    public final double degreeMean;
    public final int degreeMedian;
    public final int degreeMode;

    public NetworkStats(int nodes, int edges, int degreeMin,
            int degreeMax, double degreeMean, int degreeMedian, int degreeMode) {
        this.nodes = nodes;
        this.edges = edges;
        this.degreeMin = degreeMin;
        this.degreeMax = degreeMax;
        this.degreeMean = degreeMean;
        this.degreeMedian = degreeMedian;
        this.degreeMode = degreeMode;
    }
    
}
