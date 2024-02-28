package frc.robot.io;

import edu.wpi.first.math.geometry.Rotation2d;
import monologue.Logged;
import monologue.Annotations.Log;

public abstract class PivotIO implements Logged {
    
    public abstract void setAngle(Rotation2d angle);

    @Log
    public abstract Rotation2d getAngle();

    @Log
    public abstract boolean atUpperLimit();

    @Log
    public abstract boolean atLowerLimit();

}
