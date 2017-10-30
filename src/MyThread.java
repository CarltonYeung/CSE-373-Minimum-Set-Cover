class MyThread extends Thread {
    int solution[], start, cover[], coverSize;
    
    MyThread (int[] solution, int start, int[] cover, int coverSize) {
        this.solution = solution;
        this.start = start;
        this.cover = cover;
        this.coverSize = coverSize;
    }
    
    @Override
    public void run() {
        MinimumSetCover.backtrack(solution, start, cover, coverSize);
    }
}
