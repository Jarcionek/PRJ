package network.creator;

/**
 * @author Jaroslaw Pawlak
 */
public class Node {
    
    int id;
    double x;
    double y;

    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int id() {
        return id;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }
    
    @Override
    public String toString() {
        return "" + id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == Node.class
                && ((Node) obj).id == this.id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this.id;
        return hash;
    }
}
