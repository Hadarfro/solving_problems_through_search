import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// Board state representation
class BoardState {
    char[][] board;
    int rows = 3;
    int cols = 3;
    int trackEmptyRow; // Track the empty tile row
    int trackEmptyCol; // Track the empty tile column

    // Constructor to initialize board from input
    public BoardState(List<String> boardInput) {
        board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            String[] rowValues = boardInput.get(i).split(",");
            for (int j = 0; j < cols; j++) {
                board[i][j] = rowValues[j].charAt(0);
                if (board[i][j] == '_') {
                    trackEmptyRow = i; // Store row index of empty tile
                    trackEmptyCol = j; // Store column index of empty tile
                }
            }
        }
    }

    // Deep copy constructor
    public BoardState(BoardState original) {
        this.board = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.board[i][j] = original.board[i][j];
            }
        }
        this.trackEmptyRow = original.trackEmptyRow;
        this.trackEmptyCol = original.trackEmptyCol;
    }

    // Method to check if current state matches goal state
    public boolean isGoalState(BoardState goalState) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (this.board[i][j] != goalState.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Board State:\n");
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        // Check if comparing the object to itself
        if (this == obj) {
            return true;
        }

        // Check if the object is null or not of the same class
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        // Cast the other object to BoardState
        BoardState otherBoard = (BoardState) obj;

        // Compare the two boards using deep equality
        if (!Arrays.deepEquals(this.board, otherBoard.board)) {
            return false;
        }

        return true;
    }

}
