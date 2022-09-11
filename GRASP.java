import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GRASP {

    /**
     * Generate all valid candidates for given square
     * @param square
     * @return
     */
    public static Set<Candidate> generateGroundCandidateSet(LatinSquare square) {
        Set<Candidate> candidateSet = new HashSet<>();

        for (int row = 0; row < square.getSize(); row++) {
            Set<Integer> rowSet = square.rowSet(row);

            for (int col = 0; col < square.getSize(); col++) {
                Set<Integer> colSet = square.colSet(col);

                if (square.at(col, row).empty()) {
                    for (int treatment = 1; treatment <= square.getSize(); treatment++) {
                        // only generate valid candidates
                        if (!rowSet.contains(treatment) && !colSet.contains(treatment)) {
                            candidateSet.add(new Candidate(col, row, treatment));
                        }
                    }
                }
            }
        }
        return candidateSet;
    }

    /**
     * Create a list of candidates with incremental score less than or equal to the
     * alpha value
     *
     * @param candidates    Full set of candidates
     * @param computedAlpha Computed alpha value alpha * (max - min)
     * @return Set of candidates that meet the alpha criteria
     */
    public static Set<Candidate> generateRCL(Set<Candidate> candidates, double computedAlpha) {
        Set<Candidate> rcl = new HashSet<>();
        candidates.stream()
                .sorted(Comparator.comparingInt(Candidate::getCost))
                .filter((c) -> c.cost <= computedAlpha)
                .forEach((c) -> rcl.add(c));
        return rcl;
    }

    /**
     * calculate costs for all candidates based on their impact on adding to the
     * square
     *
     * @param candidates set of candidates to calculate costs for
     * @param square     latin square candidates could be added to
     */
    public static void incrementalCosts(Set<Candidate> candidates, LatinSquare square) {
        for (Candidate c : candidates) {
            c.cost = square.countDuplicates(c.row, c.col, c.treatment);
        }
    }

    /**
     * Update costs for candidates in the row/column combo that was modified
     *
     * @param candidates set of candidates to update costs for
     * @param addedC     candidate most recently added to square
     */
    public static void updateCosts(Set<Candidate> candidates, Candidate addedC) {
        candidates
                .stream()
                .filter((c) -> c.treatment == addedC.treatment &&
                        (c.col == addedC.col || c.row == addedC.row))
                .forEach((c) -> c.cost++);
    }

    /**
     * Greedily construct a solution from a partial square
     *
     * @param square partial square to construct from
     * @param alpha  level of greed, 1: full random, 0: full greed
     * @return constructed latin square solution
     */
    public static LatinSquare greedyRandomConstruction(LatinSquare square, double alpha) {
        Random rand = new Random();
        // generate candidate set
        Set<Candidate> allCandidates = generateGroundCandidateSet(square);
        while (!allCandidates.isEmpty()) {
            // compute best and worst incremental costs
            int minCost = Integer.MAX_VALUE;
            int maxCost = 0;
            for (Candidate c : allCandidates) {
                minCost = Math.min(minCost, c.cost);
                maxCost = Math.max(maxCost, c.cost);
            }
            // make restricted candidate list
            double computedAlpha = minCost + (alpha * (maxCost - minCost));
            Set<Candidate> rcl = generateRCL(allCandidates, computedAlpha);

            // select random candidate and update square with it
            Candidate randCandidate = (Candidate) rcl.toArray()[rand.nextInt(rcl.size())];
            square.update(randCandidate);
            allCandidates.remove(randCandidate);

            /*
             * update candidate set, removing all candidates with the same position as the
             * one
             * we just added to the square
             */
            allCandidates = allCandidates
                    .stream()
                    .filter((c) -> c.col != randCandidate.col ||
                            c.row != randCandidate.row)
                    .collect(Collectors.toSet());

            updateCosts(allCandidates, randCandidate);
        }
        return square;
    }

    /**
     * Checks that all elements in the vector have the same counts
     * @param v vector of treatments and counts
     * @return true of all treatments appear equal times
     */
    public static boolean balancedVector(Vector<Pair> v) {
        int lastCount = v.firstElement().count;
        for (Pair p : v) {
            if (p.count != lastCount)
                return false;
            lastCount = p.count;
        }
        return true;
    }

    /**
     * Swap two treatments, minimizes costs
     *
     * @param commonTreatment a common (occurs more than square.size times)
     *                        treatment to be replaced with a rare treatment
     * @param rareTreatment   a rare (occurs less than square.size times) treatment
     *                        to replace a common treatment
     * @param square          latin square we are working on
     */
    public static void replace(Pair commonTreatment, Pair rareTreatment, LatinSquare square) {
        // best position
        int cheapestRow = 0;
        int cheapestCol = 0;
        int cheapestCost = Integer.MAX_VALUE;

        // hacky
        Set<Candidate> temp = new HashSet<>();
        Candidate candidate = new Candidate(0, 0, rareTreatment.treatment);
        temp.add(candidate);

        for (int row = 0; row < square.getSize(); row++) {
            for (int col = 0; col < square.getSize(); col++) {
                // if we find a common treatment in square and it is mutable
                if (square.at(col, row).is(commonTreatment.treatment) && square.at(col, row).mutable) {
                    // set our candidate to this position
                    candidate.row = row;
                    candidate.col = col;
                    // if we replaced it, what would the cost look like?
                    incrementalCosts(temp, square);
                    if (candidate.getCost() < cheapestCost) {
                        cheapestRow = row;
                        cheapestCol = col;
                        cheapestCost = candidate.getCost();
                    }
                }
            }
        }
        // put candidate in best position
        candidate.row = cheapestRow;
        candidate.col = cheapestCol;
        square.update(candidate);
    }

    /**
     * Repair solution if needed
     *
     * @param square square to potentially repair
     * @return feasible square (has n of each treatment, where n is the square size)
     */
    public static LatinSquare repair(LatinSquare square) {
        HashMap<Integer, Integer> treatmentCounts = new HashMap<>();
        if (!square.feasible(treatmentCounts)) {
            // repair
            // order treatments from most to least common
            Vector<Pair> counts = new Vector<>();
            treatmentCounts.forEach((k, v) -> {
                counts.add(new Pair(k, v));
            });
            while (!balancedVector(counts)) {
                replace(counts.firstElement(), counts.lastElement(), square);
                counts.firstElement().count -= 1;
                counts.lastElement().count += 1;
                counts.sort(Comparator.comparingInt(Pair::getCount).reversed());
            }

        }
        return square;
    }

    /**
     * Perform an iteration of GRASP
     * @param square starting solution
     * @param alpha level of greed 1 = full random, 0 = full greedy
     * @return evolution of solution
     */
    public static LatinSquare performIteration(LatinSquare square, double alpha) {
        square = greedyRandomConstruction(square, alpha);
        square = repair(square);
        square = LocalSearch.localSearch(square);
        return square;
    }

    public static void run(String filename, int limit, double alpha, boolean timed) {
        LatinSquare original;
        try {
            File f = new File(filename);
            if(!f.exists()) {
                System.out.println("No file found at: " + filename);
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int size = line.split(",").length;
            String[] csvData = new String[size];
            csvData[0] = line;
            int iter = 1;
            while ((line = reader.readLine()) != null) {
                csvData[iter++] = line;
            }
            reader.close();
            original = new LatinSquare(csvData, size);

        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        int startScore = original.getScore();
        int iterations = 0;
        long taken = System.currentTimeMillis();

        int bestIterations = 0;
        //int bestScore = original.getScore();
        LatinSquare bestSquare = original.cloneSquare();

        if (timed) {
            long end = System.currentTimeMillis() + limit * 1000;
            while (System.currentTimeMillis() < end && bestSquare.getScore() > 0) {
                iterations++;
                LatinSquare square = performIteration(original.cloneSquare(), alpha);

                if(square.getScore() < bestSquare.getScore()) {
                    bestIterations = iterations;
                    bestSquare = square.cloneSquare();
                }
            }
            taken = System.currentTimeMillis() - taken;
        } else {
            while (iterations < limit && bestSquare.getScore() > 0) {
                iterations++;
                LatinSquare square = performIteration(original.cloneSquare(), alpha);

                if(square.getScore() < bestSquare.getScore()) {
                    bestIterations = iterations;
                    bestSquare = square.cloneSquare();
                }
            }
            taken = System.currentTimeMillis() - taken;
        }

        // output
        double improvementPercent = 1f - (bestSquare.getScore() / (double)startScore);

        System.out.println(bestSquare);
        System.out.println("Starting score: " + startScore);
        System.out.println("Best score: " + bestSquare.getScore());
        System.out.printf("Improved by: %.2f%s\n", (improvementPercent * 100), "%");
        System.out.println("Iterations to find best result: " + bestIterations);
        System.out.println("Total iterations: " + iterations);
        System.out.println("Time taken: " + (taken < 0 ? 0 : (taken / 1000.0f)));

        // System.out.println("Final score: " + bestSquare.getScore() + " achieved in " + bestIterations + " iterations and "
        //         + (taken < 0 ? 0 : (taken / 1000.0f)) + " seconds");
        // double improvementPercent = 1f - (original.getScore() / (double)startScore);
        // System.out.printf("Improved by: %.2f%s\n", (improvementPercent * 100), "%");
    }
}
