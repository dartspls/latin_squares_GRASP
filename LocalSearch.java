import java.util.Iterator;
import java.util.Vector;

public class LocalSearch {

    public static Vector<LatinSquare> generateNeighbourhood(final LatinSquare square) {
        // rows
        //System.out.println("Starting neighbourhood gen");
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
        //System.out.println("Rows done");

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
        //System.out.println("Cols done");
        return n;
    }

    public static LatinSquare localSearch(LatinSquare square) {
        
        LatinSquare bestSquare = square;
        int bestScore = square.getScore();
        boolean improvementMade = true;
        int iterations = 0;
        while(improvementMade && iterations < 5000) {
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
            iterations ++;
            
        }
        //System.out.println("LS iter: "+ iterations);
        return bestSquare;
    }
}
