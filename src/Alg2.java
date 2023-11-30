import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Alg2 {
    private ArrayList<ArrayList<Integer>> cache;

    public Alg2() { }

    public void removeLargestCycle(ArrayList<ArrayList<Integer>> cycleList, ArrayList<ArrayList<Integer>> costMatrix) {
        if (!createCache(cycleList.size())) //if the cache cannot be created
            return;

        ArrayList<ArrayList<Integer>> sorted = mergeSort(0, cycleList.size()-1, cycleList, costMatrix);

        //System.out.println("Here's the cache:");
        //printCache();

        //remove largest arrays
        int largestCycleHash = Math.abs(sorted.get(0).hashCode());
        int maxCost = getFromCache(largestCycleHash);
        for (ArrayList<Integer> cycle : sorted) {
            int cycleHash = Math.abs(cycle.hashCode());
            int cost = getFromCache(cycleHash);
            if (cost == maxCost)
                removeCycle(cycle, costMatrix);
            else
                break;
        }
    }

    /**
     *
     * @param minSize The minimum size of the cache needs to be.
     * @return If the cache was initialized or not.
     */
    private boolean createCache(int minSize) {
        //prime numbs from 1-1000
        int[] prime_nums = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
                79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
                179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277,
                281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397,
                401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509,
                521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641,
                643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733, 739, 743, 751, 757, 761,
                769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887,
                907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997 };

        if (minSize > prime_nums[prime_nums.length-1]){
            System.out.println("Can't create cache large enough, size: " + minSize);
            return false;
        }

        //find cache size
        for (int i = 0; i < prime_nums.length; i++)
        {
            if (prime_nums[i] > minSize){
                cache = new ArrayList<>();
                for (int j = 0; j < prime_nums[i]; j++) {
                    cache.add(new ArrayList<>(Arrays.asList(-1, -1)));
                }
                break;
            }
        }
        return true;
    }

    private void printCache() {
        for (ArrayList<Integer> subList : cache) {
            for (int i = 0; i < subList.size(); i++) {
                if (i > 0)
                    System.out.print(", ");
                System.out.print(subList.get(i));
            }
            System.out.println();
        }
    }

    private int getFromCache(int hash) {
        int offset = hash % cache.size();
        //System.out.printf("Hash: %d, offset: %d%n", hash, offset);
        for (int i = 0; i < cache.size(); i++) {
            int indx = (i + offset) % cache.size();
            //System.out.printf("indx: %d%n", indx);
            if (cache.get(indx).get(0) == hash) //just in case the hash was 0, check this first
                return cache.get(indx).get(1);
            else if (cache.get(indx).get(0) == -1) //the place where the hash should be has no hash in it
                return -1;
        }

        System.out.println("Woah buddy, I think you may have ran out of space in the cache!! Here's some debug");
        printCache();
        return 0;
    }

    private void placeInCache(int hash, int cost) {
        int offset = hash % cache.size();
        for (int i = 0; i < cache.size(); i++) {
            int indx = (i + offset) % cache.size();
            if (cache.get(indx).get(0) == -1) { //if there's no cost, impossible, it's free
                cache.get(indx).set(0, hash);
                cache.get(indx).set(1, cost);
                //System.out.printf("Placing in spot %d%n", indx);
                return;
            }
        }
        System.out.println("Woah buddy, I think you may have ran out of space in the cache!! Here's some debug");
        printCache();
    }

    private int calculateCost(ArrayList<Integer> cycle, ArrayList<ArrayList<Integer>> costMatrix) {
        int sum = 0;
        //System.out.printf("%n%s%n", cycle.toString());

        for (int i = 0; i < cycle.size() - 1; i++) {
            sum += costMatrix.get(cycle.get(i)).get(cycle.get(i+1));
        }
        return sum;
    }

    /**
     * Compares cycleA's cost to cycleB's cost.
     * @param cycleA
     * @param cycleB
     * @param costMatrix
     * @return If cycleA is larger than cycleB.
     */
    private boolean compareCycleCost(ArrayList<Integer> cycleA, ArrayList<Integer> cycleB,
                                     ArrayList<ArrayList<Integer>> costMatrix) {
        //System.out.printf("%na: %s b: %s%n", cycleList.get(cycleA).toString(), cycleList.get(b).toString());
        int hashA = Math.abs(cycleA.hashCode());
        int hashB = Math.abs(cycleB.hashCode());
        int costA = getFromCache(hashA);
        int costB = getFromCache(hashB);

        if (costA == -1) {
            costA = calculateCost(cycleA, costMatrix);
            placeInCache(hashA, costA);
        }

        if (costB == -1) {
            costB = calculateCost(cycleB, costMatrix);
            placeInCache(hashB, costB);
        }

        return costA > costB;
    }

    /**
     * Sorts cycleList from greatest to smallest cycle cost.
     * @param l
     * @param r
     * @param cycleList
     * @param costMatrix
     * @return A sorted cycleList from greatest to smallest cycle.
     */
    private ArrayList<ArrayList<Integer>> mergeSort(int l, int r, ArrayList<ArrayList<Integer>> cycleList,
                                                    ArrayList<ArrayList<Integer>> costMatrix) {
        if (l == r){
            return new ArrayList<ArrayList<Integer>>(Collections.singletonList(cycleList.get(l)));
        }

        int mid = (l+r)/2;
        //System.out.printf("l:%d, r:%d, mid:%d%n", l, r, mid);

        ArrayList<ArrayList<Integer>> lArr = mergeSort(l, mid, cycleList, costMatrix);
        ArrayList<ArrayList<Integer>> rArr = mergeSort(mid + 1, r, cycleList, costMatrix);
        ArrayList<ArrayList<Integer>> sorted = new ArrayList<>();

        int i=0, j=0, k=0;
        while (i < lArr.size() && j < rArr.size()) {
            if (compareCycleCost(lArr.get(i), rArr.get(j), costMatrix)) {
                sorted.add(lArr.get(i));
                i++;
            }
            else {
                sorted.add(rArr.get(j));
                j++;
            }
            k++;
        }

        //insert any remaining into sorted array
        while (i < lArr.size()){
            sorted.add(lArr.get(i));
            i++;
            k++;
        }
        while (j < rArr.size()){
            sorted.add(rArr.get(j));
            j++;
            k++;
        }

        return sorted;
    }

    private void removeCycle(ArrayList<Integer> cycle, ArrayList<ArrayList<Integer>> costMatrix) {
        for (int i = 0; i < cycle.size(); i++){
            int nextInCycle = (i + 1) % cycle.size();
            costMatrix.get(cycle.get(i)).set(cycle.get(nextInCycle), -1);
        }
    }
}