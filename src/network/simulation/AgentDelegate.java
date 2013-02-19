package network.simulation;

/**
 * This interface should contain methods accessible by other agents that can
 * see this agent delegate.
 * 
 * This interface should no contain methods that would allow to see beyond
 * this agent delegate and agent's visibility.
 * 
 * @author Jaroslaw Pawlak
 */
public interface AgentDelegate {
    /**
     * Returns the flag raised in the previous round.
     * @return the flag raised in the previous round
     */
    public int getFlag();
}
