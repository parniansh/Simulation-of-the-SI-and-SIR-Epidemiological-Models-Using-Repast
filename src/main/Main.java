package main;


import agent.Person;
import context.EpidemicContextBuilder;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.space.graph.Network;
import simulation.SIEpidemicSimulation;
import simulation.SIREpidemicSimulation;

public class Main {
    public static void main(String[] args) {
        // Simulation parameters
        String networkType = "BA"; // Choose "BA" for Barabási-Albert or "SW" for Small World
        int numNodes = 1000000;
        double infectionProbability = 0.2;
        double recoveryProbability = 0.1;
        int steps = 20;

        // Step 1: Initialize Context and Network
        EpidemicContextBuilder builder = new EpidemicContextBuilder(networkType, numNodes);
        Context<Person> context = builder.build(new DefaultContext<>());

        Network<Person> network = (Network<Person>) context.getProjection("SIRNetwork");

        // Step 2: Initialize Simulation
//        SIREpidemicSimulation simulation = new SIREpidemicSimulation(
//            network,
//            infectionProbability,
//            recoveryProbability,
//            "sir_output_log.txt"
//        );
        
        SIEpidemicSimulation simulation = new SIEpidemicSimulation(
                network,
                infectionProbability,
                "si_output_log.txt"
            );
        
        

        // Initialize infection
        simulation.initializeInfection();


        // Step 3: Simplified Repast Scheduling Initialization
        try {
            // Initialize scheduler
            Schedule schedule = new Schedule();
            RunEnvironment.init(schedule, null, null, true);
            
            if (RunEnvironment.getInstance() == null) {

                throw new IllegalStateException("RunEnvironment failed to initialize.");

            }
            ScheduleParameters params = ScheduleParameters.createRepeating(1, 1); // Start at 1, repeat every step
            schedule.schedule(params, simulation, "step");

            // Step 7: Execute the Scheduler
            int currentStep = 0;
            while (currentStep < steps) {
                schedule.execute();
                currentStep++;
            }
            
            // Other initialization steps...
//            RunEnvironment.getInstance().endAt(20); // For example
        } catch (Exception e) {
            e.printStackTrace();
        }

        

        System.out.println("Simulation completed. Results saved to si_output_log.txt");
    }
}
