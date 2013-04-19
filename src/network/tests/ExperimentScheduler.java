package network.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jaroslaw Pawlak
 */
public class ExperimentScheduler {
    
    public final int roundsLimit;
    
    private final List<AbstractExperiment> queue;
    private final String name;
    private final int maxThreads;
    
    private final AtomicInteger currentThreads = new AtomicInteger(0);
    
    private Thread thread;

    /**
     * @param name name for this series of experiments - they will be called
     * X001, X002 and so on unless specified differently in an experiment
     * @param maxThreads maximum number of threads used to perform experiments
     */
    public ExperimentScheduler(String name, int maxThreads, int roundsLimit) {
        this.queue = new LinkedList<AbstractExperiment>();
        this.name = name;
        this.maxThreads = maxThreads;
        this.roundsLimit = roundsLimit;
    }
    
    public void addExperiment(AbstractExperiment experiment) {
        queue.add(experiment);
    }
    
    public void addNormalExperiment(File network, Class agentClass, int maxFlags,
                                                                     int runs) {
        NormalExperiment ne = new NormalExperiment(network, agentClass, maxFlags,
                this, runs, name + intoThreeDigit(queue.size() + 1));
        addExperiment(ne);
    }
    
    /**
     * Starts executing the experiments in order they were added to the scheduler.
     * This method blocks.
     */
    public void execute() {
        thread = Thread.currentThread();
        while (!queue.isEmpty()) {
            if (currentThreads.get() < maxThreads) {
                queue.remove(0).start();
                currentThreads.incrementAndGet();
            } else {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ex) {}
            }
        }
        while (currentThreads.get() > 0) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {}
        }
    }

    /**
     * To be called by an experiment object (added to this scheduler) on finish
     */
    void testFinished() {
        currentThreads.decrementAndGet();
        thread.interrupt();
    }
    
    
    
    private static String intoThreeDigit(int value) {
        if (value < 0) {
            return "" + value;
        }
        if (value < 10) {
            return "00" + value;
        }
        if (value < 100) {
            return "0" + value;
        }
        return "" + value;
    }
    
}
