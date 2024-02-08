package frc.robot.io;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.ModuleConstants;
import monologue.Logged;
import monologue.Annotations.Log;

public abstract class SwerveModuleIO implements Logged {

    public enum ControlMode {
        kOpenLoop,
        kClosedLoop
    }

    public SwerveModuleIO(ModuleConstants moduleConstants) {}
    
    public abstract Rotation2d getAzimuth();

    //Subclasses should not annotate their implementations of getters with @Log.NT
    @Log.NT
    public double getAzimuthDegrees() {
        return getAzimuth().getDegrees();
    }
    
    public abstract void setState(SwerveModuleState state, ControlMode controlMode);

    @Log.NT
    public abstract double getDistance(); //meters

    @Log.NT
    public abstract double getVelocity(); //don't use this for odometry

    /*
    public void dashboardInit();
    
    public void dashboardPeriodic();
    */
}
