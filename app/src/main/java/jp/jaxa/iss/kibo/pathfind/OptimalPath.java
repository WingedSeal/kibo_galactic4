package jp.jaxa.iss.kibo.pathfind;

import jp.jaxa.iss.kibo.rpc.defaultapk.Astrobee;

public class OptimalPath {
    private static final int THRESHOLD = 30000;
    private double minTime = 1e7;
    private PathFindNode[] optimalNodes = null;
    private PathFindNode currNode;
    private long timeRemaining;
    private boolean shouldConsiderGoal;

    public OptimalPath(long timeRemaining, PathFindNode currNode, PathFindNode[] activeTargets, boolean shouldConsiderGoal) {
        this.currNode = currNode;
        this.timeRemaining = timeRemaining;
        this.shouldConsiderGoal = shouldConsiderGoal;
        findOptimalPath(new PathFindNode[activeTargets.length], 0, activeTargets);
        if (optimalNodes == null && activeTargets.length == 2) {
            findOptimalPath(new PathFindNode[1], 0, activeTargets);
        }
    }

    public PathFindNode[] getPath() {
        return optimalNodes;
    }

    /**
     * a recursive function to find optimal node order using brute force algorithm
     * 
     * @param nodes         an array of `PathFindNode` to return to in optimal order
     * @param pos           position on array to assign `PathFindNode` object
     * @param originalNodes an original array of `PathFindNode` choices
     */
    private void findOptimalPath(PathFindNode[] nodes, int pos, PathFindNode[] originalNodes) {
        if (pos == nodes.length) {
            double timeUsed = getPathTime(currNode, nodes);
            if (timeUsed < minTime && timeRemaining - timeUsed > THRESHOLD) {
                setOptimalNodes(nodes);
                minTime = timeUsed;
            }
        } else {
            for (int i = 0; i < originalNodes.length; i++) {
                nodes[pos] = originalNodes[i];
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
    private double getPathTime(PathFindNode currNode, PathFindNode[] midNodes) {
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
    private void setOptimalNodes(PathFindNode[] nodes) {
        this.optimalNodes = new PathFindNode[nodes.length];
        System.arraycopy(nodes, 0, this.optimalNodes, 0, nodes.length);
    }
}
