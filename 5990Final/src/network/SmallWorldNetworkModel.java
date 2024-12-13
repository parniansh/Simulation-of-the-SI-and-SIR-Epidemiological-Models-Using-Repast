package network;

import agent.Person;
import repast.simphony.space.graph.Network;
import repast.simphony.context.Context;

import java.util.*;

public class SmallWorldNetworkModel {
    private final int k; // Each node connected to k/2 neighbors on both sides
    private final double p; // Rewiring probability

    public SmallWorldNetworkModel(int k, double p) {
        this.k = k;
        this.p = p;
    }

    public void buildNetwork(Network<Person> network, Context<Person> context, int numNodes) {
        List<Person> nodes = new ArrayList<>();
        Random random = new Random();

        // Create nodes
        for (int i = 0; i < numNodes; i++) {
            Person person = new Person();
            context.add(person); // Add node to context
            nodes.add(person);
        }

        // Create ring lattice
        for (int i = 0; i < numNodes; i++) {
            for (int j = 1; j <= k / 2; j++) {
                network.addEdge(nodes.get(i), nodes.get((i + j) % numNodes));
            }
        }

        // Rewire edges
        for (int i = 0; i < numNodes; i++) {
            for (int j = 1; j <= k / 2; j++) {
                if (random.nextDouble() < p) {
                    Person target = nodes.get((i + j) % numNodes);
                    network.removeEdge(network.getEdge(nodes.get(i), target));
                    Person newTarget;
                    do {
                        newTarget = nodes.get(random.nextInt(numNodes));
                    } while (newTarget == nodes.get(i) || network.isAdjacent(nodes.get(i), newTarget));
                    network.addEdge(nodes.get(i), newTarget);
                }
            }
        }
    }
}
