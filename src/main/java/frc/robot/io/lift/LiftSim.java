package frc.robot.io.lift;

import frc.robot.io.LiftIO;
import static frc.robot.Constants.LiftConstants.*;

public class LiftSim extends LiftIO {
    private double distance = 0;

    public void setDistance(double distance) {
        this.distance = Math.max(kLowerLimitDistance, Math.min(kUpperLimitDistance, distance));
    }

    public double getDistance() {
        return distance;
    }

    public boolean atLowerLimit() {
        return distance <= kLowerLimitDistance;
    }

    public boolean atUpperLimit() {
        return distance >= kUpperLimitDistance;
    }
}
