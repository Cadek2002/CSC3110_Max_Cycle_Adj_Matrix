import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {

    //File Input
    public static ArrayList<ArrayList<Integer>> readFile(String inputFile, ArrayList<Integer> verticesMap) {
        try {
            Scanner matrixScanner = new Scanner(new File(inputFile));
            matrixScanner.useDelimiter(System.getProperty("line.separator"));
            Scanner rowScanner;
            ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();

            //Import Cycle Set into 2d Vector
            while (matrixScanner.hasNextLine()) {
                matrix.add(new ArrayList<>());
                rowScanner = new Scanner(matrixScanner.next());
                while (rowScanner.hasNextInt()) {
                    matrix.get(matrix.size()-1).add(verticesMap == null ? rowScanner.nextInt() : verticesMap.indexOf(rowScanner.nextInt()));
                }
            }
            matrixScanner.close();
            return matrix;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static ArrayList<Integer> readVertices(String inputFile) {
        try {
            Scanner matrixScanner = new Scanner(new File(inputFile));
            ArrayList<Integer> vertices= new ArrayList<>();
            //Import Cycle Set into 2d Vector
            while (matrixScanner.hasNext()) {
                vertices.add(matrixScanner.nextInt());
            }
            matrixScanner.close();
            return vertices;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    //File Output
    public static void exportFile(ArrayList<ArrayList<Integer>> matrix, String outputName, ArrayList<Integer> verticesMap) {
        File output = new File(outputName);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            for (ArrayList<Integer> row : matrix) {
                for (Integer i : row) writer.write(String.format("%c ", verticesMap == null ? i+48 : verticesMap.get(i)));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<ArrayList<Integer>> createMatrix(int size, int min, int max, double lambda, String outputName) {
        //initiate matrix
        ArrayList<ArrayList<Integer>> output = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            output.add(new ArrayList<>());
            //populate the list with nulls, setting the diagonals to 0
            for(int j = 0; j < size; j++) output.get(i).add(i == j ? 0 : -1);
        }

        ArrayList<Integer> sequence = new ArrayList<>(IntStream.range(0, size).boxed().toList());
        Collections.shuffle(sequence);
        int next, x, y, element, offset;

        for (int i = 0; i < size; i++) {
            next = ( i+1 < size ? i+1 : 0);
            output.get(sequence.get(i)).set(sequence.get(next), (int)(Math.random() * (max-min) + min));
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
        exportFile(output, outputName, null);
        return output;
    }
    //Find Cycles Issues
    public static void findCycles(ArrayList<ArrayList<Integer>> adjMatrix, LinkedHashSet<Integer> path, int last, int first, ArrayList<LinkedHashSet<Integer>> set) {
        if (last == first && path.size() > 1 && !set.contains(path)) {
            set.add(path);
            return;
        }
        path.add(last);
        for (int i = 0; i < adjMatrix.size(); i++)
            if (adjMatrix.get(last).get(i) > 0 && (i == first || !path.contains(i))) {
                findCycles(adjMatrix, new LinkedHashSet<>(path), i, first, set);
            }
    }
    public static ArrayList<ArrayList<Integer>> findCycles(ArrayList<ArrayList<Integer>> adjMatrix) {
        ArrayList<LinkedHashSet<Integer>> set = new ArrayList<>();
        for (int i = 0; i < adjMatrix.size(); i++)
            findCycles(adjMatrix, new LinkedHashSet<>(), i, i, set);
        ArrayList<ArrayList<Integer>> fullList = new ArrayList<>();
        for (LinkedHashSet<Integer> cycle: set)
            fullList.add(new ArrayList<>(cycle));

        return fullList;
    }
    // Algorithm 1: Function to remove edges in the largest cycle from the adjacency matrix
    public static void removeMax(ArrayList<ArrayList<Integer>> adjMatrix, ArrayList<ArrayList<Integer>> cycles) {
        int sum, max = 0;
        ArrayList<Integer> costList = new ArrayList<>();
        ArrayList<Integer> maxCycleList = new ArrayList<>();

        // Calculate the sum of each cycle and find the maximum
        for (ArrayList<Integer> cycle : cycles) {
            sum = 0;
            for (int i = 0; i < cycle.size(); i++) {
                int node1 = cycle.get(i);
                int node2 = (i == cycle.size() - 1) ? cycle.get(0) : cycle.get(i + 1);
                sum += adjMatrix.get(node1).get(node2);
            }

            if (sum > max) {
                max = sum;
            }
            costList.add(sum);
        }

        // Identify the cycles with the maximum sum
        for (int i = 0; i < costList.size(); i++) {
            if (costList.get(i) == max) {
                maxCycleList.add(i);
            }
        }

        // Remove edges in the largest cycle from the adjacency matrix
        if (!maxCycleList.isEmpty()) {
            int maxCycleIndex = maxCycleList.get(0);
            ArrayList<Integer> cycle = cycles.get(maxCycleIndex);

            if (maxCycleList.size() == 1) {
                for (int i = 0; i < cycle.size() - 1; i++) {
                    int node = cycle.get(i);
                    int nextNode = cycle.get(i + 1);
                    adjMatrix.get(node).set(nextNode, -1);
                }
            } else {
                for (int i = 1; i < maxCycleList.size(); i++) {
                    int currentMaxCycleIndex = maxCycleList.get(i);
                    ArrayList<Integer> currentCycle = cycles.get(currentMaxCycleIndex);

                    for (int j = 0; j < currentCycle.size() - 1; j++) {
                        int node = currentCycle.get(j);
                        int nextNode = currentCycle.get(j + 1);
                        adjMatrix.get(node).set(nextNode, -1);
                    }
                }
            }
        }
        
    }

    //Algorithm 2
    // Function to calculateCost
    public static int calculateCost(ArrayList<Integer> cycle, ArrayList<ArrayList<Integer>> adjMatrix) {
        int sum = 0;
        for (int i = 0; i < cycle.size() - 1; i++) {
            sum += adjMatrix.get(cycle.get(i)).get(cycle.get(i + 1));
        }
        sum += adjMatrix.get(cycle.get(cycle.size() - 1)).get(cycle.get(0));
        return sum;
    }
    // Function to compareCycles
    public static boolean compareCycles(int x, int y, ArrayList<ArrayList<Integer>> cycleList,
                                        ArrayList<ArrayList<Integer>> m, int[] cache) {
        if (cache[x] == 0) {
            cache[x] = calculateCost(cycleList.get(x), m);
        }

        if (cache[y] == 0) {
            cache[y] = calculateCost(cycleList.get(y), m);
        }

        return cache[x] > cache[y];
    }

    // Function to mergeSort
    public static ArrayList<Integer> mergeSort(ArrayList<Integer> arr, int L, int R, ArrayList<ArrayList<Integer>> cycleList, ArrayList<ArrayList<Integer>> m, int[] cache) {
        if (L == R) {
            ArrayList<Integer> base = new ArrayList<>();
            base.add(L);
            return base;
        }

        int mid = (L + R) / 2;

        ArrayList<Integer> lArr = mergeSort(arr, L, mid, cycleList, m, cache);
        ArrayList<Integer> rArr = mergeSort(arr, mid + 1, R, cycleList, m, cache);

        int i = 0, j = 0, k = 0;

        ArrayList<Integer> sorted = new ArrayList<>(Collections.nCopies(lArr.size() + rArr.size(), 0));

        while (i < lArr.size() && j < rArr.size()) {
            if (compareCycles(lArr.get(i), rArr.get(j), cycleList, m, cache)) {
                sorted.set(k, lArr.get(i));
                i += 1;
            } else {
                sorted.set(k, rArr.get(j));
                j += 1;
            }
            k += 1;
        }

        while (i < lArr.size()) {
            sorted.set(k, lArr.get(i));
            i += 1;
            k += 1;
        }

        while (j < rArr.size()) {
            sorted.set(k, rArr.get(j));
            j += 1;
            k += 1;
        }

        return sorted;
    }

    // Function to removeCycle
    public static void removeCycle(ArrayList<Integer> cycle, ArrayList<ArrayList<Integer>> m) {
        for (int i = 0; i < cycle.size() - 1; i++) {
            m.get(cycle.get(i)).set(cycle.get(i + 1), -1);
        }
        m.get(cycle.get(cycle.size() - 1)).set(cycle.get(0), -1);
    }

    // Function to removeMaxByCost
    public static ArrayList<ArrayList<Integer>> removeMaxByCost(ArrayList<ArrayList<Integer>> m, ArrayList<ArrayList<Integer>> cycleList) {
    int[] cache = new int[cycleList.size()];
    ArrayList<Integer> sorted = mergeSort(new ArrayList<>(IntStream.range(0, cycleList.size()).boxed().toList()), 0, cycleList.size() - 1, cycleList, m, cache);

    int max = cache[sorted.get(0)];
    removeCycle(cycleList.get(sorted.get(0)), m);

    int i = 1;
    while (i < sorted.size() && cache[sorted.get(i)] == max) {
        removeCycle(cycleList.get(sorted.get(i)), m);
        i += 1;
    }

    return m;
}

  

    public static void main(String[] args) {
        String matrixFileName = "inputMatrix.txt";
        String setFileName = "inputCycles.txt";
        String verticesFileName = "vertices.txt";
        String outputName = "outputMatrix.txt";

        ArrayList<Integer> vertices = readVertices(verticesFileName);
        ArrayList<ArrayList<Integer>> cycles = readFile(setFileName, vertices);
        ArrayList<ArrayList<Integer>> adjMatrix = readFile(matrixFileName, null);

    // GENERATES NEW MATRIX/CYCLES, if you want to keep a particular matrix comment these out
    // Create Matrix (Size, min_val, max_val, sparsity, output file name)
        adjMatrix = createMatrix(9, 1, 9, .5, "matrix.txt");
    // Create Cycle Set
        cycles = findCycles(adjMatrix);
        exportFile(cycles, "outCycles.txt", null);

        if (cycles != null && adjMatrix != null) {
            Instant start = Instant.now();

            // Algorithm 1: Call the function to Remove edges in the largest cycle
            removeMax(adjMatrix, cycles);
            // Display the first algorithm's results
            System.out.println("\nAlgorithm 1 Results:");
            System.out.println("Updated Adjacency Matrix:");
            for (ArrayList<Integer> row : adjMatrix) {
                for (Integer i : row) System.out.printf("%d ", i);
                System.out.println();
            }
            System.out.println("\nCycles:");
            for (ArrayList<Integer> cycle : cycles) {
                System.out.println(cycle);
            }

            // Algorithm 2: call the function to Remove edges based on cost comparison
            removeMaxByCost(adjMatrix, cycles);
            // Display the second algorithm's results
            System.out.println("\n\nAlgorithm 2 Results:");
            System.out.println("Updated Adjacency Matrix:");
            for (ArrayList<Integer> row : adjMatrix) {
                for (Integer i : row) System.out.printf("%d ", i);
                System.out.println();
            }
            System.out.println("\nCycles:");
            for (ArrayList<Integer> cycle : cycles) {
                System.out.println(cycle);
                
            }

            // Print the updated adjacency matrix
            // System.out.println("Updated Adjacency Matrix:");
            // for (ArrayList<Integer> row : adjMatrix) {
            //     for (Integer i : row) System.out.printf("%d ", i);
            //     System.out.println();
            // }

            // //System.out.println("Cycles:");
            // for (ArrayList<Integer> cycle : cycles) {
            //     System.out.println(cycle);
            // }

           
            // END ALGO CODE
            Instant end = Instant.now();

            System.out.printf("Algo Run Duration: %s Nanoseconds", Duration.between(start, end).getNano());

            // Export updated matrix into file
            exportFile(adjMatrix, outputName, null);
        }
    }
}