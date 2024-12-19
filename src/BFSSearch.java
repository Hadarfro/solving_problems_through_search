import java.util.*;

class BFSSearch extends SearchAlgorithm {
    public BFSSearch(BoardState initialState, BoardState goalState, boolean printTime, boolean printOpenList) {
        super(initialState, goalState, printTime, printOpenList);
    }

    @Override
    public SearchResult search() {
        long startTime = System.currentTimeMillis();
        int nodesCreated = 1; // Count initial state

        // Use HashSet for closed list to check visited states efficiently
        Set<String> closedList = new HashSet<>();

        // Use Queue and HashSet for BFS
        Queue<SearchNode> openList = new LinkedList<>();
        Set<String> openListSet = new HashSet<>(); // Tracks states in openList

        // Create initial search node
        SearchNode initialNode = new SearchNode(initialState, null, 0, "");
        openList.add(initialNode);
        openListSet.add(boardToString(initialState));

        // Optional: Print initial open list if required
        if (printOpenList) {
            printOpenListContents(openList);
        }

        while (!openList.isEmpty()) {
            SearchNode currentNode = openList.poll();
            String boardKey = boardToString(currentNode.state);
            openListSet.remove(boardKey);
            nodesCreated++;

            // Skip if already visited
            if (closedList.contains(boardKey)) {
                continue;
            }

            // Mark as visited
            closedList.add(boardKey);

            // Check if goal state reached
            if (currentNode.state.isGoalState(goalState)) {
                long endTime = System.currentTimeMillis();
                return new SearchResult(
                        reconstructPath(currentNode),
                        nodesCreated,
                        calculatePathCost(currentNode),
                        (endTime - startTime) / 1000.0
                );
            }

            // Generate possible moves
            List<SearchNode> successors = generateSuccessors(currentNode);

            for (SearchNode successor : successors) {
                String successorKey = boardToString(successor.state);

                // Only add to open list if not in closed list or open list
                if (!closedList.contains(successorKey) && !openListSet.contains(successorKey)) {
                    openList.add(successor);
                    openListSet.add(successorKey);

                }
            }

            // Optional: Print open list if required
            if (printOpenList) {
                printOpenListContents(openList);
            }
        }

        // No path found
        long endTime = System.currentTimeMillis();
        return new SearchResult(
                "no path",
                nodesCreated,
                Double.POSITIVE_INFINITY,
                (endTime - startTime) / 1000.0
        );
    }
}
