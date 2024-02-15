package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.Supplier;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Constants.ControllerConstants;
import monologue.Logged;

import static frc.robot.Constants.DrivetrainConstants.*;

/**
 * Class that extends the Phoenix SwerveDrivetrain class and implements subsystem
 * so it can be used in command-based projects easily.
 */
public class Drivetrain extends SwerveDrivetrain implements Subsystem, Logged {
    private static final double kSimLoopPeriod = 0.005; // 5 ms
    private Notifier m_simNotifier = null;
    private double m_lastSimTime;

    private SwerveRequest.ApplyChassisSpeeds m_PathPlannerDriveRequest = new SwerveRequest.ApplyChassisSpeeds();

    public Drivetrain(SwerveDrivetrainConstants drivetrainConstants, double OdometryUpdateFrequency, SwerveModuleConstants... modules) {
        super(drivetrainConstants, OdometryUpdateFrequency, modules);
        if (Utils.isSimulation()) {
            startSimThread();
        }
        configurePathPlanner();
    }
    public Drivetrain(SwerveDrivetrainConstants driveTrainConstants, SwerveModuleConstants... modules) {
        super(driveTrainConstants, modules);
        if (Utils.isSimulation()) {
            startSimThread();
        }
        configurePathPlanner();
    }

    private void configurePathPlanner() {
        AutoBuilder.configureHolonomic(
            this::getPose,
            this::resetPose,
            this::getChassisSpeeds,
            (desiredSpeeds) -> {
                this.setControl(m_PathPlannerDriveRequest.withSpeeds(desiredSpeeds));
            },
            new HolonomicPathFollowerConfig(
                new PIDConstants(5),
                new PIDConstants(5),
                kMaxModuleSpeed,
                kDrivebaseRadius,
                new ReplanningConfig()), //TODO tune PID
            () -> {
                Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
                if (alliance.isPresent()) return alliance.get() == DriverStation.Alliance.Red; else return false;
            },
            this);
    }

    public Pose2d getPose() {
        return this.getState().Pose;
    }

    private void resetPose(Pose2d pose) {
        this.seedFieldRelative(pose);
    }

    public ChassisSpeeds getChassisSpeeds() {
        return this.m_kinematics.toChassisSpeeds(this.getState().ModuleStates);  
    }

    public Command applyRequest(Supplier<SwerveRequest> requestSupplier) {
        return run(() -> this.setControl(requestSupplier.get()));
    }

    //TODO move to Omnicontroller 2 lib if created
    public Pose2d filterXboxControllerInputs(double y, double x, double theta) {
        Translation2d maxInput;
        Translation2d input = new Translation2d(-y, -x); //
        if (Math.abs(input.getX()) >= Math.abs(input.getY())) {
            maxInput = new Translation2d(1, Math.abs(input.getY()) * (input.getX() != 0 ? 1 / Math.abs(input.getX()) : 0));
        } else {
            maxInput = new Translation2d(Math.abs(input.getX()) * (input.getY() != 0 ? 1 / Math.abs(input.getY()) : 0), 1);
        }
        input = input.div(maxInput.getNorm());
        input = new Translation2d(squareInput(applyDeadbandSpecial(input.getNorm())), input.getAngle());
        return new Pose2d(input, new Rotation2d(squareInput(applyDeadbandSpecial(theta))));
    }

    private double applyDeadbandSpecial(double value) {
        return MathUtil.inverseInterpolate(ControllerConstants.kControllerDeadband, 1, MathUtil.applyDeadband(Math.abs(value), ControllerConstants.kControllerDeadband)) * Math.signum(value);
    }
    private double squareInput(double value) {
        return Math.pow(Math.abs(value), 2) * Math.signum(value);
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
}
