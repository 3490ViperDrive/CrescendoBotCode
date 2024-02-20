package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrain;
import com.ctre.phoenix6.mechanisms.swerve.SwerveDrivetrainConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModuleConstants;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.SteerRequestType;
import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest.FieldCentricFacingAngle;
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
import edu.wpi.first.wpilibj2.command.PrintCommand;
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
                this.setControl(m_PathPlannerDriveRequest.withSpeeds(desiredSpeeds).withDriveRequestType(DriveRequestType.Velocity));
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

    public Command applyRequestCommand(Supplier<SwerveRequest> requestSupplier) {
        return run(() -> this.setControl(requestSupplier.get()));
    }

    /* Do not use for autonomous routines */
    public Command driveTeleopCommand(
        DoubleSupplier leftStickY,
        DoubleSupplier leftStickX,
        DoubleSupplier rightStickX,
        DoubleSupplier throttle, // Prob should use LT for this, but RT could work too
        BooleanSupplier a,
        BooleanSupplier b,
        BooleanSupplier x,
        BooleanSupplier y) {
        return run(() -> {
            double[] stickInputs = filterXboxControllerInputs(leftStickY.getAsDouble(), leftStickX.getAsDouble(), rightStickX.getAsDouble());
            this.setControl(new SwerveRequest.FieldCentric()
                .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
                .withVelocityX(stickInputs[0] * 12)
                .withVelocityY(stickInputs[1] * 12)
                .withRotationalRate(stickInputs[2] * kMaxRotationSpeed));
        });
    }

    /* X and Y should be in m/s and no more than the max speed of the robot. Angle should be angle of the robot in degrees relative to downfield */
    public Command driveAutoCommand(double x, double y, double angle) {
        return run(() -> this.setControl(new SwerveRequest.FieldCentricFacingAngle()
                .withTargetDirection(Rotation2d.fromDegrees(angle))
            .withDriveRequestType(DriveRequestType.Velocity)
            .withVelocityX(x)
            .withVelocityY(y)));
    }

    //TODO move to Omnicontroller 2 lib if created
    public double[] filterXboxControllerInputs(double y, double x, double theta) {
        Translation2d input = new Translation2d(-y, -x); //fix for NW CC+
        // Translation2d maxInput;
        // //account for square part of rounded square
        // if (Math.abs(input.getX()) >= Math.abs(input.getY())) {
        //     maxInput = new Translation2d(1, Math.abs(input.getY()) * (input.getX() != 0 ? 1 / Math.abs(input.getX()) : 0));
        // } else {
        //     maxInput = new Translation2d(Math.abs(input.getX()) * (input.getY() != 0 ? 1 / Math.abs(input.getY()) : 0), 1);
        // }
        // //account for rounded part of the rounded square
        // if (quadrantAngle >= 26 && quadrantAngle <= 63) {
        //     input = input.div(1.118);
        // } else {
        //     input = input.div(maxInput.getNorm());
        // }
        // input = new Translation2d(squareInput(applyDeadbandSpecial(input.getNorm())), input.getAngle());
        double quadrantAngle = Math.abs(input.getAngle().getDegrees()) % 90;
        input = input.div(1);
        if (Math.abs(x) >= 0.99 || Math.abs(y) >= 0.99) {
            input = new Translation2d(1, input.getAngle());
        } else {
            input = new Translation2d(squareInput(applyDeadbandSpecial(input.getNorm())), input.getAngle());
        }
        if (input.getNorm() > 1) {
            input = new Translation2d(1, input.getAngle());
        }
        double[] newInputs = new double[]{input.getX(), input.getY(), squareInput(applyDeadbandSpecial(-theta))};
        log("Angle mod 90", quadrantAngle);
        log("Controller X for Ascope", new double[]{-y + 1, input.getX() + 1, 1});
        log("Controller Y for Ascope", new double[]{-x + 1, input.getY() + 1, 1});
        return newInputs;
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

//TODO add logging and telemetry