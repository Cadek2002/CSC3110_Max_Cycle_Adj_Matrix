import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.lang.Math;
import java.util.stream.IntStream;

public class Main {

    public static Vector<Vector<Integer>> readFile(String inputFile) {
        try {
            Scanner matrixScanner = new Scanner(new File(inputFile));
            matrixScanner.useDelimiter(System.getProperty("line.separator"));
            Scanner rowScanner;
            Vector<Vector<Integer>> matrix = new Vector<>();

            //Import Cycle Set into 2d Vector
            while (matrixScanner.hasNextLine()) {
                matrix.add(new Vector<>());
                rowScanner = new Scanner(matrixScanner.next());
                while (rowScanner.hasNextInt()) {
                    matrix.lastElement().add(rowScanner.nextInt());
                }
            }
            matrixScanner.close();
            return matrix;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static void exportFile(Vector<Vector<Integer>> matrix, String outputName) {
        File output = new File(outputName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            for (Vector<Integer> row : matrix) {
                for (Integer i : row) writer.write(String.format("%c ", i+48));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public static Vector<Vector<Integer>> createMatrix(int size, int min, int max, double lambda, String outputName) {
        //initiate matrix
        Vector<Vector<Integer>> output = new Vector<>(size);

        for (int i = 0; i < size; i++) {
            output.add(new Vector<>());
            //populate the list with nulls, setting the diagonals to 0
            for(int j = 0; j < size; j++) output.get(i).add(i == j ? 0 : -1);
        }

        ArrayList<Integer> sequence = new ArrayList<>(IntStream.range(0, size).boxed().toList());
        Collections.shuffle(sequence);
        int next, x, y, element, offset;

        for (int i = 0; i < size; i++) {
            next = ( i+1 < size ? i+1 : 0);
            output.get(sequence.get(i)).set(sequence.get(next), (int)(Math.random() * (max-min) * min));
        }

        int elements = (int)((size * (size-1)) * lambda);

        ArrayList<Integer> sequenceFull = new ArrayList<>(IntStream.range(0, size*(size-1)).boxed().toList());
        Collections.shuffle(sequenceFull);

        while (elements-- != 0) {
            element = sequenceFull.get(elements);
            offset = (element / (size))+1;
            x = (element % size) + offset - ((((element % size) + offset) >= size) ? size : 0);
            y = (element / size) + ((((element % size) + offset) >= size) ? 1 : 0);
            output.get(x).set(y, (int)(Math.random() * (max-min) + min));
        }
        exportFile(output, outputName);
        return output;
    }

    public static void main(String[] args) {
        String matrixFileName = "inputMatrix.txt";
        String setFileName = "inputCycles.txt";
        String outputName = "outputCycles.txt";

        Vector<Vector<Integer>> cycles = readFile(setFileName);
        Vector<Vector<Integer>> adjMatrix = readFile(matrixFileName);

        if (cycles != null && adjMatrix != null) {

            //Create Matrix (Size, min_val, max_val, sparsity, output file name)
            createMatrix(3, 1, 9, 0, "matrix.txt");
            //Create Cycle Set

            Instant start = Instant.now();
            //CODE ALGORITHM HERE variables: cycles, adjMatrix both are Vector<Vector<Integer>>



            //END ALGO CODE
            Instant end = Instant.now();

            System.out.printf("Algo Run Duration: %s Nanoseconds", Duration.between(start, end).getNano());

            //Export set into file
            exportFile(cycles, outputName);
        }
    }
}