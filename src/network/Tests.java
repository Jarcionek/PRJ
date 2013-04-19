package network;

import java.io.File;
import network.simulation.agents.*;
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
    
    private static final int THREADS = 3;
    private static final int ROUND_LIMIT = 1000;
    private static final int RUNS = 100000;
    private static final int FLAGS = 2;
    
    private static final ExperimentScheduler SCHEDULER
            = new ExperimentScheduler("TEST", THREADS, ROUND_LIMIT);
    
    public static void main(String[] args) throws InterruptedException {
        
        addExperiment(ThirdAgent2F.class);
        addExperiment(ThirdAgent2Fc1.class);
        addExperiment(ThirdAgent2Fc2.class);
        addExperiment(ThirdAgent2Fc3.class);
        addExperiment(ThirdAgent2Fc4.class);
        addExperiment(ThirdAgent2Fc5.class);
        addExperiment(LCFAgentND.class);
        addExperiment(WeightedAgent.class);
        addExperiment(WeightedAgent2.class);
        
        SCHEDULER.execute();
    }

    private static void addExperiment(Class c) {
        SCHEDULER.addExperiment(new NormalExperiment(
                n, c, FLAGS, SCHEDULER, RUNS, c.getSimpleName()));
    }
}
