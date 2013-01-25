package network.creator;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {

    public static void main(String[] args) {
        new GUI(Network.generateFullTree(5, 3));
//        new GUI(Network.generateFullyConnectedMesh(20));
//        new GUI(Network.generateRing(20));
//        new GUI(Network.generateHex(70, 5));
//        new GUI(Network.generateGrid(7, 5));
    }

}
