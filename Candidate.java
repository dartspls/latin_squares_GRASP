public class Candidate {
    public int col;
    public int row;
    public int treatment;
    public int cost;

    public Candidate(int col, int row, int treatment) {
        this(col, row, treatment, 0);
    }

    public Candidate(int col, int row, int treatment, int cost) {
        this.col = col;
        this.row = row;
        this.treatment = treatment;
        this.cost = cost;
    }

    public int getCost() {return cost;}

    @Override
    public String toString() {
        return "row: " + row + " col: " + col + " v: " + treatment + ((cost < Integer.MAX_VALUE) ? " cost: " + cost : "");
    }
}
