// Result class to store search outcomes
class SearchResult {
    String solution;
    int nodesCreated;
    double cost;
    double runtime;

    public SearchResult(String solution, int nodesCreated, double cost, double runtime) {
        this.solution = solution;
        this.nodesCreated = nodesCreated;
        this.cost = cost;
        this.runtime = runtime;
    }
}