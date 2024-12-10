package barabasi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.io.*;
import java.util.*;

public class BarabasiModel {
    private Map<Person, Set<Person>> graph; // Graph where nodes are Person objects
    private List<Person> degreeList; // List to track node degrees for preferential attachment

    public BarabasiModel() {
        this.graph = new HashMap<>();
        this.degreeList = new ArrayList<>();
    }

    public void generateGraph(int n, int m, String outputFilePath) {
        if (m >= n) {
            throw new IllegalArgumentException("The number of edges per new node (m) must be less than the total number of nodes (n).");
        }

        // Create initial complete graph with `m` nodes
        List<Person> initialNodes = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            Person person = new Person("S"); // All nodes start as susceptible
            initialNodes.add(person);
            graph.put(person, new HashSet<>());

            for (Person other : initialNodes) {
                if (!person.equals(other)) {
                    graph.get(person).add(other);
                    graph.get(other).add(person);
                }
            }
            degreeList.addAll(Collections.nCopies(m - 1, person)); // Add each node `m-1` times for initial degrees
        }

        // Add new nodes with preferential attachment
        for (int newNode = m; newNode < n; newNode++) {
            Person newPerson = new Person("S");
            Set<Person> targets = new HashSet<>();

            // Select `m` unique target nodes based on their degree
            Random random = new Random();
            while (targets.size() < m) {
                Person target = degreeList.get(random.nextInt(degreeList.size()));
                targets.add(target);
            }

            // Add new node and connect to targets
            graph.put(newPerson, new HashSet<>());
            for (Person target : targets) {
                graph.get(newPerson).add(target);
                graph.get(target).add(newPerson);
                degreeList.add(target); // Increase the degree count
            }

            // Add the new node `m` times for its initial connections
            degreeList.addAll(Collections.nCopies(m, newPerson));

            // Progress status for large graphs
            if (newNode % 10000 == 0) {
                System.out.println("Current number of nodes: " + newNode);
            }
        }

        // Save the graph as an edge list
        saveEdgeList(outputFilePath);
    }

    private void saveEdgeList(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Person, Set<Person>> entry : graph.entrySet()) {
                Person source = entry.getKey();
                for (Person target : entry.getValue()) {
                    if (source.hashCode() < target.hashCode()) { // Avoid duplicate edges in undirected graph
                        writer.write(source + " " + target);
                        writer.newLine();
                    }
                }
            }
            System.out.println("Edge list saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing edge list to file: " + e.getMessage());
        }
    }

    public Map<Person, Set<Person>> getGraph() {
        return graph;
    }
}

