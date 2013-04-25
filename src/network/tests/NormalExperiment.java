package network.tests;

import java.io.File;
import network.creator.Network;
import network.simulation.Simulation;

/**
 * Differentiation. No infections.
 * 
 * @author Jaroslaw Pawlak
 */
public class NormalExperiment extends AbstractExperiment {

    private final Network network;
    private final String networkName;
    private final Class agentClass;
    private final int maxFlags;

    public NormalExperiment(File networkFilePath, Class agentClass, int maxFlags,
            int runs, String name) {
        super( runs, name);
        this.network = Network.load(networkFilePath);
        this.networkName = networkFilePath.getAbsolutePath();
        this.agentClass = agentClass;
        this.maxFlags = maxFlags;
    }
    
    public NormalExperiment(Network network, String networkName, Class agentClass,
            int maxFlags, int runs, String experimentName) {
        super(runs, experimentName);
        this.network = network;
        this.networkName = networkName;
        this.agentClass = agentClass;
        this.maxFlags = maxFlags;
    }
    
    @Override
    protected Simulation createSimulation() {
        return new Simulation(network, agentClass, maxFlags,
                Simulation.DIFFERENTIATION, false);
    }

    @Override
    protected String getSimulationInformation() {
        return "Network: " + networkName + "\r\n"
                + "Infections: NO\r\n"
                + "Agent class: " + agentClass + "\r\n"
                + "Flags: " + maxFlags;
    }
    
}
