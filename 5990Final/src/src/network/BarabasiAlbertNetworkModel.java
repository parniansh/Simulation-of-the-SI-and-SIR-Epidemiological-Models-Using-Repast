package network;

import agent.Person;
import repast.simphony.space.graph.Network;
import repast.simphony.context.Context;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.util.*;

import repast.simphony.context.Context;
import repast.simphony.space.graph.Network;

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

        // Add new nodes with degree-proportional attachment
        for (int i = m; i < numNodes; i++) {
            Person newPerson = new Person();
            context.add(newPerson); // Add node to context

            // Degree-proportional attachment: Use a weighted list
            List<Person> degreeList = new ArrayList<>();
            for (Person node : nodes) {
                int degree = network.getDegree(node);
                for (int d = 0; d < degree; d++) {
                    degreeList.add(node); // Add node multiple times based on its degree
                }
            }

            // Select m unique targets from the degree-proportional list
            Set<Person> targets = new HashSet<>();
            while (targets.size() < m) {
                Person target = degreeList.get(random.nextInt(degreeList.size())); // Weighted random selection
                if (!targets.contains(target)) {
                    targets.add(target);
                    network.addEdge(newPerson, target); // Add edge to the target node
                }
            }

            nodes.add(newPerson); // Add new node to the node list
        }
    }
}

