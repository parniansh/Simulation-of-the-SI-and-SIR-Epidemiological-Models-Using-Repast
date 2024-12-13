package simulation;


import agent.Person;
import repast.simphony.space.graph.Network;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SIREpidemicSimulation {
    private final Network<Person> network;
    private final double infectionProbability;
    private final double recoveryProbability;
    private final String logFilePath;
    private int currentStep;

    public SIREpidemicSimulation(Network<Person> network, double infectionProbability, double recoveryProbability, String logFilePath) {
        this.network = network;
        this.infectionProbability = infectionProbability;
        this.recoveryProbability = recoveryProbability;
        this.logFilePath = logFilePath;
        this.currentStep = 0;

        // Initialize log file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath))) {
            writer.write("Step,S,I,R\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeInfection() {
        for (Person person : network.getNodes()) {
            person.setState("I"); // Infect one node
            break;
        }
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

                // Try to recover
                if (Math.random() < recoveryProbability) {
                    newStates.put(person, "R");
                }
            }
        }

        // Update states
        for (Map.Entry<Person, String> entry : newStates.entrySet()) {
            entry.getKey().setState(entry.getValue());
        }

        // Log state counts
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
            writer.write(currentStep + "," + susceptible + "," + infected + "," + recovered + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

