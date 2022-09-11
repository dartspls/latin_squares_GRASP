import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * LatinSquare
 */
public class LatinSquare {

    private int size;
    private SquareElement[][] treatments;

    public LatinSquare(int size) {
        this.size = size;
        treatments = new SquareElement[size][size];
    }

    public LatinSquare(String[] csvData, int size) {
        this.size = size;
        treatments = new SquareElement[size][size];
        buildFromCsv(csvData);
    }

    public int getSize() {
        return size;
    }

    public SquareElement at(int col, int row) {
        return treatments[row][col];
    }

    /**
     * Builds a latin square out of an array of comma seperated values
     * 
     * @param csvData array of comma seperated lines of equal length
     */
    private void buildFromCsv(String[] csvData) {
        String[] lineItems;
        for (int i = 0; i < csvData.length; i++) {

            lineItems = csvData[i].split(",");
            for (int j = 0; j < lineItems.length; j++) {
                treatments[i][j] = new SquareElement(Integer.parseInt(lineItems[j]), false);
            }
        }
    }

    /**
     * Write square contents to a file, in csv format
     * 
     * @param fileName name of file to write to. overwrites contents if file exists
     */
    public void writeToFile(String fileName) {
        File outf = new File(fileName);
        String[] lines = toCsv();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outf));
            int count = 0;
            for (String line : lines) {
                writer.write(line);
                if (count < size - 1)
                    writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Integer> rowSet(int row) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            if (!treatments[row][i].empty()) {
                set.add(treatments[row][i].value);
            }
        }
        return set;
    }

    public Set<Integer> colSet(int col) {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < size; i++) {
            if (!treatments[i][col].empty()) {
                set.add(treatments[i][col].value);
            }
        }
        return set;
    }

    private int countDuplicatesRow(int row, int treatment) {
        int dupes = 0;
        for (int i = 0; i < size; i++) {
            if (treatments[row][i].is(treatment))
                dupes++;
        }
        return dupes;
    }

    private int countDuplicatesCol(int col, int treatment) {
        int dupes = 0;
        for (int i = 0; i < size; i++) {
            if (treatments[i][col].is(treatment))
                dupes++;
        }
        return dupes;
    }

    public int countDuplicates(int row, int col, int treatment) {
        return countDuplicatesRow(row, treatment) + countDuplicatesCol(col, treatment);
    }

    public void update(Candidate c) {
        treatments[c.row][c.col] = new SquareElement(c);
    }

    public String[] toCsv() {
        String[] lines = new String[size];
        for (int iter = 0; iter < size; iter++) {
            String line = "";
            for (SquareElement i : treatments[iter]) {
                line += i.value + ",";
            }
            lines[iter] = line.substring(0, line.length() - 1);
        }
        return lines;
    }

    public LatinSquare cloneSquare() {
        // LatinSquare clone = new LatinSquare(this.toCsv(), size); // won't work because need to protect mutability
        LatinSquare clone = new LatinSquare(size);
        for(int row = 0; row < size; row ++) {
            for (int col = 0; col < size; col ++) {
                int value = treatments[row][col].value;
                boolean mutable = treatments[row][col].mutable;
                clone.treatments[row][col] = new SquareElement(value, mutable);
            }
        }
        return clone;
    }

    /**
     * A square is feasable if each treatment appears N times in an NxN square.
     * A feasable square does not necessarily have a score of 0.
     * 
     * @treatmentCounts array to fill with counts of each treatment. Can be null.
     * @return true if each treatment is found N many times in the square, where N =
     *         size of square.
     */
    public boolean feasible(HashMap<Integer, Integer> treatmentCounts) {
        if (treatmentCounts == null) {
            treatmentCounts = new HashMap<>();
        }
        for (SquareElement[] row : treatments) {
            for (SquareElement e : row) {
                if (!e.empty()) {
                    if (treatmentCounts.containsKey(e.value)) {
                        treatmentCounts.put(e.value, treatmentCounts.get(e.value) + 1);
                    } else {
                        treatmentCounts.put(e.value, 1);
                    }
                }
            }
        }

        for (int key : treatmentCounts.keySet()) {
            if (treatmentCounts.get(key) != size)
                return false;
        }
        return true;
        // return getScore() == 0;
    }

    public int getScore() {
        int score = 0;
        for (SquareElement[] row : treatments) {
            Set<Integer> presentTreatments = new HashSet<>();
            for (SquareElement e : row) {
                if (!e.empty()) {
                    presentTreatments.add(e.value);
                }
            }
            score += size - presentTreatments.size();
        }

        for (int col = 0; col < size; col++) {
            Set<Integer> presentTreatments = new HashSet<>();
            for (int row = 0; row < size; row++) {
                if (!treatments[row][col].empty()) {
                    presentTreatments.add(treatments[row][col].value);
                }
            }
            score += size - presentTreatments.size();
        }
        return score;
    }

    public void swap(int row1, int col1, int row2, int col2) {
        SquareElement temp = treatments[row1][col1];
        treatments[row1][col1] = treatments[row2][col2];
        treatments[row2][col2] = temp;
    }

    @Override
    public String toString() {
        String result = "";

        for (SquareElement[] row : treatments) {
            if (!result.equals(""))
                result += "\n";
            for (SquareElement e : row) {
                result += e.getStrVal() + " ";
            }
        }

        return result;
    }
}
