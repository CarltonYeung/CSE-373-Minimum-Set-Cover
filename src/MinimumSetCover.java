import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MinimumSetCover {
    final private static String path = "http://www3.cs.stonybrook.edu/~skiena/373/setcover/";
    final private static String[] file = {
            "s-X-12-6",        // 0
            "s-k-100-175",     // 1
            "s-k-150-225",     // 2
            "s-k-150-250",     // 3
            "s-k-20-30",       // 4
            "s-k-20-35",       // 5
            "s-k-200-300",     // 6
            "s-k-30-50",       // 7
            "s-k-30-55",       // 8
            "s-k-35-65",       // 9
            "s-k-40-60",       // 10
            "s-k-40-80",       // 11
            "s-k-50-100",      // 12
            "s-k-50-95",       // 13
            "s-rg-109-35",     // 14
            "s-rg-118-30",     // 15
            "s-rg-155-40",     // 16
            "s-rg-197-45",     // 17
            "s-rg-245-50",     // 18
            "s-rg-31-15",      // 19
            "s-rg-40-20",      // 20
            "s-rg-413-75",     // 21
            "s-rg-63-25",      // 22
            "s-rg-733-100",    // 23
            "s-rg-8-10"        // 24
    };
    
    private static int        universalSetSize;        // number of elements in the universal set
    private static int        numberOfSubsets;         // number of subsets that can be used for the cover
    private static int[][]    subsets;                 // jagged array of all subsets with their elements
    private static int[][]    candidateSubsets;        // pseudo-jagged array of subset nos. that can cover each element
    private static int[]      candidateSubsetsSize;    // keep track of how many subsets cover each element
    private static int[]      minimumCover;            // array of subset nos.
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter a file number [0, 24]: ");
        int fileNumber = s.nextInt();
        s.close();
        System.out.printf("Testing file %s\n...\n", file[fileNumber]);

        final long start = System.currentTimeMillis();

        readFile(fileNumber);
        findMinimumCover();
        
        int size = 0;
        if (minimumCover != null) {
            size = minimumCover.length;
            Arrays.sort(minimumCover);
            printCover(minimumCover);
        } else {
            System.out.println("No cover exists.");
        }

        final long end = System.currentTimeMillis();
        
        System.out.printf("...\nFound %d subsets in %.3f seconds.\n", size, (end - start) / 1000.0);
        System.exit(0);
    }
    
    private static void printCover(int[] cover) {
        for (int subsetNum : cover) {
            System.out.printf("%d: ", subsetNum);
            int[] subset = subsets[subsetNum];
            for (int element : subset)
                System.out.print(element + " ");
            System.out.println();
        }
    }
    
    private static void backtrack(int[] solution, int[] cover, int coverSize, int index) {
        // Reject when current solution is not better than min
        if (minimumCover != null && coverSize >= minimumCover.length)
            return;
        
        // Accept when a cover is found
        if (index == universalSetSize + 1) {
            // Update min
            if (minimumCover == null || coverSize < minimumCover.length) {
                minimumCover = new int[coverSize];
                System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            }
            return;
        }
        
        if (solution[index] > 0) // already have a solution for this no.
            backtrack(solution, cover, coverSize, index + 1);
        else {
            int i, subsetNo, candidateSubset[];
            int numberOfCandidates = candidateSubsetsSize[index];
            for (i = 0; i < numberOfCandidates; i++) {
                subsetNo = candidateSubsets[index][i];
                candidateSubset = subsets[subsetNo];
                
                // Add subset to cover
                cover[coverSize++] = subsetNo; // add to tail
                for (int element : candidateSubset)
                    if (element >= index) // don't bother with earlier elements that have already been solved
                        solution[element]++; // 1 more subset covers this element
                
                // Extend the solution
                backtrack(solution, cover, coverSize, index + 1);
                
                // Remove subset from cover
                coverSize--; // remove from tail
                for (int element : candidateSubset)
                    if (element >= index) // don't bother with earlier elements that have already been solved
                        solution[element]--; // 1 less subset covers this element
            }
        }
    }
    
    private static void findMinimumCover() {
        int[] solution = new int[universalSetSize + 1]; // solution[i] = number of subsets in the cover that contain i
        int[] cover = new int[numberOfSubsets]; // lots of inserting and deleting
        backtrack(solution, cover, 0, 1);
    }
    
    private static void readFile(int fileNumber) {
        Scanner s = null;
    
        try {
            URL url = new URL(path.concat(file[fileNumber]));
            s = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    
        universalSetSize = s.nextInt();
        numberOfSubsets = s.nextInt();
        
        candidateSubsets = new int[universalSetSize + 1][];
        for (int i = 0; i <= universalSetSize; i++)
            candidateSubsets[i] = new int[numberOfSubsets];
        
        candidateSubsetsSize = new int[candidateSubsets.length];
        
        subsets = new int[numberOfSubsets + 1][];
    
        s.nextLine();
        String[] line;
        int element;
        int i;
        
        for (int subset = 1; subset <= numberOfSubsets; subset++) {
            line = s.nextLine().trim().split("\\s+");
            subsets[subset] = new int[line.length]; // add a new subset
            i = 0; // index for subsets
            for (String token : line) {
                if (!token.isEmpty()) { // not empty set
                    element = Integer.parseInt(token);
                    candidateSubsets[element][candidateSubsetsSize[element]++] = subset;
                    subsets[subset][i++] = element;
                }
            }
        }
        s.close();
    }
}
