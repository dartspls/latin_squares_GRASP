import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SolveLatinSquares {
    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
            return;
        }

        String filename;
        int limit;
        double alpha;
        boolean timed;
        try {
            filename = args[0];
            limit = Integer.parseInt(args[1]);
            alpha = Double.parseDouble(args[2]);
            timed = (args[3].equals("t")) ? true : false;

        } catch (Exception e) {
            printUsage();
            return;
        }

        LatinSquare original;
        try {
            File f = new File(filename);
            if (!f.exists()) {
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

        int cpuThreads = Runtime.getRuntime().availableProcessors();
        TO[] threadReturnObjects = new TO[cpuThreads];
        Thread[] threads = new Thread[cpuThreads];
        long start = System.currentTimeMillis();
        for (int i = 0; i < cpuThreads; i++) {
            threadReturnObjects[i] = new TO();
            threads[i] = new GRASP(threadReturnObjects[i], filename, limit, alpha, timed, original);
            threads[i].start();
        }

        
        try {
            // wait for all threads to complete
            for (Thread t : threads) {
                t.join();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        // all threads should have finished
        System.out.println("All threads finished. Time elapsed: " + ((System.currentTimeMillis() - start) / 1000.0f));
        TO bestTo = threadReturnObjects[0];
        int iterationSum = bestTo.iterations;
        for(int i = 1; i < threadReturnObjects.length; i ++) {
            iterationSum += threadReturnObjects[i].totalIteratons;
            if((threadReturnObjects[i].square.getScore() < bestTo.square.getScore()) ||
                (threadReturnObjects[i].square.getScore() == bestTo.square.getScore() && threadReturnObjects[i].iterations < bestTo.iterations)) {
                bestTo = threadReturnObjects[i];
            }
        }

        // output
        double improvementPercent = 1f - (bestTo.square.getScore() / (double)original.getScore());
        System.out.println(bestTo.square);
        System.out.println("Starting score: " + original.getScore());
        System.out.println("Best score: " + bestTo.square.getScore());
        System.out.printf("Improved by: %.2f%s\n", (improvementPercent * 100), "%");
        System.out.println("Iterations to find best result: " + bestTo.iterations);
        System.out.println("Total iterations: " + iterationSum);
        System.out.println("Time taken to find best solution: " + bestTo.timeTaken / 1000.0f);
        System.out.println("Final score: " + bestTo.square.getScore() + " achieved in " + bestTo.iterations + " iterations and " +
        (bestTo.timeTaken < 0 ? 0 : (bestTo.timeTaken / 1000.0f)) + " seconds");

        // GRASP.run(filename, limit, alpha, timed);
    }

    public static void printUsage() {
        System.out.println(
                "Usage: <problem filename> <max iterations/runtime limit in seconds> <alpha (between 0 and 1)> <i: use iterations/t: use seconds>");
    }
}
