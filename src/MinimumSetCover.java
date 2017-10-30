import java.io.IOException;
import java.net.URL;
import java.util.*;

class MinimumSetCover {
    
    private int        numberOfElements;        // number of elements in the universal set
    private int        numberOfSubsets;         // number of subsets that can be used for the cover
    private int[][]    subsets;                 // jagged array of all subsets with their elements
    private int[][]    candidateSubsets;        // pseudo-jagged array of subset nos. that can cover each element
    private int[]      candidateSubsetsSize;    // keep track of how many subsets cover each element
    private int[]      minimumCover;            // array of subset nos.
    
    MinimumSetCover(URL url) {
        init(url);
    }
    
    int[] minimumCover() {
        int[] solution = new int[numberOfElements + 1];
        int[] cover = new int[numberOfSubsets];
        
        backtrack(solution, 1, cover, 0);
        
        return minimumCover;
    }
    
    void print(int[] cover) {
        if (cover == null) {
            System.out.println("No cover exists.");
            return;
        }
        
        Arrays.sort(cover);
        
        for (int subsetNum : cover) {
            System.out.printf("%d: ", subsetNum);
            for (int element : subsets[subsetNum])
                System.out.print(element + " ");
            System.out.println();
        }
        
        System.out.println("\nSize = " + cover.length);
    }
    
    private void backtrack(int[] solution, int start, int[] cover, int coverSize) {
        if (minimumCover != null && coverSize >= minimumCover.length) {
            return;
        } else if (start == numberOfElements + 1) {
            minimumCover = new int[coverSize];
            System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            return;
        }
        
        if (solution[start] > 0) {
            backtrack(solution, start + 1, cover, coverSize);
        } else {
            for (int i = 0; i < candidateSubsetsSize[start]; i++) {
                
                cover[coverSize++] = candidateSubsets[start][i];
                for (int j : subsets[candidateSubsets[start][i]])
                    if (j >= start)
                        solution[j]++;
                
                backtrack(solution, start + 1, cover, coverSize);
                
                coverSize--;
                for (int j : subsets[candidateSubsets[start][i]])
                    if (j >= start)
                        solution[j]--;
            }
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
        
        numberOfElements = scanner.nextInt();
        numberOfSubsets = scanner.nextInt();
        scanner.nextLine();
        
        candidateSubsets = new int[numberOfElements + 1][];
        for (int i = 0; i <= numberOfElements; i++)
            candidateSubsets[i] = new int[numberOfSubsets];
        candidateSubsetsSize = new int[candidateSubsets.length];
        subsets = new int[numberOfSubsets + 1][];
        
        for (int i = 1; i <= numberOfSubsets; i++) {
            String[] line = scanner.nextLine().trim().split("\\s+");
            subsets[i] = new int[line.length];
            int subsetSize = 0;
            
            for (String token : line) {
                if (!token.isEmpty()) {
                    int element = Integer.parseInt(token);
                    candidateSubsets[element][candidateSubsetsSize[element]++] = i;
                    subsets[i][subsetSize++] = element;
                }
            }
        }
        
        scanner.close();
    }
}
