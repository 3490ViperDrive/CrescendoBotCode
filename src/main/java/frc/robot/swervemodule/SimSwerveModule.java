package frc.robot.swervemodule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.ModuleConstants;
import frc.robot.io.SwerveModuleIO;

//TODO add actual simulation of swerve modules
public class SimSwerveModule extends SwerveModuleIO {
    
    Rotation2d commandedAzimuth;
    double traveledDistance;
    double commandedVelocity;
    boolean logging = false;
    ModuleConstants moduleConstants;

    public SimSwerveModule(ModuleConstants moduleConstants) {
        super(moduleConstants);
        this.moduleConstants = moduleConstants;
        this.commandedAzimuth = new Rotation2d();
        this.traveledDistance = 0;
        this.commandedVelocity = 0;
    }

    public void setState(SwerveModuleState state, ControlMode controlMode) {
        commandedAzimuth = state.angle;
        commandedVelocity = state.speedMetersPerSecond;
    }

    public Rotation2d getAzimuth() {
        return commandedAzimuth;
    }

    public double getDistance() {
        return traveledDistance;
    }

    public double getVelocity() {
        return commandedVelocity;
    }
}
