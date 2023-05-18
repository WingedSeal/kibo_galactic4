package jp.jaxa.iss.kibo.pathfind;

import jp.jaxa.iss.kibo.rpc.defaultapk.Astrobee;

public class OptimalPath {
    private static final int THRESHOLD = 30000;
    private double minTime = 1e7;
    private TargetPoint[] optimalPoints = null;
    private final TargetPoint currentPoint;
    private final long timeRemaining;
    private final boolean shouldConsiderGoal;

    public OptimalPath(long timeRemaining, TargetPoint currentPoint, TargetPoint[] activeTargets, boolean shouldConsiderGoal) {
        this.currentPoint = currentPoint;
        this.timeRemaining = timeRemaining;
        this.shouldConsiderGoal = shouldConsiderGoal;
        findOptimalPath(new TargetPoint[activeTargets.length], 0, activeTargets);
        if (optimalPoints == null && activeTargets.length == 2) {
            findOptimalPath(new TargetPoint[1], 0, activeTargets);
        }
    }

    public TargetPoint[] getPath() {
        return optimalPoints;
    }

    /**
     * a recursive function to find optimal node order using brute force algorithm
     * 
     * @param nodes         an array of `PathFindNode` to return to in optimal order
     * @param pos           position on array to assign `PathFindNode` object
     * @param originalNodes an original array of `PathFindNode` choices
     */
    private void findOptimalPath(TargetPoint[] nodes, int pos, TargetPoint[] originalNodes) {
        if (pos == nodes.length) {
            double timeUsed = getPathTime(currentPoint, nodes);
            if (timeUsed < minTime && timeRemaining - timeUsed > THRESHOLD) {
                setOptimalPoints(nodes);
                minTime = timeUsed;
            }
        } else {
            for (TargetPoint originalNode : originalNodes) {
                nodes[pos] = originalNode;
                findOptimalPath(nodes, pos + 1, originalNodes);
            }
        }
    }
    
    /**
     * calculate the time spent on walking along the nodes 
     * 
     * @param currNode the current node of Astrobee as given in the constructors
     * @param midNodes an array of `PathFindNode` object to walk pass and calculate time
     * @return estimated total time in milliseconds
     */
    private double getPathTime(TargetPoint currNode, TargetPoint[] midNodes) {
        double totalTimeSec = 0;
        for (double distance : PathFind.estimatePathDistances(currNode, midNodes[0])) {
            totalTimeSec += 2 * Math.sqrt(distance / Astrobee.ASTROBEE_ACCELERATION);
        }
        for (int i = 1; i < midNodes.length; i++) {
            for (double distance : PathFind.estimatePathDistances(midNodes[i - 1], midNodes[i])) {
                totalTimeSec += 2 * Math.sqrt(distance / Astrobee.ASTROBEE_ACCELERATION);
            }
        }
        if (shouldConsiderGoal) {
            for (double distance : PathFind.estimatePathDistances(midNodes[midNodes.length - 1], PathFindNode.GOAL)) {
                totalTimeSec += 2 * Math.sqrt(distance / Astrobee.ASTROBEE_ACCELERATION);
            }
        }
        return totalTimeSec * 1000;
    }

    /**
     * copy optimal nodes to the object properties
     */
    private void setOptimalPoints(TargetPoint[] nodes) {
        optimalPoints = new TargetPoint[nodes.length];
        System.arraycopy(nodes, 0, optimalPoints, 0, nodes.length);
    }
}
