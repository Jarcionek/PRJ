package network.GUI.simulation;

/**
 * @author Jaroslaw Pawlak
 */
class InitialisationSettings {
    
    final int indexAgent;
    final int indexInfected;
    final int indexFlags;
    final int indexConsensus;
    final boolean checkboxIncludeInfected;

    InitialisationSettings(int indexBehaviour, int indexInfectedBehaviour,
            int indexFlags, int indexConsensus, boolean checkboxIncludeInfected) {
        this.indexAgent = indexBehaviour;
        this.indexInfected = indexInfectedBehaviour;
        this.indexFlags = indexFlags;
        this.indexConsensus = indexConsensus;
        this.checkboxIncludeInfected = checkboxIncludeInfected;
    }
    
}
