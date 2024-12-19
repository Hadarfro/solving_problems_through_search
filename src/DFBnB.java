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

        int nodesCreated = 0;
        int t = heuristic(initialState) * 2; // Initial upper bound
        SearchResult result = null;

        Stack<SearchNode> stack = new Stack<>();
        SearchNode startNode = new SearchNode(initialState, null, 0, "");
        stack.push(startNode);

        while (!stack.isEmpty()) {
            SearchNode currentNode = stack.pop();

            if (currentNode.isMarkedOut()) {
                continue; // Skip marked-out nodes
            }

            currentNode.setMarkedOut(true);

            // Print open list if required
            if (printOpenList) {
                System.out.println("Open List Contents:");
                for (SearchNode node : stack) {
                    System.out.println(node);
                }
            }

            // Generate and sort successors by f-cost
            currentNode.getSuccessorsStates();
            List<SearchNode> successors = currentNode.successors;
            successors.sort(Comparator.comparingInt(SearchNode::getTotalCost));

            for (int i = 0; i < successors.size(); i++) {
                SearchNode successor = successors.get(i);

                // Avoid revisiting states already in the current path
                if (isInPath(successor, stack)) {
                    continue;
                }

                // Prune nodes exceeding the current bound
                if (successor.getTotalCost() >= t) {
                    successors = successors.subList(0, i); // Prune successors efficiently
                    break;
                }

                // Goal state check
                if (successor.state.isGoalState(goalState)) {
                    t = successor.getTotalCost(); // Update bound to the cost of the solution
                    long endTime = System.currentTimeMillis();
                    double totalTime = (endTime - startTime) / 1000.0;
                    result = new SearchResult(reconstructPath(successor), nodesCreated, successor.pathCost, totalTime);
                    successors = successors.subList(0, i + 1); // Prune successors beyond the goal node
                    break;
                }

                // Push the successor to the stack
                stack.push(successor);
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

    // Helper function to check if a node is already in the current path
    private boolean isInPath(SearchNode node, Stack<SearchNode> stack) {
        for (SearchNode n : stack) {
            if (n.state.equals(node.state)) {
                return true;
            }
        }
        return false;
    }
}
