package barabasi;

import java.util.*;
import java.util.*;

public class SmallWorldModel {
	
    public Map<Person, Set<Person>> generateSmallWorld(int n, int k, double p) {
        Map<Person, Set<Person>> graph = new HashMap<>();
        List<Person> nodes = new ArrayList<>();

        // Step 1: Create Person objects for all nodes
        for (int i = 0; i < n; i++) {
            Person person = new Person("S"); // All nodes start as susceptible
            nodes.add(person);
            graph.put(person, new HashSet<>());
        }

        // Step 2: Create a ring lattice
        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= k / 2; j++) {
                Person current = nodes.get(i);
                Person neighbor = nodes.get((i + j) % n);
                Person neighborPrev = nodes.get((i - j + n) % n); // Handle wrap-around

                graph.get(current).add(neighbor);
                graph.get(current).add(neighborPrev);
                graph.get(neighbor).add(current);
                graph.get(neighborPrev).add(current);
            }
        }

        // Step 3: Rewire edges with probability p
        Random random = new Random();
        for (Person person : nodes) {
            Set<Person> neighbors = new HashSet<>(graph.get(person));
            for (Person neighbor : neighbors) {
                if (random.nextDouble() < p) {
                    graph.get(person).remove(neighbor);
                    graph.get(neighbor).remove(person);

                    Person newNeighbor;
                    do {
                        newNeighbor = nodes.get(random.nextInt(n));
                    } while (newNeighbor == person || graph.get(person).contains(newNeighbor));

                    graph.get(person).add(newNeighbor);
                    graph.get(newNeighbor).add(person);
                }
            }
        }

        return graph;
    }
}
