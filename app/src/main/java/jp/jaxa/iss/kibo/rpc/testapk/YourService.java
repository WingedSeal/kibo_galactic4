package jp.jaxa.iss.kibo.rpc.testapk;

import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.pathfind.PathFind;
import jp.jaxa.iss.kibo.pathfind.PathFindNode;
import jp.jaxa.iss.kibo.pathfind.Target;
import jp.jaxa.iss.kibo.pathfind.TargetPoint;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;
import jp.jaxa.iss.kibo.utils.QuaternionCalculator;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){
        PathFind.pathFindMoveTo(api, PathFindNode.START, TargetPoint.getTargetPoint(1), new Quaternion(0,0,0, 1));
    }

    @Override
    protected void runPlan2(){
        // write your plan 2 here
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here
    }

}

