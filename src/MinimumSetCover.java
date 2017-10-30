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
    private static int[]      numberOfCandidates;      // keep track of how many subsets cover each element
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
        
        if (minimumCover != null) {
            Arrays.sort(minimumCover);
            printCover(minimumCover);
        } else {
            System.out.println("No cover exists.");
        }

        final long end = System.currentTimeMillis();
        
        System.out.printf("...\nFound %d subsets in %.3f seconds.\n",
                (minimumCover == null)? 0 : minimumCover.length,
                (end - start) / 1000.0);
        
        System.exit(0);
    }
    
    private static void printCover(int[] cover) {
        for (int subsetNum : cover) {
            System.out.printf("%d: ", subsetNum);
            for (int element : subsets[subsetNum])
                System.out.print(element + " ");
            System.out.println();
        }
    }
    
    static void backtrack(int[] solution, int start, int[] cover, int coverSize) {
        if (minimumCover != null && coverSize >= minimumCover.length) {
            return;
        } else if (start == universalSetSize + 1) {
            minimumCover = new int[coverSize];
            System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            return;
        }
        
        if (solution[start] > 0) { // already have a solution for this no.
            backtrack(solution, start + 1, cover, coverSize);
        } else {
            for (int i = 0; i < numberOfCandidates[start]; i++) {
                
                // (1) Add subset to cover
                cover[coverSize++] = candidateSubsets[start][i]; // "add the i'th candidate subset for start to the cover"
                for (int j : subsets[candidateSubsets[start][i]]) // cover as much as possible with this candidate
                    if (j >= start) // don't bother with earlier elements that have already been solved
                        solution[j]++; // 1 more subset covers this element
                
                // Go further down the solution tree
                backtrack(solution, start + 1, cover, coverSize);
                
                // (2) Remove subset from cover; undo (1)
                coverSize--;
                for (int j : subsets[candidateSubsets[start][i]])
                    if (j >= start)
                        solution[j]--;
            }
        }
    }
    
    private static void findMinimumCover() {
        int num = numberOfCandidates[1];
        MyThread[] threads = new MyThread[num];
        
        for (int i = 0; i < num; i++) {
            threads[i] = new MyThread(universalSetSize + 1, numberOfSubsets);
            threads[i].cover[threads[i].coverSize++] = candidateSubsets[1][i];
            for (int j : subsets[candidateSubsets[1][i]])
                threads[i].solution[j]++;
            threads[i].start();
        }
        
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        
        numberOfCandidates = new int[candidateSubsets.length];
        
        subsets = new int[numberOfSubsets + 1][];
    
        s.nextLine();
        
        for (int subset = 1; subset <= numberOfSubsets; subset++) {
            String[] line = s.nextLine().trim().split("\\s+");
            subsets[subset] = new int[line.length];
            
            int subsetSize = 0;
            for (String token : line) {
                if (!token.isEmpty()) { // not empty set
                    int element = Integer.parseInt(token);
                    candidateSubsets[element][numberOfCandidates[element]++] = subset;
                    subsets[subset][subsetSize++] = element;
                }
            }
        }
        s.close();
    }
}
