public class SolveLatinSquares {
    public static void main(String[] args) {
        if(args.length < 4) {
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

        GRASP.run(filename, limit, alpha, timed);
    }

    public static void printUsage() {
        System.out.println("Usage: <problem filename> <max iterations/runtime limit in seconds> <alpha (between 0 and 1)> <i: use iterations/t: use seconds>");
    }
}
