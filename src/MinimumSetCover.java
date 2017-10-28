import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MinimumSetCover {
    final private static String path = "http://www3.cs.stonybrook.edu/~skiena/373/setcover/";
    final private static String[] fileName = {
            "s-X-12-6",
            "s-k-100-175",
            "s-k-150-225",
            "s-k-150-250",
            "s-k-20-30",
            "s-k-20-35",
            "s-k-200-300",
            "s-k-30-50",
            "s-k-30-55",
            "s-k-35-65",
            "s-k-40-60",
            "s-k-40-80",
            "s-k-50-100",
            "s-k-50-95",
            "s-rg-109-35",
            "s-rg-118-30",
            "s-rg-155-40",
            "s-rg-197-45",
            "s-rg-245-50",
            "s-rg-31-15",
            "s-rg-40-20",
            "s-rg-413-75",
            "s-rg-63-25",
            "s-rg-733-100",
            "s-rg-8-10"
    };
    
    private static int universalSetSize; // Number of elements in the universal set
    private static int numberOfSubsets; // Number of subsets that can be used for the cover
    private static List<Set<Integer>> candidateSubsets; // List[i] = set of subset indices that contain i
    private static List<Set<Integer>> subsets; // List of all subsets with their elements
    private static Set<Integer> minimumCover; // Set of subset indices
    
    public static void main(String[] args) {
        int fileNumber = 0; // Which test file?
        
        readFile(fileNumber);
        //printAllSubsets();
        findMinimumCover();
        printCover(minimumCover);
        
        System.exit(0);
    }
    
    private static void processCover(Set<Integer> cover) {
        if (minimumCover == null || cover.size() < minimumCover.size())
            minimumCover = new LinkedHashSet<>(cover);
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
    
    private static Set<Integer> candidates(Set<Integer> subsetsUsed, int index) {
        Set<Integer> candidates = new LinkedHashSet<>();
        
        if (index > universalSetSize)
            return candidates;
        
        // Check if the element is already covered
        for (int subset : subsetsUsed) {
            for (int element : subsets.get(subset)) {
                if (index == element) {
                    candidates.add(subset);
                    return candidates;
                }
            }
        }
        
        return candidateSubsets.get(index);
    }
    
    private static boolean accept(Set<Integer> subsetsUsed, int index) {
        return index == universalSetSize + 1; // every element is covered
    }
    
    private static boolean reject(Set<Integer> subsetsUsed) {
        return false;
    }
    
    private static void backtrack(List<Integer> solution, Set<Integer> subsetsUsed, int index) {
        if (reject(subsetsUsed))
            return;
        
        if (accept(subsetsUsed, index))
            processCover(subsetsUsed);
        
        for (int subset : candidates(subsetsUsed, index)) {
            List<Integer> extendedSolution = new ArrayList<>(solution);
            extendedSolution.set(index, subset); // use subset to cover index
            
            Set<Integer> extendedSubsetsUsed = new LinkedHashSet<>(subsetsUsed);
            extendedSubsetsUsed.add(subset);
            backtrack(extendedSolution, extendedSubsetsUsed, index + 1);
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
                int number = Integer.parseInt(token);
                candidateSubsets.get(number).add(subset);
                subsets.get(subset).add(number);
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
