package frc.robot.swervemodule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib.ModuleConstants;
import frc.robot.io.SwerveModuleIO;

public class SimSwerveModuleLogging extends SwerveModuleIO {
    
    Rotation2d commandedAzimuth;
    double traveledDistance;
    double commandedVelocity;
    boolean logging = false;
    ModuleConstants moduleConstants;
    ShuffleboardTab tab;

    public SimSwerveModuleLogging(ModuleConstants moduleConstants) {
        super(moduleConstants);
        this.moduleConstants = moduleConstants;
        this.commandedAzimuth = new Rotation2d();
        this.traveledDistance = 0;
        this.commandedVelocity = 0;
        tab = Shuffleboard.getTab("simulation");
        this.dashboardInit();
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

    @Override
    public void dashboardInit() {
        super.dashboardInit();
        /*
        tab.addDouble(this.moduleConstants.moduleName + " Azimuth Sim", () -> getAzimuth().getDegrees());
        tab.addDouble(this.moduleConstants.moduleName + " Velocity Sim", () -> getVelocity());
        */
    }

    public void dashboardPeriodic() {
        SmartDashboard.putNumber(moduleConstants.moduleName + " Azimuth Sim2", getAzimuth().getDegrees());
        SmartDashboard.putNumber(moduleConstants.moduleName + " Velocity Sim2", getVelocity());
    }
}
