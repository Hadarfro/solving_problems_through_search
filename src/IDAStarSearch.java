import java.util.*;

class IDAStarSearch extends SearchAlgorithm {
    public IDAStarSearch(BoardState initialState, BoardState goalState, boolean printTime, boolean printOpenList) {
        super(initialState, goalState, printTime, printOpenList);
    }

    @Override
    public SearchResult search() {
        long startTime = System.currentTimeMillis();
        int nodesCreated = 0;

        // Initial threshold based on heuristic
        int threshold = heuristic(initialState);

        while (threshold != Integer.MAX_VALUE) {
            int minF = Integer.MAX_VALUE;

            Deque<SearchNode> openList = new ArrayDeque<>();
            SearchNode startNode = new SearchNode(initialState, null, 0, "");
            openList.push(startNode);

            while (!openList.isEmpty()) {
                SearchNode current = openList.pop();

                // Generate successors
                current.getSuccessorsStates();
                current.successors.sort(Comparator.comparingInt(SearchNode::getTotalCost)); // Sort successors by f-cost

                for (SearchNode successor : current.successors) {
                    // Path-based loop avoidance
                    if (isInPath(current, openList)) {
                        continue; // Skip this state if it is already in the current path
                    }

                    // Check if the current node exceeds the threshold
                    int f = current.getTotalCost();
                    if (f > threshold) {
                        minF = Math.min(minF, f);
                        continue; // Skip this node and track the smallest "f" exceeding the threshold
                    }

                    // Goal test
                    if (current.state.isGoalState(goalState)) {
                        long endTime = System.currentTimeMillis();
                        double totalTime = (endTime - startTime) / 1000.0;
                        return new SearchResult(reconstructPath(current), nodesCreated, current.pathCost, totalTime);
                    }
                    openList.push(successor);
                    nodesCreated++;
                }

                // Print open list if required
                if (printOpenList) {
                    System.out.println("Open List:");
                    for (SearchNode node : openList) {
                        System.out.println(node);
                    }
                    System.out.println("---- End of Open List ----");
                }
            }

            // Update threshold for the next iteration
            threshold = minF;
        }

        // No solution found
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        return new SearchResult("No Path", nodesCreated, Integer.MAX_VALUE, totalTime);
    }

    // Checks if the given node is already in the current path (to avoid loops).
    private boolean isInPath(SearchNode node, Deque<SearchNode> stack) {
        for (SearchNode n : stack) {
            if (n.state.equals(node.state)) {
                return true;
            }
        }
        return false;
    }
}
