package network.graphUtil;

import java.awt.Point;

/**
 * @author Jaroslaw Pawlak
 */
public class Edge {
    
    private final Point p1;
    private final Point p2;

    public Edge(int x1, int y1, int x2, int y2) {
        this.p1 = new Point(x1, y1);
        this.p2 = new Point(x2, y2);
    }
    
    public int x1() {
        return p1.x;
    }
    
    public int y1() {
        return p1.y;
    }
    
    public int x2() {
        return p2.x;
    }
    
    public int y2() {
        return p2.y;
    }
    
    public Point p1() {
        return p1;
    }
    
    public Point p2() {
        return p2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }
        Edge that = (Edge) obj;
        return (this.p1.equals(that.p1) && this.p2.equals(that.p2))
            || (this.p1.equals(that.p2) && this.p2.equals(that.p1));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.p1 != null ? this.p1.hashCode() : 0);
        hash = 89 * hash + (this.p2 != null ? this.p2.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[p1=" + p1 + ",p2=" + p2 + "]";
    }
    
////// PUBLIC STATIC ///////////////////////////////////////////////////////////
    
    public static boolean arePolygonalChain(Edge e1, Edge e2) {
        return e1.p1.equals(e2.p1) || e1.p1.equals(e2.p2)
            || e1.p2.equals(e2.p1) || e1.p2.equals(e2.p2);
    }
    
    /**
     * Returns a point where two edges intersect or null if they do not.
     * 
     * This method returns null if edges overlay and does not use any
     * approximations.
     */
    public static Point intersect(Edge e1, Edge e2) {
        // if both vertical
        if (isVertical(e1) && isVertical(e2)) {
            return null; // ignore overlay
        }
        
        // if one vertical, make it e1
        if (isVertical(e2)) {
            Edge t = e2;
            e2 = e1;
            e1 = t;
        }
        
        // if one vertical
        if (isVertical(e1)) {
            return intersectVertical(e2, e1);
        }
        
        // e1: y = ax + b
        double a = a(e1);
        double b = b(e1);
        
        // e2: y = cx + d
        double c = a(e2);
        double d = b(e2);
        
        // are parallel
        if (Math.abs(c - a) < 0.00001) {
            return null;
        }
        
        // intersection
        double x = (b - d) / (c - a);
        double y = (b * c - a * d) / (c - a);
        
        // check whether intersection point is on both line segments
        if (!isBetween(x, e1.p1.x, e1.p2.x)) return null;
        if (!isBetween(x, e2.p1.x, e2.p2.x)) return null;
        if (!isBetween(y, e1.p1.y, e1.p2.y)) return null;
        if (!isBetween(y, e2.p1.y, e2.p2.y)) return null;
        
        return new Point(Math.round((float) x), Math.round((float) y));
    }
    
//// PRIVATE STATIC ////////////////////////////////////////////////////////////
    
    private static Point intersectVertical(Edge e, Edge v) {
        if (!isVertical(v)) {
            throw new IllegalArgumentException("v is not vertical!");
        }
        if (isVertical(e)) {
            throw new IllegalArgumentException("e is vertical!");
        }
        
        // intersection
        double x = v.p1.x;
        double y = e.p1.y * (x - e.p2.x) + e.p2.y * (e.p1.x - x);
        y /= e.p1.x - e.p2.x;
        
        // check whether intersection point is on both line segments
        if (!isBetween(y, v.p1.y, v.p2.y)) return null;
        if (!isBetween(y, e.p1.y, e.p2.y)) return null;
        if (!isBetween(x, e.p1.x, e.p2.x)) return null;
        
        return new Point(Math.round((float) x), Math.round((float) y));
    }
    
    private static boolean isVertical(Edge e) {
        return e.p1.x == e.p2.x;
    }
    
    private static double a(Edge e) {
        return (double) (e.p2.y - e.p1.y) / (e.p2.x - e.p1.x);
    }
    
    private static double b(Edge e) {
        return e.p1.y - e.p1.x * a(e);
    }
    
    private static boolean isBetween(double value, int limit1, int limit2) {
        final double epsilon = 0.00001;
        
        if (limit1 > limit2) {
            int t = limit1;
            limit1 = limit2;
            limit2 = t;
        }
        
        // without epsilon it was: limit1 <= value && value <= limit2;
        return limit1 - epsilon < value && value < limit2 + epsilon;
    }

}
