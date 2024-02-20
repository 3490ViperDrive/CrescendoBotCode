package frc.robot.io;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;
import monologue.Logged;
import monologue.Annotations.Log;

/**
 * Basically CommandSwerveDrivetrain from ctre template project with some additions to make PathPlanner happy
 * but adapted for the subsystem/IO pattern. All subsystem functionality handled by Drivetrain
 */
public class SwerveIO extends SwerveDrivetrain implements Logged {
    private static final double kSimLoopPeriod = 0.005; // 5 ms
    private Notifier m_simNotifier = null;
    private double m_lastSimTime;

    public SwerveIO(SwerveDrivetrainConstants driveTrainConstants, double OdometryUpdateFrequency, SwerveModuleConstants... modules) {
        super(driveTrainConstants, OdometryUpdateFrequency, modules);
        if (Utils.isSimulation()) {
            startSimThread();
        }
    }
    public SwerveIO(SwerveDrivetrainConstants driveTrainConstants, SwerveModuleConstants... modules) {
        super(driveTrainConstants, modules);
        if (Utils.isSimulation()) {
            startSimThread();
        }
    }

    private void startSimThread() {
        m_lastSimTime = Utils.getCurrentTimeSeconds();

        /* Run simulation at a faster rate so PID gains behave more reasonably */
        m_simNotifier = new Notifier(() -> {
            final double currentTime = Utils.getCurrentTimeSeconds();
            double deltaTime = currentTime - m_lastSimTime;
            m_lastSimTime = currentTime;

            /* use the measured time delta, get battery voltage from WPILib */
            updateSimState(deltaTime, RobotController.getBatteryVoltage());
        });
        m_simNotifier.startPeriodic(kSimLoopPeriod);
    }

    @Log
    public Pose2d getPose() {
        return this.getState().Pose;
    }

    public void resetPose(Pose2d pose) {
        this.seedFieldRelative(pose);
    }

    public ChassisSpeeds getChassisSpeeds() {
        return this.m_kinematics.toChassisSpeeds(this.getState().ModuleStates);  
    }

    @Log
    private SwerveModuleState[] getModuleStates() {
        return getState().ModuleStates;
    }

    @Log
    private SwerveModuleState[] getModuleTargets() {
        return getState().ModuleTargets;
    }
}
