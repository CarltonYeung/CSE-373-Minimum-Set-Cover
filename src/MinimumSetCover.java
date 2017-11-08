// Carlton Yeung

import java.io.IOException;
import java.net.URL;
import java.util.*;

class MinimumSetCover {
    
    private int numOfElements; // How many elements are in the universal set?
    private int numOfSubsets; // How many subsets are there?
    private int[][] subsets; // What are all the subsets?
    private int[][] candidates; // Which subsets can cover this element?
    private int[] minimumCover; // Which subsets give a minimum cover?
    
    MinimumSetCover(URL url) {
        init(url);
    }
    
    int[] minimumCover() {
        if (!coverExists())
            return null;
    
        // How many times is an element covered?
        int[] solution = new int[numOfElements + 1];
    
        // Which subsets are used for the cover?
        int[] cover = new int[numOfSubsets];
        int coverSize = 0;
        
        // Are there any forced subsets that all covers must have?
        for (int i = 1; i <= numOfElements; i++) {
            if (candidates[i].length == 1) {
                cover[coverSize++] = candidates[i][0];
                for (int j : subsets[candidates[i][0]])
                    solution[j]++;
            }
        }
    
        backtrack(solution, 1, cover, coverSize);
        
        return minimumCover;
    }
    
    private void backtrack(int[] solution, int element,
                           int[] cover, int coverSize) {
        
        if (minimumCover != null && coverSize >= minimumCover.length)
            return;
        
        if (element == numOfElements + 1) {
            minimumCover = new int[coverSize];
            System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            return;
        }
        
        if (solution[element] > 0) {
            backtrack(solution, element + 1, cover, coverSize);
            return;
        }
        
        for (int i = 0; i < candidates[element].length; i++) {
            
            // Make move
            cover[coverSize++] = candidates[element][i];
            for (int j : subsets[candidates[element][i]])
                /* Everything earlier in the solution vector is already covered,
                 * so we don't need to cover it again. */
                if (j >= element)
                    solution[j]++;
            
            backtrack(solution, element + 1, cover, coverSize);
            
            // Un-make move
            coverSize--;
            for (int j : subsets[candidates[element][i]])
                if (j >= element)
                    solution[j]--;
        }
    }
    
    private void init(URL url) {
        try (Scanner scanner = new Scanner(url.openStream())) {
            numOfElements = scanner.nextInt();
            numOfSubsets = scanner.nextInt();
            scanner.nextLine();
            
            subsets = new int[numOfSubsets + 1][];
            
            // numOfCandidates is used to convert candidates to a jagged array.
            int[] numOfCandidates = new int[numOfElements + 1];
            
            candidates = new int[numOfElements + 1][];
            for (int i = 1; i <= numOfElements; i++)
                candidates[i] = new int[numOfSubsets];
            
            for (int i = 1; i <= numOfSubsets; i++) {
                String[] line = scanner.nextLine().trim().split("\\s+");
                subsets[i] = new int[line.length];
                int tail = 0; // needed for insertions into the array
        
                for (String token : line) {
                    if (!token.isEmpty()) { // there might be empty sets
                        int element = Integer.parseInt(token);
                        candidates[element][numOfCandidates[element]++] = i;
                        subsets[i][tail++] = element;
                    }
                }
            }
            
            // Convert candidates to a jagged array for readability later on
            for (int i = 1; i <= numOfElements; i++) {
                int[] temp = new int[numOfCandidates[i]];
                System.arraycopy(candidates[i], 0, temp, 0, numOfCandidates[i]);
                candidates[i] = temp;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
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
        boolean[] covered = new boolean[numOfElements + 1];
        
        for (int i : cover)
            for (int j : subsets[i])
                covered[j] = true;
        
        for (int i = 1; i <= numOfElements; i++)
            if (!covered[i])
                return false;
        
        return true;
    }
    
    private boolean coverExists() {
        for (int i = 1; i <= numOfElements; i++)
            if (candidates[i].length == 0)
                return false;
        
        return true;
    }
}
