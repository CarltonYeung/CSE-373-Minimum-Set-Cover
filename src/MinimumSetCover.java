import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MinimumSetCover {
    final private static String path = "http://www3.cs.stonybrook.edu/~skiena/373/setcover/";
    final private static String[] fileName = {
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
    
    private static int universalSetSize; // Number of elements in the universal set
    private static int numberOfSubsets; // Number of subsets that can be used for the cover
    private static int[][] candidateSubsets; // jagged array of subset indices that contain i
    private static int[] candidateSubsetsSize; // size of each candidateSubsets[i]
    private static int[][] subsets; // jagged array of all subsets with their elements
    private static List<Integer> minimumCover; // Set of subset indices
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter a file number [0, 24]: ");
        int fileNumber = s.nextInt();
        s.close();
        System.out.printf("Testing file %s\n...\n", fileName[fileNumber]);

        // Start timer
        final long start = System.currentTimeMillis();

        // Run backtrack search
        readFile(fileNumber);
        findMinimumCover();
        if (minimumCover != null)
            minimumCover.sort(Comparator.naturalOrder());
        printCover(minimumCover);

        // End timer
        final long end = System.currentTimeMillis();
        
        // Print results
        int size = (minimumCover == null)? 0 : minimumCover.size();
        System.out.printf("...\nFound %d subsets in %.3f seconds.\n", size, (end - start) / 1000.0);
        System.exit(0);
    }
    
    private static void printCover(List<Integer> cover) {
        if (cover == null) {
            System.out.println("No cover exists.");
            return;
        }
        
        for (int subsetNum : cover) {
            System.out.printf("%d: ", subsetNum);
            int[] subset = subsets[subsetNum];
            for (int element : subset)
                System.out.print(element + " ");
            System.out.println();
        }
    }
    
    private static void backtrack(int[] solution, List<Integer> cover, int index) {
        // Reject
        if (minimumCover != null && cover.size() >= minimumCover.size())
            return;
        
        // Accept
        if (index == universalSetSize + 1) {
            // Process cover
            if (minimumCover == null || cover.size() < minimumCover.size())
                minimumCover = new ArrayList<>(cover);
            return;
        }
        
        if (solution[index] > 0) // already have a solution for this index
            backtrack(solution, cover, index + 1);
        else {
            for (int i = 0; i < candidateSubsetsSize[index]; i++) {
                int candidateSubsetIndex = candidateSubsets[index][i];
                int[] candidateSubset = subsets[candidateSubsetIndex];
                
                // Add subset to cover
                cover.add(candidateSubsetIndex); // add to tail
                for (int element : candidateSubset)
                    if (element >= index)
                        solution[element]++; // 1 more subset covers this element
                
                backtrack(solution, cover, index + 1);
                
                // Remove subset from cover
                cover.remove(cover.size() - 1); // remove from tail
                for (int element : candidateSubset)
                    if (element >= index)
                        solution[element]--; // 1 less subset covers this element
            }
        }
    }
    
    private static void findMinimumCover() {
        int[] solution = new int[universalSetSize + 1]; // solution[i] = number of subsets in the cover that contain i
        List<Integer> cover = new ArrayList<>(); // lots of inserting and deleting
        backtrack(solution, cover, 1);
    }
    
    private static void readFile(int fileNumber) {
        Scanner s = null;
    
        try {
            URL url = new URL(path.concat(fileName[fileNumber]));
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
        candidateSubsets[0] = null; // fail-fast; index 0 should never be accessed
        
        candidateSubsetsSize = new int[universalSetSize + 1];
        
        subsets = new int[numberOfSubsets + 1][];
        subsets[0] = null; // fail-fast; index 0 should never be accessed;
    
        s.nextLine();
        String[] line;
        for (int subset = 1; subset <= numberOfSubsets; subset++) {
            line = s.nextLine().trim().split("\\s+");
            subsets[subset] = new int[line.length]; // add a new subset
            int i = 0; // index for subsets
            for (String token : line) {
                if (!token.isEmpty()) { // not empty set
                    int element = Integer.parseInt(token);
                    candidateSubsets[element][candidateSubsetsSize[element]++] = subset;
                    subsets[subset][i++] = element;
                }
            }
        }
        
        s.close();
    }
}
