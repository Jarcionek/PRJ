package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * @author Jaroslaw Pawlak
 */
public class Tester {
    
    private static final DecimalFormat DF = new DecimalFormat("#,###");
    
    private Tester() {}
    
    public static void main(String[] args) throws IOException {
        testVisibility(10000, 100, 1, 50);
    }
    
    public static void testAgents(int tests, int minAgents, int maxAgents, int visibility)
            throws IOException {
        
        System.out.println("TESTING IN PROGRESS...");
        
        File f = new File(System.getProperty("user.dir"), getDate());
        while (f.exists()) {
            f = new File(f.getPath() + "x");
        }          

        PrintWriter pw = new PrintWriter(f);
        pw.println("Agents: " + new CircleSimulation(10, 1)
                .getAgentsType().replaceAll("=\\d*", ""));
        pw.println("Visibility: " + visibility);
        pw.println("Tests: " + tests);
        pw.println("Agents" + "\t" + "Average");
        pw.flush();
        
        for (int agents = minAgents; agents <= maxAgents; agents += 2) {
            System.out.println("Testing " + agents + " agents (out of " + maxAgents + ")");
            
            File fileTemp = new File(System.getProperty("user.dir"), "temporary");
            PrintWriter pwTemp = new PrintWriter(fileTemp);
            
            CircleSimulation sim;
            long sum = 0;
            for (int i = 0; i < tests; i++) {
                sim = new CircleSimulation(agents, visibility);
                while (!sim.isConsensus()) {
                    sim.nextRound();
                }
                sum += sim.getRound();
                pwTemp.println(i + ": " + DF.format(sim.getRound()));
                
            }
            pwTemp.close();

            pw.println(agents + "\t" + (sum / tests));
            pw.flush();
        }
        
        pw.close();
        System.out.println("TESTING FINISHED");
    }
    
    public static void testVisibility(int tests, int agents, int minVisibility,
            int maxVisibility)
            throws IOException {
        
        System.out.println("TESTING IN PROGRESS...");
        
        File f = new File(System.getProperty("user.dir"), getDate());
        while (f.exists()) {
            f = new File(f.getPath() + "x");
        }          

        PrintWriter pw = new PrintWriter(f);
        pw.println("Agents: " + new CircleSimulation(10, 1)
                .getAgentsType().replaceAll("=\\d*", ""));
        pw.println("Agents: " + agents);
        pw.println("Tests: " + tests);
        pw.println("Visibility" + "\t" + "Average");
        pw.flush();
        
        for (int visibility = minVisibility; visibility <= maxVisibility; visibility++) {
            System.out.println("Testing " + visibility + " visibility (out of " + maxVisibility + ")");
            
            File fileTemp = new File(System.getProperty("user.dir"), "temporary");
            PrintWriter pwTemp = new PrintWriter(fileTemp);
            
            CircleSimulation sim;
            long sum = 0;
            for (int i = 0; i < tests; i++) {
                sim = new CircleSimulation(agents, visibility);
                while (!sim.isConsensus()) {
                    sim.nextRound();
                }
                sum += sim.getRound();
                pwTemp.println(i + ": " + DF.format(sim.getRound()));
                
            }
            pwTemp.close();

            pw.println(visibility + "\t" + (sum / tests));
            pw.flush();
        }
        
        pw.close();
        System.out.println("TESTING FINISHED");
    }
    
    public static String getDate() {
        String result = "";        
        Calendar c = Calendar.getInstance();
        
        result += c.get(Calendar.YEAR) / 100;
        result += ".";
        result += c.get(Calendar.MONTH) + 1 < 10? "0" : "";
        result += c.get(Calendar.MONTH) + 1;
        result += ".";
        result += c.get(Calendar.DAY_OF_MONTH) < 10? "0" : "";
        result += c.get(Calendar.DAY_OF_MONTH);
        result += ".";
        result += c.get(Calendar.HOUR_OF_DAY) < 10? "0" : "";
        result += c.get(Calendar.HOUR_OF_DAY);
        result += ".";
        result += c.get(Calendar.MINUTE) < 10? "0" : "";
        result += c.get(Calendar.MINUTE);
        result += ".";
        result += c.get(Calendar.SECOND) < 10? "0" : "";
        result += c.get(Calendar.SECOND);
        
        return result;
    }
}
