package network;

import java.io.File;
import network.creator.Network;
import network.simulation.Simulation;
import network.simulation.agents.LCFAgentND;
import network.simulation.agents.RandomAgent;
import network.simulation.agents.WeightedAgent2;
import network.tests.AbstractExperiment;
import network.tests.ExperimentScheduler;
import network.tests.NormalExperiment;

/**
 * @author Jaroslaw Pawlak
 */
public class Tests {

    private static final String DIR = "c:\\Users\\Jarcionek\\Documents\\";
    
    private static final String hex_7_7_e = "hex-7-7-e.network";
    private static final String ring_20 = "ring-20.network";
    
    private static final File n = new File(DIR, ring_20);
    
    private static final int THREADS = 2;
    private static final int ROUND_LIMIT = 1000;
    private static final int RUNS = 100000;
    private static final int FLAGS = 2;
    
    private static final ExperimentScheduler SCHEDULER
            = new ExperimentScheduler("TEST", THREADS, ROUND_LIMIT);
    
    public static void main(String[] args) throws InterruptedException {
        
        Network[] nx = {
            Network.generateRing(20),
            Network.generateFullTree(5, 3),
            Network.generateFullyConnectedMesh(20),
            Network.generateGrid(7, 7),
            Network.generateHex(7, 7, true),
            Network.generateStar(20),
        };
        
        String[] names = {
            "ring-20",
            "fulltree-5-3",
            "full-20",
            "grid-7-7",
            "hex-7-7-e",
            "star-20",
        };
        
        int[] f = {
            2,
            2,
            20,
            2,
            4,
            2,
        };
        
        for (int i = 0; i < nx.length; i++) {
            addInfExperiment(LCFAgentND.class, nx[i], names[i], f[i]);
            addInfExperiment(WeightedAgent2.class, nx[i], names[i], f[i]);
        }
        
        SCHEDULER.execute();
    }
    
    private static void addInfExperiment(final Class c, final Network n, final String networkName, final int flags) {
        String name = c.getSimpleName() + "-" + networkName + "-INF";
        AbstractExperiment exp = new AbstractExperiment(RUNS, name) {

            @Override
            protected Simulation createSimulation() {
                Class[] agents = new Class[n.getNumberOfNodes()];
                boolean[] infected = new boolean[n.getNumberOfNodes()];
                for (int i = 0; i < agents.length; i++) {
                    if (i == 1) {
                        agents[i] = RandomAgent.class;
                        infected[i] = true;
                    } else {
                        agents[i] = c;
                        infected[i] = false;
                    }
                }
                Simulation s = new Simulation(n, agents, infected, flags, true,
                        Simulation.DIFFERENTIATION, false);
                return s;
            }

            @Override
            protected String getSimulationInformation() {
                return "Network: " + networkName + "\r\n"
                    + "Infections: 1 - " + RandomAgent.class + "\r\n"
                    + "Agent class: " + c + "\r\n"
                    + "Flags: " + flags;
            }
        };
        SCHEDULER.addExperiment(exp);
    } 

    private static void ringNumberAgents(Class c, int agents) {
        SCHEDULER.addExperiment(new NormalExperiment(Network.generateRing(agents),
                "ring-" + agents, c, 2, RUNS, c.getSimpleName() + "-" + agents));
    }
    
    private static void addExperiment(Class c, Network n, String networkName, int flags) {
        SCHEDULER.addExperiment(new NormalExperiment(
                n, networkName, c, flags, RUNS, c.getSimpleName() + "-" + networkName));
    }
    
    private static void addExperiment(Class c) {
        SCHEDULER.addExperiment(new NormalExperiment(
                n, c, FLAGS, RUNS, c.getSimpleName()));
    }
}
