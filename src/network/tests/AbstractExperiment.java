package network.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.simulation.Simulation;

/**
 * @author Jaroslaw Pawlak
 */
public abstract class AbstractExperiment {

    
    private final int runs;
    private final String name;
    
    ExperimentScheduler scheduler;
    
    private int fails = 0;

    /**
     * @param runs how many tests to perform of this simulation
     * @param experimentName name of the output file
     */
    public AbstractExperiment(int runs, String experimentName) {
        if (runs <= 0) {
            throw new IllegalArgumentException("Runs must be positive");
        }
        this.runs = runs;
        this.name = experimentName;
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
                int min = Integer.MAX_VALUE;
                int max = Integer.MIN_VALUE;
                
                try {
                    runs:
                    for (int i = 0; i < runs; i++) {
                        Simulation simulation = createSimulation();
                        while (!simulation.isConsensus()) {
                            simulation.nextRound();
                            if (simulation.getRound() >= scheduler.roundsLimit) {
                                fails++;
                                pw.println(simulation.getRound() + " (fail)");
                                pw.flush();
                                continue runs;
                            }
                        }
                        sum += simulation.getRound();
                        if (simulation.getRound() < min) {
                            min = simulation.getRound();
                        }
                        if (simulation.getRound() > max) {
                            max = simulation.getRound();
                        }
                        pw.println(simulation.getRound());
                        pw.flush();
                    }
                } catch (Exception ex) {
                    pw.println(ex);
                    for (StackTraceElement ste : ex.getStackTrace()) {
                        pw.println(ste);
                    }
                    pw.println();
                    pw.println("Ended: " + getCurrentTime());
                    pw.close();
                    
                    scheduler.testFinished();
                    return;
                }
                
                String ended = getCurrentTime();
                pw.println();
                pw.println("== EXPERIMENT ==");
                pw.println("Experiment name: " + name);
                pw.println("Started: " + started);
                pw.println("Ended: " + ended);
                pw.println();
                pw.println("== SIMULATION ==");
                pw.println(getSimulationInformation());
                pw.println();
                pw.println("== RESULTS ==");
                pw.println("Runs: " + runs);
                pw.println("Fails: " + fails);
                pw.println("Max rounds: " + (runs == fails? "N/A" : max));
                pw.println("Min rounds: " + (runs == fails? "N/A" : min));
                if (runs != fails) {
                    DecimalFormat df = new DecimalFormat("#,###.0");
                    double avg = 1.0d * sum / (runs - fails);
                    pw.println("Average rounds: " + df.format(avg));
                }
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
        
        result += c.get(Calendar.YEAR) % 100;
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
