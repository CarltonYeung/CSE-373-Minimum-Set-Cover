import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

class Main {
    
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
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a file number [0, 24]: ");
        int fileNumber = scanner.nextInt();
        scanner.close();
        System.out.printf("Testing file %s\n...\n", fileName[fileNumber]);
        
        final long start = System.currentTimeMillis();
        
        URL url = null;
        try {
            url = new URL(path + fileName[fileNumber]);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        
        MinimumSetCover msc = new MinimumSetCover(url);
        int[] cover = msc.minimumCover();
        msc.print(cover);
        
        final long end = System.currentTimeMillis();
        
        System.out.printf("Search time = %.3f seconds.\n", (end - start) / 1000.0);
        System.exit(0);
    }
}
