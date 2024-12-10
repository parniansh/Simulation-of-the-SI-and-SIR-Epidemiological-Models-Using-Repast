package barabasi;

import java.io.*;
import java.util.*;

import java.io.*;
import java.util.*;

public class SIRBarabasiModel {
    private Map<Person, Set<Person>> graph; // Graph where nodes are Person objects
    private Map<Person, String> nodeStates; // Stores the state of each node ("S", "I", "R")
    private double infectionProbability; // Probability of infection
    private double recoveryProbability; // Probability of recovery

    public SIRBarabasiModel(Map<Person, Set<Person>> graph, double infectionProbability, double recoveryProbability) {
        this.graph = graph;
        this.nodeStates = new HashMap<>();
        this.infectionProbability = infectionProbability;
        this.recoveryProbability = recoveryProbability;

        // Initialize all nodes as susceptible ("S")
        for (Person node : graph.keySet()) {
            nodeStates.put(node, "S");
        }
    }

    public void initializeInfection(Person initialInfectedNode) {
        // Set the initial infected node
        if (nodeStates.containsKey(initialInfectedNode)) {
            nodeStates.put(initialInfectedNode, "I");
        } else {
            throw new IllegalArgumentException("Person does not exist in the graph.");
        }
    }

    public void simulateSpread(int steps, String outputFilePath) {
        for (int step = 1; step <= steps; step++) {
            Map<Person, String> newStates = new HashMap<>(nodeStates);

            // Process each infected node
            for (Map.Entry<Person, String> entry : nodeStates.entrySet()) {
                if ("I".equals(entry.getValue())) {
                    Person infectedNode = entry.getKey();

                    // Attempt to infect neighbors
                    for (Person neighbor : graph.get(infectedNode)) {
                        if ("S".equals(nodeStates.get(neighbor))) {
                            if (Math.random() < infectionProbability) {
                                newStates.put(neighbor, "I");
                            }
                        }
                    }

                    // Recover the current infected node
                    if (Math.random() < recoveryProbability) {
                        newStates.put(infectedNode, "R");
                    }
                }
            }

            // Update the states
            nodeStates = newStates;

            // Log the state after each step
            System.out.println("Step " + step + ": " + getStateCounts());
        }

        // Save the final states to a file
        saveFinalStates(outputFilePath);
    
    }

    private void saveFinalStates(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Person, String> entry : nodeStates.entrySet()) {
                writer.write("Person " + entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
            System.out.println("Final states saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing final states to file: " + e.getMessage());
        }
    }

    public Map<String, Integer> getStateCounts() {
        int susceptible = 0;
        int infected = 0;
        int recovered = 0;

        for (String state : nodeStates.values()) {
            switch (state) {
                case "S":
                    susceptible++;
                    break;
                case "I":
                    infected++;
                    break;
                case "R":
                    recovered++;
                    break;
            }
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("S", susceptible);
        counts.put("I", infected);
        counts.put("R", recovered);
        return counts;
    }

    public static void main(String[] args) {
        // Generate a Barabasi-Albert graph using BarabasiModel
        BarabasiModel barabasiModel = new BarabasiModel();
        int n = 750; // Number of nodes
        int m = 3;   // Number of edges per new node
        String edgeListPath = "barabasi_albert_edge_list.txt";
        barabasiModel.generateGraph(n, m, edgeListPath);

        // Load the graph into the SIRModel
        double infectionProbability = 0.9; // Probability of infection
        double recoveryProbability = 0.005; // Probability of recovery
        SIRBarabasiModel sirModel = new SIRBarabasiModel(barabasiModel.getGraph(), infectionProbability, recoveryProbability);

        // Initialize infection
        sirModel.initializeInfection(barabasiModel.getGraph().keySet().iterator().next()); // Start with the first node infected

        // Simulate the spread
        int steps = 20; // Number of steps to simulate
        String finalStatesPath = "sir_model_final_states.txt";
        sirModel.simulateSpread(steps, finalStatesPath);
    }
}