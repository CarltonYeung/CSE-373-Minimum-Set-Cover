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
    
    private static int universalSetSize; // Number of elements in the universal set starting from 1
    private static int numberOfSubsets; // Number of subsets that can be used for the cover
    private static List<Set<Integer>> universalElement; // universalElement[i] = array of subset #s that contain i
    private static List<Set<Integer>> subsets; // Array of all the subsets
    
    public static void main(String[] args) {
        int fileNumber = 0; // Which test file?
        
        readFile(fileNumber);
        printAllSubsets();
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
        
        universalElement = new ArrayList<>(universalSetSize);
        for (int i = 0; i <= universalSetSize; i++) // index : [1, universalSetSize]
            universalElement.add(new LinkedHashSet<>()); // index 0 is not used
        
        subsets = new ArrayList<>(numberOfSubsets);
        for (int i = 0; i < numberOfSubsets; i++) // index : [0, numberOfSubsets - 1]
            subsets.add(new LinkedHashSet<>());
    
        s.nextLine();
        String[] line;
        for (int subset = 0; subset < numberOfSubsets; subset++) {
            line = s.nextLine().trim().split("\\s+");
            for (String token : line) {
                int number = Integer.parseInt(token);
                universalElement.get(number).add(subset);
                subsets.get(subset).add(number);
            }
        }
    }
    
    private static void printAllSubsets() {
        for (Set<Integer> subset : subsets) {
            for (int element : subset) {
                System.out.printf("%d ", element);
            }
            System.out.println();
        }
    }
}
