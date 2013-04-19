package network;

import java.io.File;
import network.simulation.agents.*;
import network.tests.ExperimentScheduler;

/**
 * @author Jaroslaw Pawlak
 */
public class Tests {

    private static final String DIR = "c:\\Users\\Jarcionek\\Documents\\";
    
    public static void main(String[] args) throws InterruptedException {
        ExperimentScheduler scheduler = new ExperimentScheduler("TEST", 3);
        scheduler.addNormalExperiment(new File(DIR, "hex-7-7-e.network"), LCFAgentND.class, 4, 1000);
//        for (int i = 0; i < 10; i++) {
//            scheduler.addNormalExperiment(new File(DIR, "ring-20.network"), RandomAgent.class, 3, 1000);
//        }
        scheduler.execute();
    }

}
