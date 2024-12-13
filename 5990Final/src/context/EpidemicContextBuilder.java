package context;

import agent.Person;
import network.BarabasiAlbertNetworkModel;
import network.SmallWorldNetworkModel;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.graph.Network;

public class EpidemicContextBuilder implements ContextBuilder<Person> {
    private final String networkType; // "BA" or "SW"
    private final int numNodes;

    public EpidemicContextBuilder(String networkType, int numNodes) {
        this.networkType = networkType;
        this.numNodes = numNodes;
    }

    @Override
    public Context<Person> build(Context<Person> context) {
        if (context == null) {
            context = new DefaultContext<>();
        }

        // Create the network
        NetworkBuilder<Person> netBuilder = new NetworkBuilder<>("SIRNetwork", context, true);
        Network<Person> network = netBuilder.buildNetwork();

        // Build the network based on the selected type
        if ("BA".equals(networkType)) {
            BarabasiAlbertNetworkModel baModel = new BarabasiAlbertNetworkModel(3); // Example for BA
            baModel.buildNetwork(network, context, numNodes); // Pass the context here
        } else if ("SW".equals(networkType)) {
            SmallWorldNetworkModel swModel = new SmallWorldNetworkModel(6, 0.1); // Example for SW
            swModel.buildNetwork(network, context, numNodes); // Pass the context here
        }

        return context;
    }

}
