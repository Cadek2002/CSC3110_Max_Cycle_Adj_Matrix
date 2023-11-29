import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

public class Alg2 {
    private int[][] cache;
    public Alg2(){ }

    private void printCache(){
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < cache.length; j++){
                if (j > 0)
                    System.out.print(", ");
                System.out.print(cache[j][i]);
            }
            System.out.println();
        }
    }
    private int getFromCache(int hash)
    {
        int offset = hash % cache.length;
        System.out.printf("Hash: %d, offset: %d%n", hash, offset);
        for (int i = 0; i < cache.length; i++){
            int indx = (i + offset) % cache.length;
            System.out.printf("indx: %d%n", indx);
            if (cache[indx][0] == hash) //just in case the hash was 0, check this first
                return cache[indx][1];
            else if (cache[indx][0] == 0)
                return 0;
        }

        System.out.println("Woah buddy, I think you may have ran out of space in the cache!! Here's some debug");
        printCache();
        return 0;
    }

    private void placeInCache(int hash, int cost){
        int offset = hash % cache.length;
        for (int i = 0; i < cache.length; i++) {
            int indx = (i + offset) % cache.length;
            if (cache[indx][0] == 0) {
                cache[indx][0] = hash;
                cache[indx][1] = cost;
                return;
            }
        }
        System.out.println("Woah buddy, I think you may have ran out of space in the cache!! Here's some debug");
        printCache();
    }

    private int calculateCost(int[] cycle, int[][] costMatrix) {
        int sum = 0;
        System.out.printf("%n%s%n", Arrays.toString(cycle));
        /*for (int i = 0; i < costMatrix.length; i++)
        {
            System.out.println(Arrays.toString(costMatrix[i]));
        }*/

        for (int i = 0; i < cycle.length - 1; i++) {
            sum += costMatrix[cycle[i]][cycle[i+1]];
        }
        return sum;
    }

    private boolean compareCycleCost(int a, int b, int[][] cycleList, int[][] costMatrix) {
        System.out.printf("%na: %s b: %s%n", Arrays.toString(cycleList[a]), Arrays.toString(cycleList[b]));
        int hashA = Arrays.hashCode(cycleList[a]);
        int hashB = Arrays.hashCode(cycleList[b]);
        int costA = getFromCache(hashA);
        int costB = getFromCache(hashB);

        if (costA == 0) {
            costA = calculateCost(cycleList[a], costMatrix);
            placeInCache(hashA, costA);
        }

        if (costB == 0) {
            costB = calculateCost(cycleList[a], costMatrix);
            placeInCache(hashB, costB);
        }

        return costA > costB;
    }

    private int[][] mergeSort(int l, int r, int[][] cycleList, int[][] costMatrix) {
        if (l == r){
            return new int[][] {cycleList[l]};
        }

        int mid = (l+r)/2;
        System.out.printf("l:%d, r:%d, mid:%d%n", l, r, mid);

        int[][] lArr = mergeSort(l, mid, cycleList, costMatrix);
        int[][] rArr = mergeSort(mid + 1, r, cycleList, costMatrix);
        int[][] sorted = new int[lArr.length + rArr.length][];

        int i=0, j=0, k=0;
        while (i < lArr.length && j < rArr.length) {
            if (compareCycleCost(i, j, cycleList, costMatrix)) {
                sorted[k] = lArr[i];
                i++;
            }
            else {
                sorted[k] = rArr[j];
                j++;
            }
            k++;
        }

        //insert any remaining into sorted array
        while (i < lArr.length){
            sorted[k] = lArr[i];
            i++;
            k++;
        }
        while (j < lArr.length){
            sorted[k] = lArr[j];
            j++;
            k++;
        }

        return sorted;
    }

    private void removeCycle(int[] cycle, int[][] costMatrix) {
        for (int i = 0; i < cycle.length; i++){
            int nextInCycle = i + 1 % cycle.length;
            costMatrix[cycle[i]][cycle[nextInCycle]] = -1;
        }
    }

    public void removeLargestCycle(int[][] cycleList, int[][] costMatrix) {
        //prime numbs from 1-200
        int prime_nums[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
                79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173,
                179, 181, 191, 193, 197, 199 };

        int minSize = cycleList.length;
        if (minSize > prime_nums[prime_nums.length-1]){
            System.out.println("Can't create cache large enough, size: " + minSize);
            return;
        }

        //find cache size
        for (int i = 0; i < prime_nums.length; i++)
        {
            if (prime_nums[i] > minSize){
                cache = new int[prime_nums[i]][2];
                break;
            }
        }

        int[][] sorted = mergeSort(0, cycleList.length, cycleList, costMatrix);

        //remove largest arrays
        int largestCycleHash = Arrays.hashCode(sorted[sorted.length-1]);
        int maxCost = getFromCache(largestCycleHash);
        for (int i = sorted.length-1; i >=0; i--) {
            int cycleHash = Arrays.hashCode(sorted[i]);
            int cost = getFromCache(cycleHash);
            if (cost == maxCost)
                removeCycle(sorted[i], costMatrix);
            else
                break;
        }
    }
}
