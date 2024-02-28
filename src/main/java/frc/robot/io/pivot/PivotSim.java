package frc.robot.io.pivot;

import frc.robot.io.PivotIO;
import static frc.robot.Constants.PivotConstants.*;

import edu.wpi.first.math.geometry.Rotation2d;

public class PivotSim extends PivotIO {
    private Rotation2d angle = Rotation2d.fromDegrees(45);

    public PivotSim() {

    }

    public void setAngle(Rotation2d angle) {
        this.angle = Rotation2d.fromDegrees(Math.max(kLowerLimit, Math.min(kUpperLimit, angle.getDegrees())));
    }

    public Rotation2d getAngle() {
        return angle;
    }

    public boolean atLowerLimit() {
        return angle.getDegrees() <= kLowerLimit;
    }

    public boolean atUpperLimit() {
        return angle.getDegrees() >= kUpperLimit;
    }
}
