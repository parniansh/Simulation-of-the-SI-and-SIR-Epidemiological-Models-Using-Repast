package barabasi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import java.io.*;
import java.util.*;

public class BarabasiModel {
    private Map<Person, Set<Person>> graph; 
    private List<Person> degreeList; 

    public BarabasiModel() {
        this.graph = new HashMap<>();
        this.degreeList = new ArrayList<>();
    }

    public void generateGraph(int n, int m, String outputFilePath) {
        if (m >= n) {
            throw new IllegalArgumentException("The number of edges per new node (m) must be less than the total number of nodes (n).");
        }

        List<Person> initialNodes = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            Person person = new Person("S"); 
            initialNodes.add(person);
            graph.put(person, new HashSet<>());

            for (Person other : initialNodes) {
                if (!person.equals(other)) {
                    graph.get(person).add(other);
                    graph.get(other).add(person);
                }
            }
            degreeList.addAll(Collections.nCopies(m - 1, person)); 
        }

        for (int newNode = m; newNode < n; newNode++) {
            Person newPerson = new Person("S");
            Set<Person> targets = new HashSet<>();

            Random random = new Random();
            while (targets.size() < m) {
                Person target = degreeList.get(random.nextInt(degreeList.size()));
                targets.add(target);
            }

            graph.put(newPerson, new HashSet<>());
            for (Person target : targets) {
                graph.get(newPerson).add(target);
                graph.get(target).add(newPerson);
                degreeList.add(target); 
            }

            degreeList.addAll(Collections.nCopies(m, newPerson));

            if (newNode % 10000 == 0) {
                System.out.println("Current number of nodes: " + newNode);
            }
        }

        saveEdgeList(outputFilePath);
    }

    private void saveEdgeList(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Person, Set<Person>> entry : graph.entrySet()) {
                Person source = entry.getKey();
                for (Person target : entry.getValue()) {
                    if (source.hashCode() < target.hashCode()) { 
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

