import java.util.*;

class AStarSearch extends SearchAlgorithm {

    public AStarSearch(BoardState initialState, BoardState goalState,
                       boolean printTime, boolean printOpenList) {
        super(initialState, goalState, printTime, printOpenList);
    }

    @Override
    public SearchResult search() {
        long startTime = System.currentTimeMillis();

        // Priority queue for open list (f(n) = g(n) + h(n))
        PriorityQueue<SearchNode> openList = new PriorityQueue<>(Comparator.comparingInt(SearchNode::getTotalCost));

        // Map to store the best nodes for each state
        Map<BoardState, SearchNode> bestNodes = new HashMap<>();

        // Initialize the start node
        SearchNode startNode = new SearchNode(initialState, null, 0, "");
        openList.add(startNode);
        bestNodes.put(initialState, startNode);

        while (!openList.isEmpty()) {
            // Get the node with the lowest total cost
            SearchNode currentNode = openList.poll();

            // Check if we reached the goal
            if (currentNode.state.isGoalState(goalState)) {
                long endTime = System.currentTimeMillis();
                double totalTime = (endTime - startTime) / 1000.0;
                return new SearchResult(reconstructPath(currentNode), bestNodes.size(), currentNode.pathCost, totalTime);
            }

            // If the current node is no longer optimal, skip it
            if (currentNode.getTotalCost() > bestNodes.get(currentNode.state).getTotalCost()) {
                continue;
            }

            // Generate successors dynamically
            currentNode.getSuccessorsStates();

            // Process each successor
            for (SearchNode successor : currentNode.successors) {
                int newPathCost = currentNode.pathCost + calculateMoveCost(currentNode, successor);

                // If the successor is not optimal, skip it
                if (bestNodes.containsKey(successor.state) && bestNodes.get(successor.state).getTotalCost() <= successor.getTotalCost()) {
                    continue;
                }
                // Update best known node and add to the open list
                bestNodes.put(successor.state, successor);
                openList.add(successor);
            }

            // Print the open list if required
            if (printOpenList) {
                System.out.println("Open List Contents:");
                for (SearchNode node : openList) {
                    System.out.println(node);
                }
            }
        }

        // No path found
        long endTime = System.currentTimeMillis();
        double totalTime = (endTime - startTime) / 1000.0;
        return new SearchResult("No Path", bestNodes.size(), Integer.MAX_VALUE, totalTime);
    }
}
