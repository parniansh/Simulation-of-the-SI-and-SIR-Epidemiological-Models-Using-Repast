package network;


import repast.simphony.space.graph.Network;

public interface NetworkModel<T> {
    void buildNetwork(Network<T> network, int numNodes);
}

