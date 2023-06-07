package jp.jaxa.iss.kibo.pathfind;

import jp.jaxa.iss.kibo.rpc.defaultapk.Astrobee;
import java.util.ArrayList;
public class OptimalPath {
    private static final int THRESHOLD = 500;
    private double minTime = 1e7;
    private int maxScore = 0;
    private TargetPoint[] optimalPoints = null;
    private final PathFindNode currentNode;
    private final long timeRemaining;
    private final boolean shouldConsiderGoal;
    private TargetPoint[] activeTargets;
    public Astrobee astrobee;

    public OptimalPath(Astrobee astrobee,long timeRemaining, PathFindNode currentNode, TargetPoint[] activeTargets, boolean shouldConsiderGoal) {
        this.astrobee = astrobee;
        this.currentNode = currentNode;
        this.timeRemaining = timeRemaining;
        this.shouldConsiderGoal = shouldConsiderGoal;
        this.activeTargets = activeTargets;
        int totalPointOnPath = activeTargets.length;
        do {
            ArrayList<TargetPoint[]> permutation = getTargetPointPermutation(activeTargets, totalPointOnPath);
            findOptimalPath(permutation);
            totalPointOnPath--;
        } while (totalPointOnPath > 0 && optimalPoints == null);
    }

    public TargetPoint[] getPath() {
        return optimalPoints;
    }

    public double getMinTime(){
        return minTime;
    }

    public static ArrayList<TargetPoint[]> getTargetPointPermutation(TargetPoint[] targetArray, int totalPointOnPath) {
        ArrayList<TargetPoint[]> allPermutations = new ArrayList<>();
        enumerate(targetArray, targetArray.length, totalPointOnPath, allPermutations);
        return allPermutations;
    }

    private static void enumerate(TargetPoint[] a, int n, int k, ArrayList<TargetPoint[]> allPermutations) {
        if (k == 0) {
            TargetPoint[] singlePermutation = new TargetPoint[a.length-n];
            for (int i = n; i < a.length; i++){
                singlePermutation[i-n] = a[i];
            }
            allPermutations.add(singlePermutation);
        }

        for (int i = 0; i < n; i++) {
            swap(a, i, n-1);
            enumerate(a, n-1, k-1, allPermutations);
            swap(a, i, n-1);
        }
    }

    // helper function that swaps element of a between position i and j
    public static void swap(TargetPoint[] a, int i, int j) {
        TargetPoint temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    /**
     * a recursive function to find optimal node order using brute force algorithm
     * 
     * @param allPermutations
     */
    private void findOptimalPath(ArrayList<TargetPoint[]> allPermutations) {
        for (int i=0; i<allPermutations.size(); i++) {
            TargetPoint[] pointsToVisit = allPermutations.get(i);
            double timeUsed = getPathTime(pointsToVisit);
            int score = getTotalScore(pointsToVisit);
            if ((activeTargets.length == pointsToVisit.length || score == maxScore) && timeUsed < minTime && timeRemaining - timeUsed > THRESHOLD) {
                setOptimalPoints(pointsToVisit);
                minTime = timeUsed;
            }
            else if (score > maxScore && timeRemaining - timeUsed > THRESHOLD) {
                setOptimalPoints(pointsToVisit);
                minTime = timeUsed;
                maxScore = score;
            }
        }
    }

    /**
     * calculate the time spent on walking along the nodes 
     *
     * @param midNodes an array of `PathFindNode` object to walk pass and calculate time
     * @return estimated total time in milliseconds
     */
    private double getPathTime(TargetPoint[] midNodes) {
        double totalTimeSec = 0;
        for (double distance : PathFind.estimatePathDistances(astrobee,currentNode, midNodes[0])) {
            totalTimeSec += 2 * Math.sqrt(distance * 2 / (Astrobee.ASTROBEE_ACCELERATION + Astrobee.ASTROBEE_DECELERATION));
        }
        for (int i = 1; i < midNodes.length; i++) {
            for (double distance : PathFind.estimatePathDistances(astrobee,midNodes[i - 1], midNodes[i])) {
                totalTimeSec += 2 * Math.sqrt(distance * 2 / (Astrobee.ASTROBEE_ACCELERATION + Astrobee.ASTROBEE_DECELERATION));
            }
        }
        if (shouldConsiderGoal) {
            for (double distance : PathFind.estimatePathDistances(astrobee,midNodes[midNodes.length - 1], PathFindNode.GOAL)) {
                totalTimeSec += 2 * Math.sqrt(distance * 2 / (Astrobee.ASTROBEE_ACCELERATION + Astrobee.ASTROBEE_DECELERATION));
            }
        }
        return totalTimeSec * 1000;
    }

    private int getTotalScore(TargetPoint[] midPoints) {
        int totalScore = 0;
        for (TargetPoint point: midPoints) {
            totalScore += point.getScore();
        }
        return totalScore;
    }

    /**
     * copy optimal nodes to the object properties
     */
    private void setOptimalPoints(TargetPoint[] nodes) {
        optimalPoints = new TargetPoint[nodes.length];
        System.arraycopy(nodes, 0, optimalPoints, 0, nodes.length);
    }
}
