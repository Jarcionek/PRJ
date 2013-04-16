package network.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.simulation.Simulation;

/**
 * @author Jaroslaw Pawlak
 */
public abstract class AbstractExperiment {

    private final ExperimentScheduler scheduler;
    private final int runs;
    private final String name;

    /**
     * @param scheduler ExperimentSchduler to be notified on experiment finish
     * @param runs how many tests to perform of this simulation
     * @param name name of the output file
     */
    public AbstractExperiment(ExperimentScheduler scheduler, int runs,
                                            String name, File networkFilePath) {
        if (runs <= 0) {
            throw new IllegalArgumentException("Runs must be positive");
        }
        this.scheduler = scheduler;
        this.runs = runs;
        this.name = name;
    }
    
    public final void start() {
        Thread t = new Thread() {
            @Override
            public void run() {
                
                File f = new File(System.getProperty("user.dir"), name + ".txt");
                if (f.exists()) {
                    f = new File(System.getProperty("user.dir"), name + "x.txt");
                }
                while (f.exists()) {
                    f = new File(f.getPath().substring(0, f.getPath().length() - 4) + "x.txt");
                }
                
                PrintWriter pw;
                try {
                    pw = new PrintWriter(f);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(AbstractExperiment.class.getName())
                                                   .log(Level.SEVERE, null, ex);
                    return;
                }
                
                pw.println("Experiment name: " + name);
                pw.println("Runs: " + runs);
                pw.println(getSimulationInformation());
                String started = getCurrentTime();
                pw.println("Started: " + started);
                pw.println();
                pw.flush();
                
                long sum = 0;
                
                try {
                    for (int i = 0; i < runs; i++) {
                        Simulation simulation = createSimulation();
                        while (!simulation.isConsensus()) {
                            simulation.nextRound();
                            if (simulation.getRound() >= ExperimentScheduler.ROUNDS_LIMIT) {
                                pw.print("stopped at ");
                                break;
                            }
                        }
                        sum += simulation.getRound();
                        pw.println(simulation.getRound());
                        pw.flush();
                    }
                } catch (Exception ex) {
                    pw.println(ex);
                    for (StackTraceElement ste : ex.getStackTrace()) {
                        pw.println(ste);
                    }
                    pw.flush();
                }
                
                String ended = getCurrentTime();
                pw.println();
                pw.println("Experiment name: " + name);
                pw.println(getSimulationInformation());
                pw.println("Started: " + started);
                pw.println("Ended: " + ended);
                pw.println("Average rounds: " + (sum / runs));
                pw.close();
                
                scheduler.testFinished();
                
            }
            
        };
        t.start();
    }
    
    protected abstract Simulation createSimulation();
    
    /**
     * This method should return information about simulation to be saved into
     * logs such as network file path, agents class and which are infected,
     * number of flags etc.
     */
    protected abstract String getSimulationInformation();
    
////////////////////////////////////////////////////////////////////////////////
    
    private static String getCurrentTime() {
        String result = "";        
        Calendar c = Calendar.getInstance();
        
        result += c.get(Calendar.YEAR) / 100;
        result += "/";
        result += c.get(Calendar.MONTH) + 1 < 10? "0" : "";
        result += c.get(Calendar.MONTH) + 1;
        result += "/";
        result += c.get(Calendar.DAY_OF_MONTH) < 10? "0" : "";
        result += c.get(Calendar.DAY_OF_MONTH);
        result += " ";
        result += c.get(Calendar.HOUR_OF_DAY) < 10? "0" : "";
        result += c.get(Calendar.HOUR_OF_DAY);
        result += ":";
        result += c.get(Calendar.MINUTE) < 10? "0" : "";
        result += c.get(Calendar.MINUTE);
        result += ":";
        result += c.get(Calendar.SECOND) < 10? "0" : "";
        result += c.get(Calendar.SECOND);
        
        return result;
    }
}
