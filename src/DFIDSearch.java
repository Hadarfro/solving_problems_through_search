import java.util.*;

class DFIDSearch extends SearchAlgorithm {
    public DFIDSearch(BoardState initialState, BoardState goalState,
                      boolean printTime, boolean printOpenList) {
        super(initialState, goalState, printTime, printOpenList);
    }

    @Override
    public SearchResult search() {
        long startTime = System.currentTimeMillis();
        int nodesCreated = 0;

        // Iteratively increase depth limit
        for (int depthLimit = 1; ; depthLimit++) {
            Set<BoardState> path = new HashSet<>();
            nodesCreated = 0;

            // Perform depth-limited DFS
            SearchResult result = limitedDFS(initialState, null, depthLimit, path, 0, nodesCreated);

            // If a solution is found, return it
            if (result != null) {
                result.runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                return result;
            }

            // Print open list if required
            if (printOpenList) {
                System.out.println("Depth Limit " + depthLimit + " completed.");
            }
        }
    }

    private SearchResult limitedDFS(BoardState state, SearchNode parent, int depthLimit,
                                    Set<BoardState> path, int pathCost, int nodeCreated) {
        // Check if depth limit is reached
        if (depthLimit == 0) return null;

        // Print the current node if printOpenList is true
        if (printOpenList) {
            System.out.println(state);
        }

        // Check for goal state
        if (state.isGoalState(goalState)) {
            return new SearchResult(reconstructPath(parent), nodeCreated, pathCost, 0); // runtime updated in main
        }

        // Add current state to path to avoid loops
        path.add(state);

        // Generate successors
        List<SearchNode> successors = generateSuccessors(new SearchNode(state, parent, pathCost, ""));
        nodeCreated += successors.size();

        for (SearchNode successor : successors) {
            // Skip visited states (loop avoidance)
            if (!path.contains(successor.state)) {
                SearchResult result = limitedDFS(successor.state, successor, depthLimit - 1, path,
                        successor.pathCost, nodeCreated);
                if (result != null) return result; // Solution found
            }
        }

        // Backtrack: Remove current state from path
        path.remove(state);
        return null; // No solution found at this depth
    }

    @Override
    protected String reconstructPath(SearchNode goalNode) {
        // Efficient path reconstruction
        Deque<String> pathStack = new ArrayDeque<>();
        SearchNode current = goalNode;

        while (current != null && current.parent != null) {
            pathStack.push(current.path);
            current = current.parent;
        }

        return String.join("-", pathStack);
    }
}
