class MyThread extends Thread {
    int solution[], start, cover[], coverSize;
    
    MyThread (int solutionSize, int numberOfSubsets) {
        this.solution = new int[solutionSize];
        this.start = 1;
        this.cover = new int[numberOfSubsets];
        this.coverSize = 0;
    }
    
    @Override
    public void run() {
        MinimumSetCover.backtrack(solution, start, cover, coverSize);
    }
}
