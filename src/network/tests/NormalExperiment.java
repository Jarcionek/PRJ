package network.tests;

import java.io.File;
import network.creator.Network;
import network.simulation.Simulation;

/**
 * @author Jaroslaw Pawlak
 */
class NormalExperiment extends AbstractExperiment {

    private final Network network;
    private final File networkFilePath;
    private final Class agentClass;
    private final int maxFlags;

    NormalExperiment(File network, Class agentClass, int maxFlags,
                         ExperimentScheduler scheduler, int runs, String name) {
        super(scheduler, runs, name, network);
        this.network = Network.load(network);
        this.networkFilePath = network;
        this.agentClass = agentClass;
        this.maxFlags = maxFlags;
    }
    
    @Override
    protected Simulation createSimulation() {
        return new Simulation(network, agentClass, maxFlags);
    }

    @Override
    protected String getSimulationInformation() {
        return "Network: " + networkFilePath + "\r\n"
                + "Infections: NO\r\n"
                + "Agent class: " + agentClass + "\r\n"
                + "Flags: " + maxFlags;
    }
    
}
