package frc.robot.io;

import monologue.Logged;
import monologue.Annotations.Log;

public abstract class LiftIO implements Logged {
    
    public abstract void setDistance(double distance);

    @Log
    public abstract double getDistance();

    @Log
    public abstract boolean atLowerLimit();

    @Log
    public abstract boolean atUpperLimit();

}
