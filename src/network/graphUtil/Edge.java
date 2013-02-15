package network.graphUtil;

import java.awt.Point;

/**
 * @author Jaroslaw Pawlak
 */
public class Edge {
    
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;

    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass())
                && ((Edge) obj).x1 == this.x1 && ((Edge) obj).y1 == this.y1
                && ((Edge) obj).x2 == this.x2 && ((Edge) obj).y2 == this.y2;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + this.x1;
        hash = 29 * hash + this.y1;
        hash = 29 * hash + this.x2;
        hash = 29 * hash + this.y2;
        return hash;
    }
    
////// STATIC //////////////////////////////////////////////////////////////////
    
    /**
     * @see intersect(Point, Point, Point, Point)
     */
    public static Point intersect(Edge e1, Edge e2) {
        Point L1P1 = new Point(e1.x1, e1.y1);
        Point L1P2 = new Point(e1.x2, e1.y2);
        Point L2P1 = new Point(e2.x1, e2.y1);
        Point L2P2 = new Point(e2.x2, e2.y2);
        return intersect(L1P1, L1P2, L2P1, L2P2);
    }
    
    /**
     * Checks whether two line segments intersect. May be slightly inaccurate
     * due to double rounding error. Returns the point of intersection or null
     * if the line segments do not intersect.
     * 
     * If line segments overlay, returns the middle of the common line segment,
     * with some approximation.
     * 
     * If one point is common (e.g. in polygon), returns null.
     * 
     * @param L1P1 line 1, point 1
     * @param L1P2 line 1, point 2
     * @param L2P1 line 2, point 1
     * @param L2P2 line 2, point 1
     * @return coordinates of intersection or null if line segments
     *         do not intersect
     */
    public static Point intersect(Point L1P1, Point L1P2, Point L2P1, Point L2P2) {
        // if both lines are vertical
        if (L1P1.x == L1P2.x && L2P1.x == L2P2.x) {
            // if they overlay
            if (L1P1.x == L2P1.x) {
                int y = overlayedMiddleY(L1P1, L1P2, L2P1, L2P2);
                if (y == Integer.MIN_VALUE) {
                    return null;
                } else {
                    return new Point(L1P1.x, y);
                }
            } else {
                return null;
            }
        }
        
        // if line 1 is vertical
        if (L1P1.x == L1P2.x) {
            return intersectVertical(L1P1.x, L1P1.y, L1P2.y, L2P1, L2P2);
        }
        
        // if line 2 is vertical
        if (L2P1.x == L2P2.x) {
            return intersectVertical(L2P1.x, L2P1.y, L2P2.y, L1P1, L1P2);
        }
        
        // line 1: y = ax + b
        double a = (double) (L1P2.y - L1P1.y) / (L1P2.x - L1P1.x);
        double b = (double) L1P1.y - L1P1.x * a;
        
        // line 2: y = cx + d
        double c = (double) (L2P2.y - L2P1.y) / (L2P2.x - L2P1.x);
        double d = (double) L2P1.y - L2P1.x * c;
        
        // if lines overlay or are parallel (approximation!)
        if (Math.abs(c - a) < 0.01) { // tg(alpha)
            // overlay
            if (Math.abs(b - d) < 5) { // vertical shift in pixels
                int y = overlayedMiddleY(L1P1, L1P2, L2P1, L2P2);
                if (y == Integer.MIN_VALUE) {
                    return null;
                } else {
                    if (a == 0) {
                        return new Point(L1P1.x, y);
                    } else {
                        double x = (y - b) / a;
                        return new Point(Math.round((float) x), y);
                    }
                }
            // parallel
            } else {
                return null;
            }
        }
        
        // if lines have common point (but they don't overlay)
        if (L1P1.equals(L2P1) || L1P1.equals(L2P2)
                || L1P2.equals(L2P1) || L1P2.equals(L2P2)) {
            return null;
        }
        
        // intersection
        double x = (b - d) / (c - a);
        double y = (b * c - a * d) / (c - a);
        
        // check whether intersection point is on both line segments
        if (!isInRange(x, L1P1.x, L1P2.x)) return null;
        if (!isInRange(x, L2P1.x, L2P2.x)) return null;
        if (!isInRange(y, L1P1.y, L1P2.y)) return null;
        if (!isInRange(y, L2P1.y, L2P2.y)) return null;
        
        return new Point(Math.round((float) x), Math.round((float) y));
    }
    
    private static boolean isInRange(double value, int min, int max) {
        if (min > max) {
            int t = min;
            min = max;
            max = t;
        }
        return min <= value && value <= max;
    }
    
    private static Point intersectVertical(int x, int ymin, int ymax,
                                           Point p1, Point p2) {
        
        double y = p1.y * (x - p2.x) + p2.y * (p1.x - x);
        y /= p1.x - p2.x;
        
        if (isInRange(y, ymin, ymax)) {
            return new Point(x, Math.round((float) y));
        } else {
            return null;
        }
    }
    
    private static int overlayedMiddleY(Point L1P1, Point L1P2,
                                       Point L2P1, Point L2P2) {
        if (L1P1.y > L1P2.y) {
            int t = L1P1.y;
            L1P1.y = L1P2.y;
            L1P2.y = t;
        }
        if (L2P1.y > L2P2.y) {
            int t = L2P1.y;
            L2P1.y = L2P2.y;
            L2P2.y = t;
        }
        
        // L1 entirely in L2
        if (L2P1.y <= L1P1.y && L1P2.y <= L2P2.y) {
            return L1P1.y + (L1P2.y - L1P1.y) / 2;
        }
        
        // L2 entirely in L1
        if (L1P1.y <= L2P1.y && L2P2.y <= L1P2.y) {
            return L2P1.y + (L2P2.y - L2P1.y) / 2;
        }
        
        // L1-y-max on L2, but L1-y-min not
        if (L2P1.y <= L1P2.y && L1P2.y <= L2P2.y) {
            return L2P1.y + (L1P2.y - L2P1.y) / 2;
        }
        
        // L1-y-min on L2, but L1-y-max not
        if (L2P1.y <= L1P1.y && L1P1.y <= L2P2.y) {
            return L1P1.y + (L2P2.y - L1P1.y) / 2;
        }
        
        return Integer.MIN_VALUE;
    }
}
