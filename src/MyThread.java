class MyThread extends Thread {
    int solution[], start, cover[], coverSize, cs[], ncs;
    
    MyThread (int solutionSize, int numberOfSubsets, int[] cs, int ncs) {
        this.solution = new int[solutionSize];
        this.start = 1;
        this.cover = new int[numberOfSubsets];
        this.coverSize = 0;
        this.cs = cs;
        this.ncs = ncs;
    }
    
    @Override
    public void run() {
        MinimumSetCover.backtrack(solution, start, cover, coverSize, cs, ncs);
    }
}
