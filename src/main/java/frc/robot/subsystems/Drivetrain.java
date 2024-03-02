package frc.robot.subsystems;

import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.ctre.phoenix6.mechanisms.swerve.SwerveRequest;
import com.ctre.phoenix6.mechanisms.swerve.SwerveModule.DriveRequestType;

import com.ctre.phoenix6.mechanisms.swerve.utility.PhoenixPIDController;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.generated.TunerConstants;
import frc.robot.io.SwerveIO;
import frc.robot.utils.OmniHID;
import monologue.Logged;

import static frc.robot.Constants.ControllerConstants.*;
import static frc.robot.Constants.DrivetrainConstants.*;

import static edu.wpi.first.units.Units.*;

/**
 * Class that extends the Phoenix SwerveDrivetrain class and implements subsystem
 * so it can be used in command-based projects easily.
 */
public class Drivetrain implements Subsystem, Logged {
    private SwerveIO m_swerve;
    private PhoenixPIDController m_headingPID;

    private SwerveRequest.ApplyChassisSpeeds m_PathPlannerRequest;
    private SwerveRequest.RobotCentric m_OpenLoopRobotCentricRequest;
    private SwerveRequest.FieldCentric m_OpenLoopFieldCentricRequest;
    private SwerveRequest.FieldCentricFacingAngle m_OpenLoopControlledHeadingRequest;
    private SwerveRequest.FieldCentricFacingAngle m_ClosedLoopControlledHeadingRequest;

    public Drivetrain() {
        m_swerve = TunerConstants.Drivetrain;
        m_headingPID = new PhoenixPIDController(7.5, 0, 0.3);
        m_headingPID.enableContinuousInput(0, 2 * Math.PI);

        m_PathPlannerRequest = new SwerveRequest.ApplyChassisSpeeds().withDriveRequestType(DriveRequestType.Velocity);
        m_OpenLoopRobotCentricRequest = new SwerveRequest.RobotCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
            .withDeadband(DriverXbox.kThumbstickDeadband)
            .withRotationalDeadband(DriverXbox.kThumbstickDeadband);
        m_OpenLoopFieldCentricRequest = new SwerveRequest.FieldCentric()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
            .withDeadband(DriverXbox.kThumbstickDeadband)
            .withRotationalDeadband(DriverXbox.kThumbstickDeadband);
        m_OpenLoopControlledHeadingRequest = new SwerveRequest.FieldCentricFacingAngle()
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage)
            .withDeadband(DriverXbox.kThumbstickDeadband)
            .withRotationalDeadband(DriverXbox.kThumbstickDeadband);
        m_OpenLoopControlledHeadingRequest.HeadingController = m_headingPID;
        m_ClosedLoopControlledHeadingRequest = new SwerveRequest.FieldCentricFacingAngle()
            .withDriveRequestType(DriveRequestType.Velocity);
        m_ClosedLoopControlledHeadingRequest.HeadingController = m_headingPID; //might need a separate PhoenixPIDController with separate gains for this

        AutoBuilder.configureHolonomic(
            m_swerve::getPose,
            m_swerve::resetPose,
            m_swerve::getChassisSpeeds,
            (desiredSpeeds) -> {
                m_swerve.setControl(m_PathPlannerRequest.withSpeeds(desiredSpeeds));
            },
            new HolonomicPathFollowerConfig(
                new PIDConstants(5),
                new PIDConstants(5),
                kMaxModuleSpeed,
                kDrivebaseRadius,
                new ReplanningConfig(true, true)), //TODO tune PID
            () -> {
                Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
                if (alliance.isPresent()) return alliance.get() == DriverStation.Alliance.Red; else return false;
            },
            this);
    }

    @Override
    public void periodic() {}

    public Command applyRequestCommand(Supplier<SwerveRequest> requestSupplier) {
        return run(() -> m_swerve.setControl(requestSupplier.get()));
    }

    /** Do not use for autonomous routines */
    public Command driveTeleopCommand(
        Supplier<double[]> inputs,
        DoubleSupplier crawl, // Prob should use LT for this, but RT could work too
        BooleanSupplier down,
        BooleanSupplier right,
        BooleanSupplier left,
        BooleanSupplier up,
        BooleanSupplier robotCentric) {
        return run(() -> {
            double[] stickInputs = inputs.get();
            double translationMultiplier = OmniHID.applyMultiplier(crawl.getAsDouble(), Math.sqrt(DriverXbox.kCrawlTranslationMultiplier));
            stickInputs[0] *= translationMultiplier;
            stickInputs[1] *= translationMultiplier;
            stickInputs[2] *= OmniHID.applyMultiplier(crawl.getAsDouble(), Math.sqrt(DriverXbox.kCrawlRotationMultiplier));
            if (robotCentric.getAsBoolean()) {
                m_swerve.setControl(m_OpenLoopRobotCentricRequest
                        .withVelocityX(-stickInputs[0] * kMaxTranslationSpeed) //Robot centric will probably just be used for intaking,
                        .withVelocityY(-stickInputs[1] * kMaxTranslationSpeed) //so controls are inverted so driving via intake cam makes sense
                        .withRotationalRate(stickInputs[2] * kMaxRotationSpeed));
            } else {
                if (up.getAsBoolean() || down.getAsBoolean() || left.getAsBoolean() || right.getAsBoolean()) {
                    Rotation2d desiredAngle;
                    if (down.getAsBoolean()) {
                        desiredAngle = Rotation2d.fromDegrees(180);
                    } else if (right.getAsBoolean()) {
                        desiredAngle = Rotation2d.fromDegrees(300);
                    } else if (left.getAsBoolean()) {
                        desiredAngle = Rotation2d.fromDegrees(90);
                    } else { //Must be up
                        desiredAngle = Rotation2d.fromDegrees(0);
                    }
                    m_swerve.setControl(m_OpenLoopControlledHeadingRequest
                        .withVelocityX(stickInputs[0] * kMaxTranslationSpeed)
                        .withVelocityY(stickInputs[1] * kMaxTranslationSpeed)
                        .withTargetDirection(desiredAngle));
                } else {
                    m_swerve.setControl(m_OpenLoopFieldCentricRequest
                        .withVelocityX(stickInputs[0] * kMaxTranslationSpeed)
                        .withVelocityY(stickInputs[1] * kMaxTranslationSpeed)
                        .withRotationalRate(stickInputs[2] * kMaxRotationSpeed));
                }
            }
        });
    }

    /** X and Y should be in m/s and no more than the max speed of the robot. Angle should be angle of the robot in degrees relative to downfield */
    public Command driveAutoCommand(double x, double y, double angle) {
        return run(() -> m_swerve.setControl(m_ClosedLoopControlledHeadingRequest
            .withTargetDirection(Rotation2d.fromDegrees(angle))
            .withVelocityX(x)
            .withVelocityY(y)));
    }

    public Command sysIDTranslationCommand(double volts) {
        return run(() -> m_swerve.setControl(new SwerveRequest.SysIdSwerveTranslation()
            .withVolts(Volts.of(volts))));
    }

    /** Use this in auto routines to set the initial pose of the robot */
    public Command resetPoseCommand(Pose2d pose) {
        return runOnce(() -> m_swerve.resetPose(pose));
    }

    public Command zeroYawCommand() {
        return runOnce(m_swerve::seedFieldRelative);
    }
}