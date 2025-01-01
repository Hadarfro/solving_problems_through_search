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
            nodesCreated = 0;
            Set<BoardState> visited = new HashSet<>(); // Create hash table H

            // Perform depth-limited DFS
            SearchResult result = limitedDFS(new SearchNode(initialState, null, 0, ""), depthLimit, visited, nodesCreated);

            // If a solution is found, return it
            if (result != null) {
                result.runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                return result;
            }

            if (printOpenList) {
                System.out.println("Depth Limit " + depthLimit + " completed.");
            }
        }
    }

    private SearchResult limitedDFS(SearchNode node, int depthLimit, Set<BoardState> visited, int nodesCreated) {
        BoardState state = node.state;

        // If goal is found, return path
        if (state.isGoalState(goalState)) {
            return new SearchResult(reconstructPath(node), nodesCreated, node.pathCost, 0); // runtime updated in main
        }

        // If limit is 0, return cutoff
        if (depthLimit == 0) {
            return null; // Represents cutoff
        }

        // Insert the current state into the hash table
        visited.add(state);

        boolean isCutoff = false;

        // Generate successors
        List<SearchNode> successors = generateSuccessors(node);
        nodesCreated += successors.size();

        for (SearchNode successor : successors) {
            // If successor is already in the hash table, skip it
            if (visited.contains(successor.state)) {
                continue;
            }

            // Recursive call with reduced depth limit
            SearchResult result = limitedDFS(successor, depthLimit - 1, visited, nodesCreated);

            if (result == null) {
                isCutoff = true;
            } else if (result != null) {
                return result; // Solution found
            }
        }

        // Remove the current state from the hash table (release memory for `n`)
        visited.remove(state);

        // Return cutoff if necessary
        if (isCutoff) {
            return null; // Represents cutoff
        }

        return null; // Represents failure
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
