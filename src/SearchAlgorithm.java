import java.util.*;

// Abstract base class for search algorithms
abstract class SearchAlgorithm {
    protected BoardState initialState;
    protected BoardState goalState;
    protected boolean printTime;
    protected boolean printOpenList;

    public SearchAlgorithm(BoardState initialState, BoardState goalState, boolean printTime, boolean printOpenList) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.printTime = printTime;
        this.printOpenList = printOpenList;
    }

    // Abstract method to be implemented by each specific algorithm
    public abstract SearchResult search();

    // Inner class to represent search nodes
    protected class SearchNode {
        BoardState state;       // The current board state
        SearchNode parent;      // Reference to the parent node
        int pathCost;        // g(n): Cost to reach this node from the start
        int heuristicCost;   // h(n): Estimated cost to reach the goal
        String path;            // The move/action that led to this state
        List<SearchNode> successors;
        boolean markedOut;

        // Constructor with heuristic
        SearchNode(BoardState state, SearchNode parent, int pathCost, String path) {
            this.state = state;
            this.parent = parent;
            this.pathCost = pathCost;
            this.heuristicCost = heuristic(state);
            this.path = path;
            this.successors = new ArrayList<>();
            this.markedOut = false;
        }

        // Total cost: f(n) = g(n) + h(n)
        public int getTotalCost() {
            return pathCost + heuristicCost;
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(state.board);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BoardState other = (BoardState) obj;
            return Arrays.deepEquals(state.board, other.board);
        }


        @Override
        public String toString() {
            return state.toString() +
                    "Path Cost (g): " + pathCost + "\n" +
                    "Heuristic Cost (h): " + heuristicCost + "\n" +
                    "Total Cost (f): " + getTotalCost() + "\n";
        }

        protected boolean isMarkedOut() {
            return markedOut;
        }

        protected void setMarkedOut(boolean markedOut) {
            this.markedOut = markedOut;
        }

        public void getSuccessorsStates() {
            // Preallocate list with a reasonable initial capacity
            List<SearchNode> successors = new ArrayList<>();
            BoardState currentState = this.state;

            // Cache frequently used values to reduce repeated access
            char[][] board = currentState.board;
            int rows = currentState.rows;
            int cols = currentState.cols;

            // Preallocate directions array to avoid repeated allocation
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

            // Reusable StringBuilder for move descriptions to reduce object creation
            StringBuilder moveDescriptionBuilder = new StringBuilder(20);

            // Iterate through all cells to find movable balls
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char ball = board[i][j];

                    // Quick bitwise check for skipping empty or black cells
                    if (ball == '_' || ball == 'X') {
                        continue;
                    }

                    // Process each direction
                    for (int[] dir : directions) {
                        // Use bitwise modulo for potentially faster calculation
                        int newRow = (i + dir[0] + rows) % rows;
                        int newCol = (j + dir[1] + cols) % cols;

                        // Check if destination is empty
                        if (board[newRow][newCol] == '_') {
                            // Create new board state with move
                            BoardState newState = new BoardState(currentState);

                            // Swap ball positions
                            newState.board[newRow][newCol] = ball;
                            newState.board[i][j] = '_';

                            // Calculate move cost based on ball color (using lookup table for potential further optimization)
                            int moveCost = calculateMoveCostBall(ball);

                            // Efficiently construct move description
                            moveDescriptionBuilder.setLength(0);
                            moveDescriptionBuilder
                                    .append('(').append(i + 1).append(',').append(j + 1)
                                    .append("):").append(ball)
                                    .append(":(").append(newRow + 1).append(',').append(newCol + 1)
                                    .append(')');

                            // Efficiently construct path
                            String newPath = this.path.isEmpty()
                                    ? moveDescriptionBuilder.toString()
                                    : this.path + "--" + moveDescriptionBuilder.toString();

                            // Create successor node
                            SearchNode successor = new SearchNode(newState, this, this.pathCost + moveCost, newPath);

                            successors.add(successor);
                        }
                    }
                }
            }

            // Sort the successors list by total cost (pathCost + heuristicCost)
            successors.sort(Comparator.comparingInt(SearchNode::getTotalCost));
            this.successors = successors;
        }
    }

    // Calculate move cost based on ball color
    protected int calculateMoveCost(SearchNode currentNode,SearchNode  successor) {
        // Identify the color of the marble being moved
        char ball = identifyMovedMarble(currentNode.state, successor.state);

        switch (ball) {
            case 'B': return 1;  // Blue ball
            case 'G': return 3;  // Green ball
            case 'R': return 10; // Red ball
            default: return 0;
        }
    }

    protected char identifyMovedMarble(BoardState from, BoardState to) {
        for (int i = 0; i < from.board.length; i++) {
            for (int j = 0; j < from.board[i].length; j++) {
                if (from.board[i][j] != to.board[i][j]) {
                    return from.board[i][j] != '_' ? from.board[i][j] : to.board[i][j];
                }
            }
        }
        return '_';
    }

    // Reconstruct path from goal node to initial state
    protected String reconstructPath(SearchNode goalNode) {
        return goalNode.path;
    }

    // Calculate total path cost
    protected double calculatePathCost(SearchNode goalNode) {
        return goalNode.pathCost;
    }

    // Convert board state to string for efficient comparison
    protected String boardToString(BoardState state) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < state.rows; i++) {
            for (int j = 0; j < state.cols; j++) {
                sb.append(state.board[i][j]);
            }
        }
        return sb.toString();
    }

    // Print open list contents for debugging
    protected void printOpenListContents(Queue<SearchNode> openList) {
        System.out.println("Open List Contents:");
        for (SearchNode node : openList) {
            System.out.println("Board State:");
            for (int i = 0; i < node.state.rows; i++) {
                for (int j = 0; j < node.state.cols; j++) {
                    System.out.print(node.state.board[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println("Path Cost: " + node.pathCost);
            System.out.println("---");
        }
    }

    // Generate all possible successor states
    protected List<SearchNode> generateSuccessors(SearchNode currentNode) {
        // Preallocate list with a reasonable initial capacity
        List<SearchNode> successors = new ArrayList<>();
        BoardState currentState = currentNode.state;

        // Cache frequently used values to reduce repeated access
        char[][] board = currentState.board;
        int rows = currentState.rows;
        int cols = currentState.cols;

        // Preallocate directions array to avoid repeated allocation
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        // Reusable StringBuilder for move descriptions to reduce object creation
        StringBuilder moveDescriptionBuilder = new StringBuilder(20);

        // Iterate through all cells to find movable balls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char ball = board[i][j];

                // Quick bitwise check for skipping empty or black cells
                if ((ball == '_') | (ball == 'X')) continue;

                // Process each direction
                for (int[] dir : directions) {
                    // Use bitwise modulo for potentially faster calculation
                    int newRow = (i + dir[0] + rows) % rows;
                    int newCol = (j + dir[1] + cols) % cols;

                    // Check if destination is empty
                    if (board[newRow][newCol] == '_') {
                        // Create new board state with move
                        BoardState newState = new BoardState(currentState);

                        // Swap ball positions
                        newState.board[newRow][newCol] = ball;
                        newState.board[i][j] = '_';

                        // Calculate move cost based on ball color (using lookup table for potential further optimization)
                        int moveCost = calculateMoveCostBall(ball);

                        // Efficiently construct move description
                        moveDescriptionBuilder.setLength(0);
                        moveDescriptionBuilder
                                .append('(').append(i + 1).append(',').append(j + 1)
                                .append("):").append(ball)
                                .append(":(").append(newRow + 1).append(',').append(newCol + 1)
                                .append(')');

                        // Efficiently construct path
                        String newPath = currentNode.path.isEmpty()
                                ? moveDescriptionBuilder.toString()
                                : currentNode.path + "--" + moveDescriptionBuilder.toString();

                        // Create successor node
                        SearchNode successor = new SearchNode(
                                newState,
                                currentNode,
                                currentNode.pathCost + moveCost,
                                newPath);

                        successors.add(successor);
                    }
                }
            }
        }
        return successors;
    }

    // Optional: Optimized move cost calculation using a lookup table
    private static final int[] MOVE_COST_LOOKUP = new int[128];
    static {
        MOVE_COST_LOOKUP['B'] = 1;  // Blue ball
        MOVE_COST_LOOKUP['G'] = 3;  // Green ball
        MOVE_COST_LOOKUP['R'] = 10; // Red ball
    }

    protected int calculateMoveCostBall(char ball) {
        return MOVE_COST_LOOKUP[ball];
    }

    protected int heuristic(BoardState currentState) {
        int totalDistance = 0;

        if (currentState.equals(goalState)) {
            return 0;
        }

        // Pre-compute goal positions for efficient lookup
        Map<Character, List<int[]>> goalPositions = precomputeGoalPositions(goalState.board);

        int rows = currentState.board.length;
        int cols = currentState.board[0].length;

        for (int i = 0; i < currentState.board.length; i++) {
            for (int j = 0; j < currentState.board[i].length; j++) {
                char ball = currentState.board[i][j];
                if (ball != '_' && ball != 'X') {
                    // Find the minimum distance to any goal for this ball
                    int minDistance = Integer.MAX_VALUE;
                    for (int[] goal : goalPositions.getOrDefault(ball, Collections.emptyList())) {
                        int distance = calculateCircularManhattanDistance(i, j, goal[0], goal[1], rows, cols);
                        minDistance = Math.min(minDistance, distance);
                    }

                    // Multiply the distance by the ball's move cost
                    int cost = calculateMoveCostBall(ball);
                    totalDistance += (minDistance * cost);
                }
            }
        }

        return totalDistance;
    }

    // Circular Manhattan distance calculation
    protected int calculateCircularManhattanDistance(int x1, int y1, int x2, int y2, int rows, int cols) {
        // Calculate vertical and horizontal distances, considering wrapping
        int verticalDistance = Math.min(Math.abs(x1 - x2), rows - Math.abs(x1 - x2));
        int horizontalDistance = Math.min(Math.abs(y1 - y2), cols - Math.abs(y1 - y2));
        return verticalDistance + horizontalDistance;
    }

    // Precompute goal positions for faster lookup
    protected Map<Character, List<int[]>> precomputeGoalPositions(char[][] goalBoard) {
        Map<Character, List<int[]>> goalPositions = new HashMap<>();

        for (int i = 0; i < goalBoard.length; i++) {
            for (int j = 0; j < goalBoard[i].length; j++) {
                char ball = goalBoard[i][j];
                if (ball != '_' && ball != 'X') {
                    goalPositions.computeIfAbsent(ball, k -> new ArrayList<>()).add(new int[]{i, j});
                }
            }
        }

        return goalPositions;
    }


}