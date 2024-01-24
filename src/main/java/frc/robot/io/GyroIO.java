package frc.robot.io;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import monologue.Logged;
import monologue.Annotations.Log;

//Gyro yaw should be CCW+ and axes should follow WPILib convention
public abstract class GyroIO implements Logged {

    public abstract void zeroGyro(double offset);

    public void zeroGyro() {
        zeroGyro(0);
    }

    @Log.NT
    public abstract Rotation2d getYaw();

    //Shuffleboard does not know what a struct is
    @Log.NT
    public double getYawDegrees() {
        return getYaw().getDegrees();
    }

    @Log.File
    public abstract Rotation3d getRotation();

    @Log.File
    public abstract Translation3d getAcceleration();
}
