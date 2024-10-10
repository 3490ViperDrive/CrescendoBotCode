
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
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.generated.TunerConstants;
import frc.robot.io.SwerveIO;
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

    public boolean isRobotCentric = false;
    public boolean isCrawling = false;
    public double rotationSlowingSeverity = 0.5;

    public static enum DriveMode {
        kFieldCentric,
        kRobotCentric,
        kFieldCentricFacingAngle
    }

    public Drivetrain() {
        m_swerve = TunerConstants.Drivetrain;
        m_headingPID = new PhoenixPIDController(7.5, 0, 0.3);
        m_headingPID.enableContinuousInput(0, 2 * Math.PI);

        m_PathPlannerRequest = new SwerveRequest.ApplyChassisSpeeds().withDriveRequestType(DriveRequestType.OpenLoopVoltage);
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
                new PIDConstants(0.5),
                new PIDConstants(0.5),
                kMaxModuleSpeed,
                kDrivebaseRadius,
                new ReplanningConfig(true, true)), //TODO tune PID
            () -> {
                Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
                if (alliance.isPresent()) return alliance.get() == DriverStation.Alliance.Red; else return false;
            },
            this);
    }

    public Command applyRequestCommand(Supplier<SwerveRequest> requestSupplier) {
        return run(() -> m_swerve.setControl(requestSupplier.get()));
    }

    //TODO: combine drive methods under banner of single Omnicontrol
    public Command driveTeleopCommandGeneric(
        DoubleSupplier translationX,
        DoubleSupplier translationY,
        DoubleSupplier rotationAxis,
        BooleanSupplier robotCentric) {
        return run(() -> {
            double[] stickInputs = {translationX.getAsDouble(), translationY.getAsDouble(), rotationAxis.getAsDouble()};
            double translationMultiplier = applyMultiplier(0, Math.sqrt(DriverXbox.kCrawlTranslationMultiplier));
            softerInputs(stickInputs);

            stickInputs[0] *= translationMultiplier;
            stickInputs[1] *= translationMultiplier;
            stickInputs[2] *= applyMultiplier(rotationSlowingSeverity, Math.sqrt(DriverXbox.kCrawlRotationMultiplier));

            if (isCrawling == true) {
                stickInputs[0] *= 0.225;
                stickInputs[1] *= 0.225;
            } //TODO: add an "else" statement that clamps the normal travel speed if needed

            if (robotCentric.getAsBoolean()) {
                    m_swerve.setControl(m_OpenLoopRobotCentricRequest
                        .withVelocityX(stickInputs[0] * kMaxTranslationSpeed) //Moustafa likes robot-oriented being forward
                        .withVelocityY(stickInputs[1] * kMaxTranslationSpeed) //for some reason
                        .withRotationalRate(stickInputs[2] * kMaxRotationSpeed));
            } else {
                m_swerve.setControl(m_OpenLoopFieldCentricRequest
                        .withVelocityX(stickInputs[0] * kMaxTranslationSpeed)
                        .withVelocityY(stickInputs[1] * kMaxTranslationSpeed)
                        .withRotationalRate(stickInputs[2] * kMaxRotationSpeed));
            }
                
            });
        }

        void softerInputs(double[] inputs){
            for(int i = 0; i < inputs.length; i++){
                inputs[i] = squareInput(applyDeadbandSpecial(inputs[i], 0.2));

            }
        }

    public Command driveTeleopCommand(
        DoubleSupplier leftStickY,
        DoubleSupplier leftStickX,
        DoubleSupplier rightStickX,
        DoubleSupplier crawl, // Prob should use LT for this, but RT could work too
        BooleanSupplier down,
        BooleanSupplier right,
        BooleanSupplier left,
        BooleanSupplier up,
        BooleanSupplier robotCentric) {
        return run(() -> {
            double[] stickInputs = filterXboxControllerInputs(leftStickY.getAsDouble(), leftStickX.getAsDouble(), rightStickX.getAsDouble());
            double translationMultiplier = applyMultiplier(crawl.getAsDouble(), Math.sqrt(DriverXbox.kCrawlTranslationMultiplier));
            stickInputs[0] *= translationMultiplier;
            stickInputs[1] *= translationMultiplier;
            stickInputs[2] *= applyMultiplier(crawl.getAsDouble(), Math.sqrt(DriverXbox.kCrawlRotationMultiplier));
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
    } //Command driveTeleopCommand 

    //TODO cleanup this method

    /**
     * Tell the drive subsystem how it should move in Teleop.
     * No input filtering shenanigans; your controls will have to apply deadband and any other
     * desired input filtering before passing it in.
     * @param xSup [-1, 1] Forward/backward (upfield/downfield) speed.
     * Joysticks default up = -1 so you'll probably want to invert it.
     * @param ySup [-1, 1] Strafe left/right speed.
     * @param thetaSup [-1, 1] (kFieldCentric, kRobotCentric) Turn left/right speed.
     * [0, 360) (kFieldCentricFacingAngle) Angle the robot should face where 0 = downfield.
     * @param mode The drive mode to use.
     * @return a Command that will use the above options to drive the robot.
     */
    public Command driveTeleopSimpleCmd(DoubleSupplier xSup, DoubleSupplier ySup, DoubleSupplier thetaSup, DriveMode mode) {
        Command driveCommand;
        switch(mode) {
            case kRobotCentric:
                driveCommand = run(() -> {
                    m_swerve.setControl(m_OpenLoopRobotCentricRequest
                        .withVelocityX(xSup.getAsDouble() * kMaxTranslationSpeed)
                        .withVelocityY(ySup.getAsDouble() * kMaxTranslationSpeed)
                        .withRotationalRate(thetaSup.getAsDouble() * kMaxRotationSpeed));
                });
            break;
            case kFieldCentricFacingAngle:
                driveCommand = run(() -> {
                    m_swerve.setControl(m_OpenLoopControlledHeadingRequest
                        .withVelocityX(xSup.getAsDouble() * kMaxTranslationSpeed)
                        .withVelocityY(ySup.getAsDouble() * kMaxTranslationSpeed)
                        .withTargetDirection(Rotation2d.fromDegrees(thetaSup.getAsDouble())));
                });
            break;
            case kFieldCentric:
            default:
                driveCommand = run(() -> {
                    m_swerve.setControl(m_OpenLoopFieldCentricRequest
                        .withVelocityX(xSup.getAsDouble() * kMaxTranslationSpeed)
                        .withVelocityY(ySup.getAsDouble() * kMaxTranslationSpeed)
                        .withRotationalRate(thetaSup.getAsDouble() * kMaxRotationSpeed));
                });
            break;
        }
        driveCommand = driveCommand.withName("Drive Teleop Simple (" + mode + ")");
        return driveCommand;
    }

    public Command driveTeleopSimpleCmd(Supplier<Translation2d> translation, DoubleSupplier rotation, DriveMode mode) { //is this bad for performance?
        return driveTeleopSimpleCmd(() -> translation.get().getX(), () -> translation.get().getY(), rotation, mode);
    }


    /* X and Y should be in m/s and no more than the max speed of the robot. Angle should be angle of the robot in degrees relative to downfield */
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

    /* Use this in auto routines to set the initial pose of the robot */
    public Command resetPoseCommand(Pose2d pose) {
        return runOnce(() -> m_swerve.resetPose(pose));
    }

    public void setDriverPerspective() {
        Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            if (alliance.get() == DriverStation.Alliance.Red) {
                DataLogManager.log("Operator perspective red");
                m_swerve.setOperatorPerspectiveForward(Rotation2d.fromDegrees(0));
            } else {
                DataLogManager.log("Operator perspective blue");
                m_swerve.setOperatorPerspectiveForward(Rotation2d.fromDegrees(0));
                //TODO: This seems to be the configuration that produces desired results
            }
        } else {
            DataLogManager.log("Attempted to check operator perspective, but no alliance was found");
        }
    }

    public Command zeroYawCommand() {
        return runOnce(m_swerve::seedFieldRelative);
    }

    public double[] filterXboxControllerInputs(double y, double x, double theta) {
        Translation2d input = new Translation2d(-y, -x); //fix for NW CC+
        if (input.getNorm() > 1) {
            input = new Translation2d(1, input.getAngle());
        }
        double newTheta = squareInput(applyDeadbandSpecial(-theta,DriverXbox.kThumbstickDeadband));
        input = new Translation2d(Math.min(input.getNorm(), applyMultiplier(Math.abs(newTheta), DriverXbox.kRotationDesaturationFactor)), input.getAngle()); //Mildly reduce translation speed to boost rotation speed when moving at full speed
        double[] newInputs = new double[]{input.getX(), input.getY(), newTheta};
        log("Controller X for Ascope", new double[]{-y + 1, input.getX() + 1, 1});
        log("Controller Y for Ascope", new double[]{-x + 1, input.getY() + 1, 1});
        log("filtered controller inputs x, y, theta", newInputs);
        return newInputs;
    }

    private double applyDeadbandSpecial(double value, double deadband) {
        return MathUtil.inverseInterpolate(deadband, 1, MathUtil.applyDeadband(Math.abs(value), deadband)) * Math.signum(value);
    }
    private double squareInput(double value) {
        return Math.pow(Math.abs(value), 2) * Math.signum(value);
    }
    private double applyMultiplier(double value, double multiplier) {
        return 1 - (value * multiplier);
    }

    public Command toggleRobotCentric(){
        return(runOnce(()-> {
            isRobotCentric = !isRobotCentric;
        }));
    }

    public Command toggleCrawling(){
        return(runOnce(()->{
            isCrawling = !isCrawling;
        }));
    }
}