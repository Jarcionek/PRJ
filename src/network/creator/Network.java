package network.creator;

import exceptions.ShouldNeverHappenException;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import javax.swing.filechooser.FileFilter;
import network.graphUtil.Edge;

/**
 * @author Jaroslaw Pawlak
 */
public class Network implements Iterable<Node> {
    
    public static final String EXTENSION = "network";
    public static final FileFilter FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName()
                    .substring(f.getName().lastIndexOf('.') + 1)
                    .equalsIgnoreCase(EXTENSION);
        }

        @Override
        public String getDescription() {
            return "*." + EXTENSION;
        }
    };
    
    private final List<Node> nodeList;
    private final List<List<Integer>> adjacencyList;

    public Network() {
        nodeList = new LinkedList<Node>();
        adjacencyList = new LinkedList<List<Integer>>();
    }
    
////////////////////////////////////////////////////////////////////////////////
////// PUBLIC METHODS //////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////    
    
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
    
    public void removeNodeKeepConnections(int id) {
        Integer[] nodes = adjacencyList.get(id).toArray(new Integer[] {});
        
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < i; j++) {
                if (!isConnected(nodes[i], nodes[j])) {
                    connectNodes(nodes[i], nodes[j]);
                }
            }
        }
        
        removeNode(id);
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
        if (adjacencyList.get(id1).contains(id2)) {
            throw new IllegalArgumentException("Nodes already connected! "
                    + "id1 = " + id1 + ", id2 = " + id2);
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

    @Override
    public String toString() {
        if (nodeList.size() != adjacencyList.size()) {
            throw new ShouldNeverHappenException("Ups! Something is wrong!");
        }
        
        String result = "";
        
        for (int i = 0; i < nodeList.size(); i++) {
            Node n = nodeList.get(i);
            if (n.id != i) {
                throw new ShouldNeverHappenException("IDs do not match!");
            }
            result += i + " (" + n.x + "; " + n.y + "): ";
            for (int j = 0; j < adjacencyList.get(i).size(); j++) {
                result += adjacencyList.get(i).get(j);
                if (j < adjacencyList.get(i).size() - 1) {
                    result += ", ";
                }
            }
            if (i < nodeList.size() - 1) {
                result += "\r\n";
            }
        }
        
        return result;
    }
    
    /**
     * Returns network as string ignoring positions (x, y) of nodes.
     */
    public String toStringAdjacencyOnly() {
        return this.toString().replaceAll(" \\(.*\\)", "");
    }

    @Override
    public Iterator<Node> iterator() {
        return nodeList.iterator();
    }
    
    public Iterable<Edge> getEdges(Dimension size) {
        return getEdges(size.width, size.height);
    }
    
    /**
     * Nodes positions are saved as double value [0, 1] which represent a position
     * proportionally to the current sizes of the window where the network
     * is displayed. Since Edge contains integer coordinates, double values
     * have to be converted into actual integer pixel positions.
     * @param width width of the panel where network is drawn
     * @param height height of the panel where network is drawn
     * @return 
     */
    public Iterable<Edge> getEdges(final int width, final int height) {
        final Iterator<Edge> iterator = new Iterator<Edge>() {
            
            private int id = 0;
            private int connectedTo = 0;
            
            private boolean hasNext = false;
            private boolean initialised = false;
            
            @Override
            public boolean hasNext() {
                if (initialised) {
                    return hasNext;
                } else {
                    while (id < getNumberOfNodes()) {
                        if (adjacencyList.get(id).isEmpty()) {
                            id++;
                        } else {
                            initialised = true;
                            hasNext = true;
                            return true;
                        }
                    }
                    return false;
                }
            }

            @Override
            public Edge next() {
                Node n1 = nodeList.get(id);
                Node n2 = nodeList.get(adjacencyList.get(id).get(connectedTo));
                /* //TODO drawing edges optimisation
                 * GUI component could have its own representation of the network
                 * with integer coordinates. It should be updating chosen nodes
                 * on the fly rather then recalculating everything (calculations
                 * below) every time the network is drawn.
                 * Obviously, this method will have to be used when the window
                 * sizes change.
                 */
                int x1 = (int) (n1.x * width);
                int y1 = (int) (n1.y * height);
                int x2 = (int) (n2.x * width);
                int y2 = (int) (n2.y * height);
                Edge edge = new Edge(x1, y1, x2, y2);
                
                boolean newFound = false;
                for (int j = connectedTo + 1; j < adjacencyList.get(id).size(); j++) {
                    if (adjacencyList.get(id).get(j) > id) {
                        connectedTo = j;
                        newFound = true;
                        break;
                    }
                }
                if (!newFound) {
                    outer:
                    for (int i = id + 1; i < getNumberOfNodes(); i++) {
                        for (int j = 0; j < adjacencyList.get(i).size(); j++) {
                            if (adjacencyList.get(i).get(j) > i) {
                                id = i;
                                connectedTo = j;
                                newFound = true;
                                break outer;
                            }
                        }
                    }
                }
                hasNext = newFound;
                
                return edge;
            }

            @Override
            public void remove() {}
        };
        
        return new Iterable<Edge>() {
            @Override
            public Iterator<Edge> iterator() {
                return iterator;
            }
        };
    }
    
    public Point getPosition(int id, Dimension size) {
        return getPosition(id, size.width, size.height);
    }
    
    public Point getPosition(int id, int width, int height) {
        Node n = nodeList.get(id);
        Point p = new Point();
        p.x = (int) (width * n.x);
        p.y = (int) (height * n.y);
        return p;
    }
    
    public Integer[] adjacentTo(int id) {
        if (id < 0 || id >= adjacencyList.size()) {
            throw new IllegalArgumentException("Incorrect ID! id = " + id);
        }
        return adjacencyList.get(id).toArray(new Integer[] {});
    }
    
    public int getNumberOfNodes() {
        if (nodeList.size() != adjacencyList.size()) {
            throw new ShouldNeverHappenException("error 12463465472625324");
        }
        return nodeList.size();
    }
    
    /**
     * Returns whether two nodes of given ids are connected or not.
     * 
     * @param id1 first node's id
     * @param id2 second node's id
     * @return true if two nodes are connected, false otherwise
     * @throws IllegalArgumentException if any id is out of range
     */
    public boolean isConnected(int id1, int id2) {
        if (id1 < 0 || id2 < 0 || id1 > nodeList.size() || id2 > nodeList.size()) {
            throw new IllegalArgumentException("Id out of range! Must be [0,"
                    + nodeList.size() + "]. id1 = " + id1 + ", id2 = " + id2 + ".");
        }
        return adjacencyList.get(Math.min(id1, id2)).contains(Math.max(id1, id2));
    }

    /**
     * Changes the node's position to the given one.
     * 
     * If any coordinate is out of range [0, 1] it will be set to 0 if it was
     * negative or to 1 if it was greater than 1.
     * 
     * @param id node's id
     * @param x new x coordinate
     * @param y new y coordinate
     * @throws IllegalArgumentException if id is out of range [0, numberOfNodes]
     */
    public void moveNode(int id, double x, double y) {
        if (id < 0 || id > nodeList.size()) {
            throw new IllegalArgumentException("Invalid ID! id = " + id + ". "
                    + "Must be in range [0," + nodeList.size() + "]");
        }
        
        nodeList.get(id).x = Math.max(0, Math.min(x, 1));
        nodeList.get(id).y = Math.max(0, Math.min(y, 1));
    }
    
    /**
     * Returns an array with ids of neighbours of node with given id.
     */
    public int[] getNeighboursIDs(int id) {
        if (id < 0 || id > nodeList.size()) {
            throw new IllegalArgumentException("Invalid id (" + id + ")! "
                    + "Must be in range [0," + nodeList.size() + "].");
        }
        List<Integer> neighbours = adjacencyList.get(id);
        int[] result = new int[neighbours.size()];
        int i = 0;
        for (int n : neighbours) {
            result[i++] = n;
        }
        return result;
    }
    
    public NetworkStats getStatistics() {
        if (getNumberOfNodes() == 0) {
            return new NetworkStats(0, 0, 0, 0, 0, 0, 0);
        }
        
        int[] neighbours = new int[adjacencyList.size()];
        {
            int index = 0;
            for (List list : adjacencyList) {
                neighbours[index++] = list.size();
            }
        }
        Arrays.sort(neighbours);
        
        int nodes = neighbours.length;
        
        int edges_x2 = 0;
        for (int i : neighbours) {
            edges_x2 += i;
        }
        
        int degreeMin = neighbours[0];
        
        int degreeMax = neighbours[neighbours.length - 1];
        
        double degreeMean = (double) edges_x2 / nodes;
        
        int degreeMedian = neighbours[neighbours.length / 2];
        
        int degreeMode = 0; // best value
        int bestLength = 0;
        int currentLength = 1;
        for (int i = 1; i < neighbours.length; i++) {
            if (neighbours[i] == neighbours[i-1]) {
                currentLength++;
            } else {
                if (currentLength > bestLength) {
                    bestLength = currentLength;
                    degreeMode = neighbours[i - 1];
                }
                currentLength = 1;
            }
        }
        if (currentLength > bestLength) {
            degreeMode = neighbours[neighbours.length - 1];
        }
        
        return new NetworkStats(nodes, edges_x2 >> 1, degreeMin, degreeMax,
                                degreeMean, degreeMedian, degreeMode);
    }
    
    public boolean containsEdgesInteresctions() {
        for (Edge e1 : getEdges(800, 600)) {
            for (Edge e2 : getEdges(800, 600)) {
                if (!e1.equals(e2) && !Edge.arePolygonalChain(e1, e2)) {
                    if (Edge.intersect(e1, e2) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * @param size sizes of a drawable panel where the network is drawn
     * @param x position of mouse click on a drawable pane (in pixels)
     * @param y position of mouse click on a drawable pane (in pixels)
     * @param s size (diameter) of the node
     * @return closest node or null if not found within radius s
     */
    public Node findClosestNode(Dimension size, int x, int y, int s) {
        Node best = null;
        double bestDist = Double.MAX_VALUE;
        
        for (Node n : this) {
            int nx = (int) (n.x() * size.width);
            int ny = (int) (n.y() * size.height);
            if (Math.abs(nx - x) < s && Math.abs(ny - y) < s) {
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
     * Returns true if saved successfully.
     */
    public boolean save(File file) {
        try {
            Charset cs = Charset.forName("US-ASCII");
            BufferedWriter writer = Files.newBufferedWriter(file.toPath(), cs);
            String string = this.toString();
            writer.write(string, 0, string.length());
            writer.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    /**
     * Returns null in case of error.
     */
    public static Network load(File file) {
        try {
            Scanner scanner = new Scanner(file);
            Network network = new Network();
            
            while (scanner.hasNextLine()) {
                
                String line = scanner.nextLine().replaceAll("\\s+", "");
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                
                String[] split = line.split("\\(|;|\\):|,");
                if (split.length < 3) {
                    return null;
                }
                
                Node n = new Node(Integer.parseInt(split[0]),
                                  Double.parseDouble(split[1]),
                                  Double.parseDouble(split[2]));
                if (n.id != network.nodeList.size()) {
                    return null;
                }
                network.nodeList.add(n);
                network.adjacencyList.add(new ArrayList<Integer>());
                
                for (int i = 3; i < split.length; i++) {
                    network.adjacencyList.get(n.id).add(Integer.parseInt(split[i]));
                }
            }
            
            scanner.close();
            return network;
        } catch (NumberFormatException ex) {
            return null;
        } catch (FileNotFoundException ex) {
            return null;
        }
    }


////////////////////////////////////////////////////////////////////////////////
////// GENERATORS //////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
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
    public static Network generateHex(int width, int height, boolean more) {
        if (width < 2) {
            throw new IllegalArgumentException("Width cannot be smaller than 2.");
        }
        if (height < 1) {
            throw new IllegalArgumentException("Height cannot be smaller than 1.");
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

                //horizontal
                if (((j & 1) == 1 && i > 0) || i > 1) {
                    n.connectNodes(current, current - 1);
                }
                
                //vertical
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
                
                //extra vertical
                if (more && (i == 0 || i == width - 1) && j > 1 && (j & 1) == 1) {
                    n.connectNodes(current, current - 2 * width + 1);
                }
                
                current++;
            }
        }
        
        return n;
    }
    
    public static Network generateRing(int nodes) {
        if (nodes < 3) {
            throw new IllegalArgumentException("Value must be no smaller than 3");
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
    
    public static Network generateStar(int nodes) {
        if (nodes < 1) {
            throw new IllegalArgumentException("Value must be positive");
        }
        
        double radius = 0.4d;
        double alpha = Math.PI * 2 / (nodes - 1);
        
        Network n = new Network();
        n.addNode(0.5d, 0.5d);
        
        for (int i = 0; i < nodes - 1; i++) {
            n.addNode(0.5d - radius * Math.cos(i * alpha),
                      0.5d - radius * Math.sin(i * alpha));
            n.connectNodes(i + 1, 0);
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
    
    /**
     * Creates a random network with the maximum number of given nodes
     * and edges. Algorithm does not avoid edges intersections, however does not
     * place nodes one on the other or too close to the sides of the area
     * (as defined by the epsilon). The algorithm uses timeout to break all
     * computations and return what was generated so far. In case of large
     * epsilon and large number of nodes it is possible that the number of nodes
     * will be less than requested (in this case no edges will be generated).
     * It is guaranteed that if at least one edge was generated, the requested
     * number of nodes was generated. Finally, if algorithm breaks due to timeout
     * while generating edges, their number may be lower than requested.
     * @param nodes maximum number of nodes
     * @param edges maximum number of edges
     * @param eps epsilon to avoid overlapping nodes - range [0,1]
     * @param timeout timeout in milliseconds
     * @return a new network
     */
    public static Network generateRandom(int nodes, int edges, final double eps,
                                                           final long timeout) {
        if (nodes <= 0 || edges <= 0) {
            throw new IllegalArgumentException("Both values must be positive");
        }
        if (eps < 0 || eps > 1) {
            throw new IllegalArgumentException("Epsilon has to be in range [0,1]");
        }
        
        Random r = new Random();
        Network n = new Network();
        
        long start = System.currentTimeMillis();
        
        createNodes:
        for (int i = 0; i < nodes; i++) {
            if (System.currentTimeMillis() - start > timeout) {
                return n;
            }
            double x = r.nextDouble() * (1 - 2 * eps) + eps;
            double y = r.nextDouble() * (1 - 2 * eps) + eps;
            for (Node node : n) {
                if (Math.abs(node.x - x) < eps && Math.abs(node.y - y) < eps) {
                    i--;
                    continue createNodes;
                }
            }
            n.addNode(x, y);
        }
        
        for (int i = 0; i < edges; i++) {
            if (System.currentTimeMillis() - start > timeout) {
                return n;
            }
            int one = r.nextInt(nodes);
            int two = r.nextInt(nodes);
            if (one == two || n.isConnected(one, two)) {
                i--;
                continue;
            }
            n.connectNodes(one, two);
        }
        
        return n;
    }
    
////////////////////////////////////////////////////////////////////////////////
////// PRIVATE UTILITIES ///////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
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
                    * pow(base * base, exponent >> 1);
        }
    }
    
}
