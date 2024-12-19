import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Ex1 {

    // Search algorithm selection
    private static boolean printTime;

    public static void main(String[] args) {
        try {
            // Read input file
            SearchParameters params = readInputFile("input.txt");

            // Select and run appropriate search algorithm
            SearchAlgorithm algorithm = selectSearchAlgorithm(params);
            SearchResult result = algorithm.search();

            // Write output to file
            writeOutputFile(result.solution,result.nodesCreated,result.cost,result.runtime);
        }
        catch (IOException e) {
            System.err.println("Error processing file: " + e.getMessage());
        }
    }

    // Read input file and parse parameters
    private static SearchParameters readInputFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Parse algorithm
            String algorithmName = reader.readLine().trim();

            // Parse time printing option
            String timeOption = reader.readLine().trim();
            printTime = timeOption.equals("with time");

            // Parse open list printing option
            String openOption = reader.readLine().trim();
            boolean printOpenList = openOption.equals("with open");

            // Read initial board state
            List<String> boardInput = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.equals("Goal state:")) {
                boardInput.add(line.trim());
            }
            BoardState initialState = new BoardState(boardInput);

            // Read goal state
            List<String> goalInput = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                goalInput.add(line.trim());
            }
            BoardState goalState = new BoardState(goalInput);

            return new SearchParameters(algorithmName, initialState, goalState,
                    printTime, printOpenList);
        }
    }


    // Select search algorithm based on input
    private static SearchAlgorithm selectSearchAlgorithm(SearchParameters params) {
        switch (params.algorithmName) {
            case "BFS":
                return new BFSSearch(params.initialState, params.goalState,
                        params.printTime, params.printOpenList);
            case "DFID":
                return new DFIDSearch(params.initialState, params.goalState,
                        params.printTime, params.printOpenList);
            case "A*":
                return new AStarSearch(params.initialState, params.goalState,
                        params.printTime, params.printOpenList);
            case "IDA*":
                return new IDAStarSearch(params.initialState, params.goalState,
                        params.printTime, params.printOpenList);
            case "DFBnB":
                return new DFBnBSearch(params.initialState, params.goalState,
                        params.printTime, params.printOpenList);
            default:
                throw new IllegalArgumentException("Invalid search algorithm: " +
                        params.algorithmName);
        }
    }

    // Method to write output to file
    private static void writeOutputFile(String solution, int nodeCount, double cost, double time) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            // Write solution path or "no path"
            writer.write(solution);
            writer.newLine();

            // Write node count
            writer.write("Num: " + nodeCount);
            writer.newLine();

            // Write solution cost
            writer.write("Cost: " + (solution.equals("no path") ? "inf" : cost));
            writer.newLine();

            // Write runtime if required
            if (printTime) {
                writer.write( "run time: " + time + "");
            }
        }
    }
}

// Helper class to pass search parameters
class SearchParameters {
    String algorithmName;
    BoardState initialState;
    BoardState goalState;
    boolean printTime;
    boolean printOpenList;

    public SearchParameters(String algorithmName, BoardState initialState,
                            BoardState goalState, boolean printTime,
                            boolean printOpenList) {
        this.algorithmName = algorithmName;
        this.initialState = initialState;
        this.goalState = goalState;
        this.printTime = printTime;
        this.printOpenList = printOpenList;
    }
}