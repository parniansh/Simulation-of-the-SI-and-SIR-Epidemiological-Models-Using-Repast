package simulation;

import agent.Person;
import repast.simphony.space.graph.Network;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SIEpidemicSimulation {
    private final Network<Person> network;
    private final double infectionProbability;
    private final String logFilePath;
    private int currentStep;

    public SIEpidemicSimulation(Network<Person> network, double infectionProbability, String logFilePath) {
        this.network = network;
        this.infectionProbability = infectionProbability;
        this.logFilePath = logFilePath;
        this.currentStep = 0;

        // Initialize log file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath))) {
            writer.write("Step,S,I\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void initializeInfection() {
//        // Infect the first node
//        for (Person person : network.getNodes()) {
//            person.setState("I");
//            break; // Infect only one node
//        }
//    }

    public void initializeInfection() {
        int totalNodes = network.size();
        int nodesToInfect = (int) Math.ceil(0.01 * totalNodes); // Calculate 1% of nodes

        Random random = new Random(); // Random instance for random infection
        Set<Person> nodes = new HashSet<>(); // Create an empty HashSet

        // Explicitly cast each node to Person and add to the HashSet
        for (Object node : network.getNodes()) {
            nodes.add((Person) node);
        }

        int infectedCount = 0;
        for (Person person : nodes) {
            if (infectedCount >= nodesToInfect) {
                break;
            }
            if ("S".equals(person.getState())) {
                person.setState("I");
                infectedCount++;
            }
        }

        System.out.println(nodesToInfect + " nodes randomly infected.");
    }


    
    
    @ScheduledMethod(start = 1, interval = 1)
    public void step() {
        currentStep++;

        Map<Person, String> newStates = new HashMap<>();

        for (Person person : network.getNodes()) {
            if ("I".equals(person.getState())) {
                // Try to infect neighbors
                for (Object neighborObj : network.getAdjacent(person)) {
                    Person neighbor = (Person) neighborObj;
                    if ("S".equals(neighbor.getState()) && Math.random() < infectionProbability) {
                        newStates.put(neighbor, "I");
                    }
                }
            }
        }

        // Update states
        for (Map.Entry<Person, String> entry : newStates.entrySet()) {
            entry.getKey().setState(entry.getValue());
        }

        // Log current state counts
        logState();
    }

    private void logState() {
        int susceptible = 0, infected = 0, recovered = 0;

        for (Person person : network.getNodes()) {
            switch (person.getState()) {
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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(currentStep + "," + susceptible + "," + infected  + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
