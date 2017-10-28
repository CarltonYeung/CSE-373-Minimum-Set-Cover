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
        
        int fileNumber = 12; // Which test file?
        
        readFile(fileNumber);
        findMinimumCover();
        printCover(minimumCover);
        
        final long end = System.currentTimeMillis();
        System.out.printf("Time taken: %.3fs\n", (end - start) / 1000.0);
        System.exit(0);
    }
    
    private static void processCover(Set<Integer> cover) {
        if (minimumCover == null || cover.size() < minimumCover.size()) {
            minimumCover = new TreeSet<>(cover);
        }
    }
    
    private static void printCover(Set<Integer> cover) {
        if (cover == null) {
            System.out.println("No cover exists.");
            return;
        }
        
        for (int subsetNum : cover) {
            System.out.printf("%d: ", subsetNum);
            Set<Integer> subset = subsets.get(subsetNum);
            for (int element : subset)
                System.out.print(element + " ");
            System.out.println();
        }
    
        System.out.println("# of subsets used: " + cover.size());
    }
    
    private static Set<Integer> candidates(int index) {
        return candidateSubsets.get(index);
    }
    
    private static boolean accept(int index) {
        return index == universalSetSize + 1; // every element is covered
    }
    
    private static boolean reject(Set<Integer> cover) {
        // Reject if cover is not better than minimum
        return minimumCover != null && cover.size() >= minimumCover.size();
    }
    
    private static void backtrack(List<Deque<Integer>> solution, Set<Integer> cover, int index) {
        if (reject(cover))
            return;
        
        if (accept(index)) {
            processCover(cover);
            return;
        }
        
        if (!solution.get(index).isEmpty()) // already have a solution for this index
            backtrack(solution, cover, index + 1);
        else {
            Set<Integer> candidates = candidateSubsets.get(index); // get candidates
            
            for (int subset : candidates) {
                Set<Integer> candidateSubset = subsets.get(subset);
                
                for (int element : candidateSubset)
                    solution.get(element).addFirst(subset);
                
                cover.add(subset);
                
                backtrack(solution, cover, index + 1);
                
                for (int element : candidateSubset)
                    solution.get(element).removeFirst();
                
                cover.remove(subset);
            }
        }
    }
    
    private static void findMinimumCover() {
        List<Deque<Integer>> solution = new ArrayList<>(universalSetSize + 1);
        for (int i = 0; i <= universalSetSize; i++)
            solution.add(new ArrayDeque<>());
        
        Set<Integer> cover = new LinkedHashSet<>();
        
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
        
        candidateSubsets = new ArrayList<>();
        for (int i = 0; i <= universalSetSize; i++)
            candidateSubsets.add(new LinkedHashSet<>());
        
        subsets = new ArrayList<>();
        subsets.add(null); // index 0 should never be used
    
        s.nextLine();
        String[] line;
        for (int subset = 1; subset <= numberOfSubsets; subset++) {
            line = s.nextLine().trim().split("\\s+");
            subsets.add(new LinkedHashSet<>()); // add a new subset
            for (String token : line) {
                if (!token.isEmpty()) { // not empty set
                    int element = Integer.parseInt(token);
                    candidateSubsets.get(element).add(subset);
                    subsets.get(subset).add(element);
                }
            }
        }
        
        s.close();
    }
    
    private static void printAllSubsets() {
        for (int i = 1; i <= numberOfSubsets; i++) {
            Set<Integer> subset = subsets.get(i);
            for (int element : subset)
                System.out.printf("%d ", element);
            System.out.println();
        }
    }
}
