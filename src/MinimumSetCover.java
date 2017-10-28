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
    private static List<Set<Integer>> candidateSubsets; // List[i] = set of subset indices that contain i
    private static List<Set<Integer>> subsets; // List of all subsets with their elements
    private static Set<Integer> minimumCover; // Set of subset indices
    
    public static void main(String[] args) {
        final long start = System.currentTimeMillis();
        
        int fileNumber = 0; // Which test file?
        
        readFile(fileNumber);
        //printAllSubsets();
        findMinimumCover();
        printCover(minimumCover);
        
        final long end = System.currentTimeMillis();
        System.out.printf("Time taken: %.3fs\n", (end - start) / 1000.0);
        System.exit(0);
    }
    
    private static void processCover(Set<Integer> cover) {
        if (minimumCover == null || cover.size() < minimumCover.size()) {
            minimumCover = new LinkedHashSet<>(cover);
            //printCover(minimumCover);
        }
    }
    
    private static void printCover(Set<Integer> cover) {
        if (cover == null) {
            System.out.println("No cover exists.");
            return;
        }
        
        System.out.println("# subsets used: " + cover.size());
        
        for (int subset : cover) {
            System.out.printf("%d: ", subset);
            for (int element : subsets.get(subset))
                System.out.print(element + " ");
            System.out.println();
        }
    }
    
    private static Set<Integer> candidates(List<Integer> solution, Set<Integer> cover, int index) {
        Set<Integer> candidates = new LinkedHashSet<>();
        
        if (index > universalSetSize)
            return candidates;
        
        // Check if the element is already covered
        if (solution.get(index) > 0) { // already have a subset to cover index
            candidates.add(solution.get(index));
            return candidates;
        }
        
        return candidateSubsets.get(index);
    }
    
    private static boolean accept(Set<Integer> cover, int index) {
        return index == universalSetSize + 1; // every element is covered
    }
    
    private static boolean reject(Set<Integer> cover) {
        // Reject if cover is not better than minimum
        return minimumCover != null && cover.size() >= minimumCover.size();
    }
    
    private static void backtrack(List<Integer> solution, Set<Integer> cover, int index) {
        if (reject(cover))
            return;
        
        if (accept(cover, index)) {
            processCover(cover);
            return;
        }
        
        if (solution.get(index) > 0) { // already have a solution for this index
            backtrack(solution, cover, index + 1);
        } else {
            for (int subset : candidates(solution, cover, index)) {
                List<Integer> extSolution = new ArrayList<>(solution);
                Set<Integer> extCover = new LinkedHashSet<>(cover);
                
                // Use subset to cover everything it can starting at index
                for (int element : subsets.get(subset)) {
                    if (element >= index)
                        extSolution.set(element, subset); // use subset to cover element
                }
                extCover.add(subset); // add subset to cover
                backtrack(extSolution, extCover, index + 1);
            }
        }
    }
    
    private static void findMinimumCover() {
        ArrayList<Integer> solution = new ArrayList<>(universalSetSize + 1);
        for (int i = 0; i < universalSetSize + 1; i++)
            solution.add(0);
        solution.set(0, null); // Index 0 should never be used
        
        HashSet<Integer> subsetsUsed = new LinkedHashSet<>();
        
        backtrack(solution, subsetsUsed, 1);
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
        
        candidateSubsets = new ArrayList<>(universalSetSize);
        candidateSubsets.add(0, null); // index 0 is used as placeholder
        for (int i = 1; i <= universalSetSize; i++)
            candidateSubsets.add(new LinkedHashSet<>());
        
        subsets = new ArrayList<>(numberOfSubsets);
        subsets.add(0, null); // index 0 is used as placeholder
    
        s.nextLine();
        String[] line;
        for (int subset = 1; subset <= numberOfSubsets; subset++) {
            line = s.nextLine().trim().split("\\s+");
            subsets.add(new LinkedHashSet<>()); // add a new subset
            for (String token : line) {
                if (!token.isEmpty()) { // not empty set
                    int number = Integer.parseInt(token);
                    candidateSubsets.get(number).add(subset);
                    subsets.get(subset).add(number);
                }
            }
        }
        
        s.close();
    }
    
    private static void printAllSubsets() {
        for (int i = 1; i <= numberOfSubsets; i++) {
            for (int element : subsets.get(i)) {
                System.out.printf("%d ", element);
            }
            System.out.println();
        }
    }
}
