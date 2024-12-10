package barabasi;

import java.io.*;
import java.util.*;


public class SIBarabasiModel {
    private Map<Person, Set<Person>> graph; // Graph where nodes are Person objects
    private Map<Person, String> nodeStates; // Stores the state of each node ("S" or "I")
    private double infectionProbability;

    public SIBarabasiModel(int n, int m, double infectionProbability) {
        // Generate the Barabási-Albert graph using BarabasiModel
        BarabasiModel barabasiModel = new BarabasiModel();
        String edgeListPath = "barabasi_albert_edge_list.txt";
        barabasiModel.generateGraph(n, m, edgeListPath);
        this.graph = barabasiModel.getGraph();

        this.nodeStates = new HashMap<>();
        this.infectionProbability = infectionProbability;

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
            throw new IllegalArgumentException("Node does not exist in the graph.");
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

    private Map<String, Integer> getStateCounts() {
        int susceptible = 0;
        int infected = 0;

        for (String state : nodeStates.values()) {
            if ("S".equals(state)) {
                susceptible++;
            } else if ("I".equals(state)) {
                infected++;
            }
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("S", susceptible);
        counts.put("I", infected);
        return counts;
    }

    public static void main(String[] args) {
        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        int n = 750; // Total number of nodes
        int m = 3;   // Number of edges per new node
        double infectionProbability = 0.2; // Probability of infection

        String finalStatesPath = "si_model_barabasi_final_states.txt";

        // Create SIModel using BarabasiModel
        SIBarabasiModel siModel = new SIBarabasiModel(n, m, infectionProbability);
        siModel.initializeInfection(siModel.graph.keySet().iterator().next()); // Infect the first node

        // Simulate the spread for 20 steps
        siModel.simulateSpread(20, finalStatesPath);
    }
}
