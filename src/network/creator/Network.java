package network.creator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jaroslaw Pawlak
 */
public class Network implements Iterable<Node> {
    
    private List<Node> nodeList = new LinkedList<Node>();
    private List<List<Integer>> adjacencyList = new LinkedList<List<Integer>>();
    
    public void addNode(double x, double y) {
        if (x < 0 || x > 1 || y < 0 || y > 1) {
            throw new IllegalArgumentException("x and y have to be in range [0, 1]");
        }
        
        Node n = new Node(nodeList.size(), x, y);
        nodeList.add(n);
        adjacencyList.add(new ArrayList<Integer>());
    }
    
    public void removeNode(int id) {
        if (id < 0 || id >= nodeList.size()) {
            throw new IllegalArgumentException("no such id");
        }
        
        nodeList.remove(id);
        adjacencyList.remove(id);
        
        for (List<Integer> list : adjacencyList) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == id) {
                    list.remove(i);
                    break;
                }
            }
        }
        
        //decrease all other ids larger than 'id' by one
        for (Node n : nodeList) {
            if (n.id > id) {
                n.id = n.id - 1;
            }
        }
        for (List<Integer> list : adjacencyList) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) > id) {
                    //arraylist results in redundant shifts - but should be fine
                    int old = list.remove(i);
                    list.add(i, old - 1);
                }
            }
        }
        
    }
    
    public void connectNodes(int id1, int id2) {
        if (id1 < 0 || id1 >= adjacencyList.size()) {
            throw new IllegalArgumentException("Cannot connect nodes " + id1
                    + " and " + id2 + ". " + id1 + " is not valid id.");
        }
        if (id2 < 0 || id2 >= adjacencyList.size()) {
            throw new IllegalArgumentException("Cannot connect nodes " + id1
                    + " and " + id2 + ". " + id2 + " is not valid id.");
        }
        if (id1 == id2) {
            throw new IllegalArgumentException("Cannot connect a node "
                    + "to itself! id = " + id1);
        }
        
        insertSorted(adjacencyList.get(id1), id2);
        insertSorted(adjacencyList.get(id2), id1);
    }
    
    public void disconnectNodes(int id1, int id2) {
        if (id1 < 0 || id1 >= adjacencyList.size()
                || id2 < 0 || id2 >= adjacencyList.size()
                || id1 == id2) {
            throw new IllegalArgumentException("Cannot disconnect these nodes!");
        }
        
        adjacencyList.get(id1).remove((Integer) id2);
        adjacencyList.get(id2).remove((Integer) id1);
    }
    
    private void insertSorted(List<Integer> list, int value) {
        if (list.isEmpty()) {
            list.add(value);
            return;
        }
        if (list.get(0) > value) {
            list.add(0, value);
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) > value) {
                list.add(i - 1, value);
                return;
            }
        }
        list.add(value);
    }

    @Override
    public String toString() {
        if (nodeList.size() != adjacencyList.size()) {
            throw new RuntimeException("Ups! Something is wrong!");
        }
        
        String result = "";
        
        for (int i = 0; i < nodeList.size(); i++) {
            Node n = nodeList.get(i);
            if (n.id != i) {
                throw new RuntimeException("IDs do not match!");
            }
            result += i + " (" + n.x + "; " + n.y + "): ";
            for (int j : adjacencyList.get(i)) {
                result += j + ", ";
            }
            result += "\n";
        }
        
        return result;
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeList.iterator();
    }
    
    public Node getNode(int id) {
        Node n = nodeList.get(id);
        return new Node(n.id, n.x, n.y);
    }
    
    public Integer[] adjacentTo(int id) {
        if (id < 0 || id >= adjacencyList.size()) {
            throw new IllegalArgumentException("Incorrect ID!");
        }
        return adjacencyList.get(id).toArray(new Integer[] {});
    }
    
    public int nodesNumber() {
        return nodeList.size();
    }
    
    public boolean isConnected(int id1, int id2) {
        return adjacencyList.get(Math.min(id1, id2)).contains(Math.max(id1, id2));
    }
    
    public void moveNode(int id, double x, double y) {
        if (id < 0 || id > nodeList.size()) {
            throw new IllegalArgumentException("Invalid ID!");
        }
        
        nodeList.get(id).x = x;
        nodeList.get(id).x = y;
    }
    
    
    
    public static Network generateGrid(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Values must be positive");
        }
        
        Network n = new Network();
        
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                
                n.addNode((i + 1.0d) / (width + 1), (j + 1.0d) / (height + 1));
                
                int current = j * width + i;
                if (i > 0) {
                    n.connectNodes(current, current - 1);
                }
                if (j > 0) {
                    n.connectNodes(current, current - width);
                }
                
            }
        }
        
        return n;
    }
    
    /**
     * Odd height recommended.
     */
    public static Network generateHex(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Values must be positive");
        }
        
        Network n = new Network();
        
        int current = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((j & 1) == 0 && i == 0) {
                    continue;
                }
                if ((j & 1) == 0) {
                    n.addNode((i + 0.375d) / (width + 1), (j + 1.0d) / (height + 1));
                } else {
                    n.addNode((i + 0.875d) / (width + 1), (j + 1.0d) / (height + 1));
                }

                if (((j & 1) == 1 && i > 0) || i > 1) {
                    n.connectNodes(current, current - 1);
                }
                if (j > 0) {
                    if ((j & 1) == 0) {
                        n.connectNodes(current, current - width);
                        n.connectNodes(current, current - width + 1);
                    } else {
                        if (i < width - 1) {
                            n.connectNodes(current, current - width + 1);
                        }
                        if (i > 0) {
                            n.connectNodes(current, current - width);
                        }
                    }
                }
                
                current++;
            }
        }
        
        return n;
    }
    
    public static Network generateRing(int nodes) {
        if (nodes <= 0) {
            throw new IllegalArgumentException("Value must be positive");
        }
        
        double radius = 0.4d;
        double alpha = Math.PI * 2 / nodes;
        
        Network n = new Network();
        n.addNode(0.1d, 0.5d);
        
        for (int i = 1; i < nodes; i++) {
            n.addNode(0.5d - radius * Math.cos(i * alpha),
                      0.5d - radius * Math.sin(i * alpha));
            n.connectNodes(i, i - 1);
        }
        
        n.connectNodes(0, nodes - 1);
        
        return n;
    }
    
    public static Network generateFullyConnectedMesh(int nodes) {
        if (nodes <= 0) {
            throw new IllegalArgumentException("Value must be positive");
        }
        
        double radius = 0.4d;
        double alpha = Math.PI * 2 / nodes;
        
        Network n = new Network();
        
        for (int i = 0; i < nodes; i++) {
            n.addNode(0.5d - radius * Math.cos(i * alpha),
                      0.5d - radius * Math.sin(i * alpha));
        }
        
        for (int i = 0; i < nodes; i++) {
            for (int j = 0; j < i; j++) {
                n.connectNodes(i, j);
            }
        }
        
        return n;
    }
    
    public static Network generateFullTree(int children, int levels) {
        if (children <= 0 || levels <= 0) {
            throw new IllegalArgumentException("Values must be positive");
        }
        
        Network n = new Network();
        
        int current = 0;
        for (int i = 0; i < levels; i++) {
            for (int j = 0; j < pow(children, i); j++) {
                n.addNode((j + 1.0d) / (pow(children, i) + 1), (i + 1.0d) / (levels + 1));
                if (i > 0) {
                    n.connectNodes(current, (current - 1) / children);
                }
                current++;
            }
        }
        
        return n;
    }
    
    private static int pow(int base, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be positive, "
                    + "given = " + exponent);
        }
        if (base <= 0) {
            throw new IllegalArgumentException("Base must be non-negative, "
                    + "given = " + base);
        }
        if (exponent == 0) {
            return 1;
        } else if (exponent == 1) {
            return base;
        } else {
            return ((exponent & 1) == 0? 1 : base)
                    * pow(base * base, exponent / 2);
        }
    }
}
