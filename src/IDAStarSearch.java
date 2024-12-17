import java.util.*;

class IDAStarSearch extends SearchAlgorithm {
    public IDAStarSearch(BoardState initialState, BoardState goalState, boolean printTime, boolean printOpenList) {
        super(initialState, goalState, printTime, printOpenList);
    }

    @Override
    public SearchResult search() {
        long startTime = System.currentTimeMillis();
        int nodeCreated = 1;

        // Calculate initial heuristic for the start node
        int t = heuristic(initialState);

        // Avoid unnecessary object creation
        List<SearchNode> successorBuffer = new ArrayList<>();

        while (t != Integer.MAX_VALUE) {
            int minF = Integer.MAX_VALUE;

            Deque<SearchNode> L = new ArrayDeque<>();
            Map<BoardState, SearchNode> H = new HashMap<>();

            SearchNode startNode = new SearchNode(initialState, null, 0, "");
            L.push(startNode);
            H.put(initialState, startNode);

            while (!L.isEmpty()) {
                SearchNode n = L.pop();

                if (n.isMarkedOut()) {
                    H.remove(n.state); // Remove nodes fully explored
                    continue;
                }

                n.setMarkedOut(true);
                L.push(n); // Reinsert the node after marking as "out"

                // Print open list if required
                if (printOpenList) {
                    System.out.println("Open List:");
                    for (SearchNode node : L) {
                        System.out.println(node);
                    }
                    System.out.println("---- End of Open List ----");
                }

                n.getSuccessorsStates();
                for (SearchNode successor : n.successors) {
                    int f = successor.getTotalCost();

                    if (f > t) {
                        minF = Math.min(minF, f);
                        continue; // Skip successors exceeding the threshold
                    }

                    SearchNode existingNode = H.get(successor.state);

                    if (existingNode != null) {
                        if (existingNode.isMarkedOut()) {
                            continue; // Skip fully explored nodes
                        }
                        if (existingNode.getTotalCost() > f) {
                            L.remove(existingNode); // Remove dominated node
                            H.remove(existingNode.state);
                        }
                        else {
                            continue; // Skip if the existing node is better
                        }
                    }

                    // Check if the goal state is reached
                    if (successor.state.isGoalState(goalState)) {
                        long endTime = System.currentTimeMillis();
                        double totalTime = (endTime - startTime) / 1000.0;
                        return new SearchResult(reconstructPath(successor), nodeCreated, successor.pathCost, totalTime);
                    }

                    nodeCreated++;
                    L.push(successor);
                    H.put(successor.state, successor);
                }
            }
            t = minF; // Update threshold
        }

        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        return new SearchResult("No Path", 0, Integer.MAX_VALUE, totalTime);
    }
}
