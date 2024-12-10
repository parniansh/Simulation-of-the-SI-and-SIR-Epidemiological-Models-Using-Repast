package barabasi;

import java.io.*;
import java.util.*;
import java.io.*;
import java.util.*;

import java.io.*;
import java.util.*;

public class SISmallWorldModel {
    private Map<Person, Set<Person>> graph; // The network of connections
    private Map<Person, String> nodeStates; // Stores the state of each node ("S", "I")
    private double infectionProbability; // Probability of infection

    public SISmallWorldModel(Map<Person, Set<Person>> graph, double infectionProbability) {
        this.graph = graph;
        this.nodeStates = new HashMap<>();
        this.infectionProbability = infectionProbability;

        // Initialize all nodes as susceptible ("S")
        for (Person person : graph.keySet()) {
            nodeStates.put(person, "S");
        }
    }

    public void initializeInfection(Person initialInfected) {
        // Set the initial infected node
        if (nodeStates.containsKey(initialInfected)) {
            nodeStates.put(initialInfected, "I");
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
                    Person infectedPerson = entry.getKey();

                    // Attempt to infect neighbors
                    for (Person neighbor : graph.get(infectedPerson)) {
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
                writer.write(entry.getKey() + ": " + entry.getValue());
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

        // Generate a Small World Network
        SmallWorldModel smallWorldModel = new SmallWorldModel();
        int n = 200; // Number of nodes
        int k = 4;   // Each node connected to k/2 neighbors on both sides
        double p = 0.1; // Rewiring probability
        Map<Person, Set<Person>> graph = smallWorldModel.generateSmallWorld(n, k, p);

        // Initialize SI Model
        double infectionProbability = 0.1; // Probability of infection
        SISmallWorldModel siModel = new SISmallWorldModel(graph, infectionProbability);

        // Start the simulation
        Person initialInfected = graph.keySet().iterator().next(); // Infect the first person
        siModel.initializeInfection(initialInfected);
        int steps = 20; // Number of steps to simulate
        String finalStatesPath = "output/si_model_small_world_final_states.txt";
        
//        String finalStatesPath = System.getProperty("user.home") + "/Desktop/output.txt";

        siModel.simulateSpread(steps, finalStatesPath);
    }
}

