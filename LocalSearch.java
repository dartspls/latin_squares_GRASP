import java.util.Iterator;
import java.util.Vector;

public class LocalSearch {

    /**
     * Generate a neighbourhood of solutions from an initial solution.
     * Neighbourhood structure is: each item swapped with each other item
     * in each row and column.
     * 
     * @param square initial solution
     * @return vector of neighbouring solutions
     */
    public static Vector<LatinSquare> generateNeighbourhood(final LatinSquare square) {
        // rows
        Vector<LatinSquare> n = new Vector<>();
        for (int row = 0; row < square.getSize(); row ++) {
            for(int i = 0; i < square.getSize(); i ++) {
                if(!square.at(i, row).mutable) continue;
                for(int j = i + 1; j < square.getSize(); j ++) {
                    if(!square.at(j, row).mutable) continue;
                    LatinSquare ns = square.cloneSquare();
                    ns.swap(row, i, row, j);
                    n.add(ns);
                }
            }
        }

        // cols
        for (int col = 0; col < square.getSize(); col ++){
            for(int i = 0; i < square.getSize(); i ++) {
                if(!square.at(col, i).mutable) continue;
                for(int j = i + 1; j < square.getSize(); j ++) {
                    if(!square.at(col, j).mutable) continue;
                    LatinSquare ns = square.cloneSquare();
                    ns.swap(i, col, j, col);
                    n.add(ns);
                }
            }
        }
        return n;
    }

    /**
     * Perform local search on a solution. Stops when local optima is found (no improvements)
     * @param square square to use as initial solution
     * @return local optima
     */
    public static LatinSquare localSearch(LatinSquare square) {
        
        LatinSquare bestSquare = square;
        int bestScore = square.getScore();
        boolean improvementMade = true;
        while(improvementMade) {
            improvementMade = false;
            Vector<LatinSquare> neighbourhood = generateNeighbourhood(bestSquare);
            Iterator<LatinSquare> iter = neighbourhood.iterator();
            while(iter.hasNext()) {
                LatinSquare evaluating = iter.next();
                int evalScore = evaluating.getScore();
                if(evalScore < bestScore) {
                    bestScore = evalScore;
                    bestSquare = evaluating;
                    improvementMade = true;
                }
            }
        }
        return bestSquare;
    }
}
