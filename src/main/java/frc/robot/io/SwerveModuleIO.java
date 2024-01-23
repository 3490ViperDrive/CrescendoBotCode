package frc.robot.io;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.ModuleConstants;

public abstract class SwerveModuleIO {

    public enum ControlMode {
        kOpenLoop,
        kClosedLoop
    }

    public SwerveModuleIO(ModuleConstants moduleConstants) {}
    
    public abstract Rotation2d getAzimuth();
    
    public abstract void setState(SwerveModuleState state, ControlMode controlMode);

    public abstract double getDistance(); //meters

    public abstract double getVelocity(); //don't use this for odometry

    public void dashboardInit() {};

    public void dashboardPeriodic() {};

}
