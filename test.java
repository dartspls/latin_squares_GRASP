import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class test {
    public static void main(String[] args) {
        String filename = args[0];
        // int size = Integer.parseInt(args[1]);
        try {
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
            LatinSquare ls = new LatinSquare(csvData, size);
            System.out.println("Score: " + ls.getScore());
            // ls.writeToFile("output.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // String[] data = {
        // "1,3,0,0",
        // "0,4,3,1",
        // "3,1,0,0",
        // "0,2,1,0"
        // };
        // LatinSquare sq = new LatinSquare(data, 4);
        // System.out.println(sq);
        // System.out.println("Score of incomplete square: " + sq.getScore());

        // System.out.println("Feasible: " + sq.feasible(null));

        // testRow();
        // testCol();
        // testCandidateGroundSetGeneration();
        // testFeasible();
        // System.out.println("Output of greedy random construction: ");
        // // for(int i = 0; i < 30; i ++) {
        // sq = GRASP.greedyRandomConstruction(sq, 0.5);
        // // }

        // System.out.println("Result of greedy square: ");
        // System.out.println(sq);
        // System.out.println("Score of resulting square: " + sq.getScore());
        // System.out.println("Feasibility of resulting square: " + sq.feasible(null));
        // if (!sq.feasible(null)) {
        // GRASP.repair(sq);
        // }

        //test2();

    }

    public static void test2() {
        String[] data = {
                "6,7,4,9,3,1,2,10,8,5",
                "10,5,9,6,8,4,1,3,7,2",

                "5,6,7,3,2,8,10,1,4,9",

                "7,2,8,10,4,5,1,9,6,3",

                "1,9,3,2,6,5,7,4,6,10",

                "2,1,10,4,5,7,3,8,9,6",

                "5,3,2,8,9,10,6,7,1,4",

                "4,8,5,7,10,3,9,2,6,1",

                "3,10,6,7,1,9,4,5,2,8",

                "9,4,1,3,8,2,10,6,5,7"
        };
        LatinSquare ls = new LatinSquare(data, 10);
        System.out.println("10by10 score: " + ls.getScore());
    }

    public static void testFeasible() {
        String[] data = {
                "1,3,4,4",
                "2,4,3,1",
                "3,1,2,2",
                "4,2,1,3"
        };
        LatinSquare sq = new LatinSquare(data, 4);
        final int expectedScore = 2;
        if (sq.feasible(null) && expectedScore == sq.getScore()) {
            System.out.println("Feasible test passed");
        } else {
            System.out.println("Feasible test failed");
        }
    }

    public static void testCandidateGroundSetGeneration() {
        String[] data = {
                "1,3,0,4",
                "2,4,3,1",
                "3,1,4,2",
                "0,2,1,3"
        };
        LatinSquare sq = new LatinSquare(data, 4);
        Set<Candidate> candidates = GRASP.generateGroundCandidateSet(sq);
        // System.out.println(candidates.size());
        // candidates.forEach(System.out::println);
        Object[] c = candidates.toArray();

        boolean num = c.length == 2;
        boolean fst = false;
        ;
        boolean snd = false;
        ;
        for (Object o : c) {
            Candidate co = (Candidate) o;
            if (co.treatment == 2) {
                fst = (co.treatment == 2 && co.row == 0 && co.col == 2);
            } else if (co.treatment == 4) {
                snd = (co.treatment == 4 && co.row == 3 && co.col == 0);
            }
        }

        if (num && fst && snd)
            System.out.println("CandidateGroundSetGen passed");
        else {
            System.out.println("CandidateGroundSetGen failed");
            System.out.println(
                    "Expected size of 2, got: " + c.length + "\n" +
                            "Expected first element to be: row: 0 col: 2 v: 2\n" +
                            "                          Go: " + c[0].toString() + "\n" +
                            "Expected second element to be: row: 3 col: 0 v: 4\n" +
                            "                          got: " + c[1].toString());
        }
    }

    public static void testRow() {
        String[] data = {
                "1,3,0,4",
                "2,4,3,1",
                "3,1,4,2",
                "0,2,1,3"
        };

        LatinSquare sq = new LatinSquare(data, 4);
        Set<Integer> exampleRow = new HashSet<>();
        exampleRow.add(1);
        exampleRow.add(3);
        exampleRow.add(4);

        Set<Integer> sqRow = sq.rowSet(0);

        boolean rows = sqRow.equals(exampleRow);

        if (rows)
            System.out.println("Test rows passed");
        else
            System.out.println("Test rows failed");
    }

    public static void testCol() {
        String[] data = {
                "1,3,0,4",
                "2,4,3,1",
                "3,1,4,2",
                "0,2,1,3"
        };

        LatinSquare sq = new LatinSquare(data, 4);
        Set<Integer> exampleCol = new HashSet<>();
        exampleCol.add(1);
        exampleCol.add(2);
        exampleCol.add(3);
        Set<Integer> sqCol = sq.colSet(0);
        boolean cols = sqCol.equals(exampleCol);
        if (cols)
            System.out.println("Test cols passed");
        else
            System.out.println("Test cols failed");
    }
}
