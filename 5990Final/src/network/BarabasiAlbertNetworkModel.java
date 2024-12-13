package network;

import agent.Person;
import repast.simphony.space.graph.Network;
import repast.simphony.context.Context;

import java.util.*;

public class BarabasiAlbertNetworkModel {
    private final int m; // Number of edges per new node

    public BarabasiAlbertNetworkModel(int m) {
        this.m = m;
    }

    public void buildNetwork(Network<Person> network, Context<Person> context, int numNodes) {
        List<Person> nodes = new ArrayList<>();
        Random random = new Random();

        // Add initial fully connected nodes
        for (int i = 0; i < m; i++) {
            Person person = new Person();
            context.add(person); // Add node to context
            nodes.add(person);
            for (int j = 0; j < i; j++) {
                network.addEdge(person, nodes.get(j));
            }
        }

        // Add new nodes
        for (int i = m; i < numNodes; i++) {
            Person newPerson = new Person();
            context.add(newPerson); // Add node to context

            // Add m edges to existing nodes with probability proportional to degree
            Set<Person> targets = new HashSet<>();
            while (targets.size() < m) {
                Person target = nodes.get(random.nextInt(nodes.size()));
                if (!targets.contains(target)) {
                    targets.add(target);
                    network.addEdge(newPerson, target);
                }
            }

            nodes.add(newPerson);
        }
    }
}
