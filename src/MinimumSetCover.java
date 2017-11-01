import java.io.IOException;
import java.net.URL;
import java.util.*;

class MinimumSetCover {
    
    private int numOfElements; // how many elements are in the universal set?
    private int numOfSubsets; // how many subsets are there?
    private int[][] subsets; // what are all the subsets?
    private int[][] candidates; // which subsets can cover this element?
    private int[] minimumCover; // which subsets give a minimum cover?
    
    MinimumSetCover(URL url) {
        init(url);
    }
    
    int[] minimumCover() {
        if (!coverExists())
            return null;
        
        int[] solution = new int[numOfElements + 1]; // how many times is an element covered by the cover?
        int[] cover = new int[numOfSubsets]; // which subsets are used for the cover?
        int coverSize = 0;
        
        // Start by using forced subsets; i.e. every cover must have this subset
        for (int i = 1; i <= numOfElements; i++) {
            if (candidates[i].length == 1) {
                cover[coverSize++] = candidates[i][0]; // add this subset to the cover
                for (int j : subsets[candidates[i][0]]) // use this subset to cover all its elements
                    solution[j]++;
            }
        }
    
        backtrack(solution, 1, cover, coverSize);
        
        return minimumCover;
    }
    
    int[] minimumCoverExhaustive() {
        int[] solution = new int[numOfElements + 1];
        int[] cover = new int[numOfSubsets];
        
        exhaustive(solution, 1, cover, 0);
        
        return minimumCover;
    }
    
    void print(int[] cover) {
        if (cover == null) {
            System.out.println("No cover exists.");
            return;
        }
        
        Arrays.sort(cover);
        
        for (int i : cover) {
            System.out.printf("%d: ", i);
            for (int j : subsets[i])
                System.out.printf(" %d", j);
            System.out.println();
        }
        
        System.out.println("\nSize = " + cover.length);
    }
    
    boolean isCover(int[] cover) {
        int[] covered = new int[numOfElements + 1]; // how many times is an element covered?
    
        // Union all the elements in the cover
        for (int i : cover)
            for (int j : subsets[i])
                covered[j]++;
        
        // Check if any element is uncovered
        for (int i = 1; i <= numOfElements; i++)
            if (covered[i] == 0)
                return false;
        
        return true;
    }
    
    private boolean coverExists() {
        // Check for any elements that can't be covered
        for (int i = 1; i <= numOfElements; i++)
            if (candidates[i].length == 0)
                return false;
        return true;
    }
    
    private void backtrack(int[] solution, int start, int[] cover, int coverSize) {
        // Prune any cover that can't be smaller than the current minimum cover
        if (minimumCover != null && coverSize >= minimumCover.length)
            return;
        
        // Has a new minimum cover been found?
        if (start == numOfElements + 1) {
            minimumCover = new int[coverSize];
            System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            return;
        }
        
        // Is this element already covered?
        if (solution[start] > 0) {
            backtrack(solution, start + 1, cover, coverSize);
            return;
        }
        
        // Explore all candidate subtrees
        for (int i = 0; i < candidates[start].length; i++) {
            
            // Extend the solution
            cover[coverSize++] = candidates[start][i];
            for (int j : subsets[candidates[start][i]])
                if (j >= start) // small optimization
                    solution[j]++;
            
            // Continue from the next element
            backtrack(solution, start + 1, cover, coverSize);
            
            // Retract the solution
            coverSize--;
            for (int j : subsets[candidates[start][i]])
                if (j >= start)
                    solution[j]--;
        }
    
    }
    
    private void exhaustive(int[] solution, int start, int[] cover, int coverSize) {
        if (start == numOfElements + 1) {
            if (minimumCover == null || coverSize < minimumCover.length) {
                minimumCover = new int[coverSize];
                System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            }
            return;
        }
    
        if (solution[start] > 0) {
            exhaustive(solution, start + 1, cover, coverSize);
            return;
        }
    
        for (int i = 0; i < candidates[start].length; i++) {
            cover[coverSize++] = candidates[start][i];
            for (int j : subsets[candidates[start][i]])
                solution[j]++;
    
            exhaustive(solution, start + 1, cover, coverSize);
        
            coverSize--;
            for (int j : subsets[candidates[start][i]])
                solution[j]--;
        }
    }
    
    private void init(URL url) {
        Scanner scanner = null;
        
        try {
            scanner = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        // Read the first two numbers from the file
        numOfElements = scanner.nextInt();
        numOfSubsets = scanner.nextInt();
        scanner.nextLine();
        
        /* Initialize the data structures.
         * Some arrays' index 0 is not initialized on purpose,
         * so that the program crashes if it's referenced by accident.
         */
        subsets = new int[numOfSubsets + 1][];
        
        /* numOfCandidates is used to convert candidates to a jagged array.
         * For now, it's not known how many subsets cover each element.
         */
        int[] numOfCandidates = new int[numOfElements + 1];
        candidates = new int[numOfElements + 1][];
        for (int i = 1; i <= numOfElements; i++)
            candidates[i] = new int[numOfSubsets];
        
        // Read each line to initialize the data structures
        for (int i = 1; i <= numOfSubsets; i++) {
            String[] line = scanner.nextLine().trim().split("\\s+");
            subsets[i] = new int[line.length];
            int tail = 0; // used to insert each element to the tail
            
            for (String token : line) {
                if (!token.isEmpty()) { // there may be empty sets
                    int element = Integer.parseInt(token);
                    candidates[element][numOfCandidates[element]++] = i;
                    subsets[i][tail++] = element;
                }
            }
        }
    
        scanner.close();
        
        /* Convert candidates to a jagged array primarily for ease of use
         * and slightly better spatial locality.
         */
        for (int i = 1; i <= numOfElements; i++) {
            int[] temp = new int[numOfCandidates[i]];
            System.arraycopy(candidates[i], 0, temp, 0, numOfCandidates[i]);
            candidates[i] = temp;
        }
    }
}
