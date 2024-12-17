import java.util.*;
import java.util.Collections;

class DFBnBSearch extends SearchAlgorithm {
    public DFBnBSearch(BoardState initialState, BoardState goalState,
                       boolean printTime, boolean printOpenList) {
        super(initialState, goalState, printTime, printOpenList);
    }

    @Override
    public SearchResult search() {
        long startTime = System.currentTimeMillis();

        int nodesCreated = 1;
        int t = heuristic(initialState); // Initial upper bound
        System.out.println("the bound is: " + t);
        SearchResult result = null;

        Stack<SearchNode> L = new Stack<>();
        Map<BoardState, SearchNode> H = new HashMap<>();

        SearchNode startNode = new SearchNode(initialState, null, 0, "");
        L.push(startNode);
        H.put(initialState, startNode);

        while (!L.isEmpty()) {
            SearchNode n = L.pop();

            if (n.isMarkedOut()) {
                H.remove(n.state); // Correctly remove node from H
                continue;
            }

            n.setMarkedOut(true);
            L.push(n); // Marked out nodes go back into the stack for cleanup later

            // Print open list if required
            if (printOpenList) {
                System.out.println("Open List Contents:");
                for (SearchNode node : L) {
                    System.out.println(node);
                }
            }


            n.getSuccessorsStates();
            // Generate successors
            List<SearchNode> successors = n.successors;
            successors.sort(Comparator.comparingInt(SearchNode::getTotalCost)); // Use Comparator for clarity

            for (int i = 0; i < successors.size(); i++) {
                SearchNode successor = successors.get(i);

                // Prune nodes with cost >= t
                if (successor.getTotalCost() > t) {
                    successors = successors.subList(0, i); // Efficiently prune remaining successors
                    break;
                }

                SearchNode existingNode = H.get(successor.state);

                // Handle nodes already in the hash table
                if (existingNode != null) {
                    if (successor.isMarkedOut()) {
                        continue; // Skip already marked-out nodes
                    } else if (existingNode.getTotalCost() <= successor.getTotalCost()) {
                        continue; // Skip if the existing node is better or equal
                    } else {
                        L.remove(existingNode); // Remove the inferior node from the stack
                        H.remove(existingNode.state);
                    }
                }

                // Check for goal state
                if (successor.state.isGoalState(goalState)) {
                    t = successor.getTotalCost();
                    long endTime = System.currentTimeMillis();
                    double totalTime = (endTime - startTime) / 1000.0;
                    result = new SearchResult(reconstructPath(successor), nodesCreated, successor.pathCost, totalTime);
                    successors = successors.subList(0, i + 1); // Prune successors beyond the goal node
                    break;
                }

                // Add the successor to the stack and hash table
                L.push(successor);
                H.put(successor.state, successor);
                nodesCreated++;
            }
        }

        if (result == null) {
            long endTime = System.currentTimeMillis();
            double totalTime = (endTime - startTime) / 1000.0;
            return new SearchResult("No Path", nodesCreated, Integer.MAX_VALUE, totalTime);
        }

        return result;
    }
}