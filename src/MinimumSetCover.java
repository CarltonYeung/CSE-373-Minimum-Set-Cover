import java.io.IOException;
import java.net.URL;
import java.util.*;

class MinimumSetCover {
    
    private int numOfElements; // how many elements are in the universal set?
    private int numOfSubsets; // how many subsets are there?
    private int[][] subsets; // what are all the subsets?
    private int[][] candidates; // which subsets can cover this element?
    private int[] numOfCandidateSubsets; // how many subsets can cover this element?
    private int[] minimumCover; // which subsets give a minimum cover?
    
    MinimumSetCover(URL url) {
        init(url);
    }
    
    int[] minimumCover() {
        if (!coverExists())
            return null;
        
        int[] solution = new int[numOfElements + 1];
        int[] cover = new int[numOfSubsets];
        
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
                System.out.printf(" %d", element);
            System.out.println();
        }
        
        System.out.println("\nSize = " + cover.length);
    }
    
    private boolean coverExists() {
        for (int i = 1; i <= numOfElements; i++)
            if (numOfCandidateSubsets[i] == 0)
                return false;
        return true;
    }
    
    private void backtrack(int[] solution, int start, int[] cover, int coverSize) {
        if (minimumCover != null && coverSize >= minimumCover.length) {
            return;
        }
        
        if (start == numOfElements + 1) {
            minimumCover = new int[coverSize];
            System.arraycopy(cover, 0, minimumCover, 0, coverSize);
            return;
        }
        
        if (solution[start] > 0) {
            backtrack(solution, start + 1, cover, coverSize);
            return;
        }
        
        for (int i = 0; i < numOfCandidateSubsets[start]; i++) {
            cover[coverSize++] = candidates[start][i];
            for (int j : subsets[candidates[start][i]])
                if (j >= start)
                    solution[j]++;
            
            backtrack(solution, start + 1, cover, coverSize);
            
            coverSize--;
            for (int j : subsets[candidates[start][i]])
                if (j >= start)
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
        
        numOfElements = scanner.nextInt();
        numOfSubsets = scanner.nextInt();
        scanner.nextLine();
        
        candidates = new int[numOfElements + 1][];
        for (int i = 0; i <= numOfElements; i++)
            candidates[i] = new int[numOfSubsets];
        numOfCandidateSubsets = new int[candidates.length];
        subsets = new int[numOfSubsets + 1][];
        
        for (int i = 1; i <= numOfSubsets; i++) {
            String[] line = scanner.nextLine().trim().split("\\s+");
            subsets[i] = new int[line.length];
            int subsetSize = 0;
            
            for (String token : line) {
                if (!token.isEmpty()) {
                    int element = Integer.parseInt(token);
                    candidates[element][numOfCandidateSubsets[element]++] = i;
                    subsets[i][subsetSize++] = element;
                }
            }
        }
        
        scanner.close();
    }
}
