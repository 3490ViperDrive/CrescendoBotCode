package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ControllerConstants;
import frc.robot.io.GyroIO;
import frc.robot.io.SwerveModuleIO;
import frc.robot.io.gyro.NavXGyro;
import frc.robot.io.swervemodule.CTRESwerveModule;
import frc.robot.io.swervemodule.SimSwerveModule;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import monologue.Logged;
import monologue.Annotations.Log;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static frc.robot.Constants.DrivetrainConstants.*;

import java.util.Optional;
import java.util.function.DoubleSupplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;


public class Drivetrain extends SubsystemBase implements Logged {
    
    private GyroIO m_gyro;
    private SwerveModuleIO[] m_swerveModules;
    private SwerveDrivePoseEstimator m_poseEstimator;
    @Log.File
    private Pose2d m_pose;
    private Field2d m_poseField;

    @Log.NT
    Rotation2d m_yawOffset;
    @Log.NT
    boolean fieldOriented;

    public enum ControlMode {
        kOpenLoop,
        kClosedLoop
    } 

    public Drivetrain() {
        m_gyro = new NavXGyro();
        m_gyro.zeroGyro();
        fieldOriented = false;
        if (RobotBase.isReal()) {
            m_swerveModules = new SwerveModuleIO[] {
            new CTRESwerveModule(kFrontLeftModule),
            new CTRESwerveModule(kFrontRightModule),
            new CTRESwerveModule(kBackRightModule),
            new CTRESwerveModule(kBackLeftModule)
            };
        } else {
            m_swerveModules = new SwerveModuleIO[] {
            new SimSwerveModule(kFrontLeftModule),
            new SimSwerveModule(kFrontRightModule),
            new SimSwerveModule(kBackRightModule),
            new SimSwerveModule(kBackLeftModule)
            };
        }
        

        //TODO FINISh ODOMETRY
        m_pose = new Pose2d(0, 0, Rotation2d.fromDegrees(180));
        m_poseField = new Field2d();
        m_poseEstimator = new SwerveDrivePoseEstimator(kKinematics, m_gyro.getYaw(), getSwerveModulePositions(), m_pose);

        AutoBuilder.configureHolonomic(
            this::getPose,
            this::resetPose,
            this::getChassisSpeeds,
            this::driveClosedLoopCommand,
            new HolonomicPathFollowerConfig(new PIDConstants(1), new PIDConstants(1), kMaxModuleSpeed, Math.hypot(kTrackWidth/2, kWheelbase/2), new ReplanningConfig()), //TODO tune PID
            () -> {
                Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
                if (alliance.isPresent()) return alliance.get() == DriverStation.Alliance.Red; else return false;
            },
            this);

        /*
        for (SwerveModuleIO module : m_swerveModules) {
            module.dashboardInit();
        }
        */
    }

    @Override
    public void periodic() {
        
        /*
        for (SwerveModuleIO module : m_swerveModules) {
            module.dashboardPeriodic();
        }
        SmartDashboard.putBoolean("Field Oriented?", fieldOriented);
        */
        m_pose = m_poseEstimator.update(m_gyro.getYaw(), getSwerveModulePositions());
        m_poseField.setRobotPose(m_pose);
        SmartDashboard.putData(m_poseField);
    }

    //TODO args could probably be more descriptive
    //TODO change SwerveModuleIO.ControlMode to ControlMode once odometry is added
    public void drive(double x, double y, double theta, boolean fieldRelative, SwerveModuleIO.ControlMode controlMode) {
        if (controlMode == SwerveModuleIO.ControlMode.kOpenLoop) {
            x *= kMaxTranslationSpeed;
            y *= kMaxTranslationSpeed;
            theta *= kMaxRotationSpeed;
        }
        ChassisSpeeds desiredChassisSpeeds;
        if (fieldRelative) {
            desiredChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(x, y, theta, m_gyro.getYaw());
        } else {
            desiredChassisSpeeds = new ChassisSpeeds(x, y, theta);
        }
        drive(desiredChassisSpeeds, controlMode);
    }

    //Pathplanner momennt
    public void drive(ChassisSpeeds desiredChassisSpeeds, SwerveModuleIO.ControlMode controlMode) {
        log("Desired ChassisSpeeds", desiredChassisSpeeds);
        SwerveModuleState[] desiredModuleStates = kKinematics.toSwerveModuleStates(desiredChassisSpeeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredModuleStates, desiredChassisSpeeds, kMaxModuleSpeed, kMaxTranslationSpeed, kMaxRotationSpeed);
        setModuleStates(desiredModuleStates, SwerveModuleIO.ControlMode.kOpenLoop); //TODO set to setModuleStates(desiredModuleStates, controlMode); once closed loop is confirmed to work
        //Only needed for AdvantageScope
        log("Desired Swerve State", new double[] {
            desiredModuleStates[0].angle.getDegrees(), desiredModuleStates[0].speedMetersPerSecond,
            desiredModuleStates[1].angle.getDegrees(), desiredModuleStates[1].speedMetersPerSecond,
            desiredModuleStates[2].angle.getDegrees(), desiredModuleStates[2].speedMetersPerSecond,
            desiredModuleStates[3].angle.getDegrees(), desiredModuleStates[3].speedMetersPerSecond});
    }

    public void drive (Translation2d translation, double theta, boolean fieldRelative, SwerveModuleIO.ControlMode controlMode) {
        drive(translation.getX(), translation.getY(), theta, fieldRelative, controlMode);
    }
    
    public Command driveOpenLoopCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier theta) {
        //Fwd, left, and CCW are postive for ChassisSpeeds
        return this.run(() -> drive(filterXY(-x.getAsDouble(), -y.getAsDouble()), filterAxis(-theta.getAsDouble()), fieldOriented, SwerveModuleIO.ControlMode.kOpenLoop)); //TODO fix this awful command
    }

    public Command driveClosedLoopCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier theta, boolean fieldRelative) {
        //Fwd, left, and CCW are postive for ChassisSpeeds
        return this.run(() -> drive(-x.getAsDouble(), -y.getAsDouble(), filterAxis(-theta.getAsDouble()), fieldRelative, SwerveModuleIO.ControlMode.kClosedLoop)); //TODO fix this awful command
    }

    public Command driveClosedLoopCommand(ChassisSpeeds chassisSpeeds) {
        return this.run(() -> drive(chassisSpeeds, SwerveModuleIO.ControlMode.kClosedLoop));
    }

    //Throttle should be [0, 1]
    public Command driveOpenLoopThrottleCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier theta, DoubleSupplier throttle) {
        DoubleSupplier throttleMultiplier = () -> MathUtil.interpolate(0.4, 1, throttle.getAsDouble());
        log("throttle multiplier", throttleMultiplier.getAsDouble());
        return this.run(() -> drive(new Translation2d(-x.getAsDouble(), -y.getAsDouble()).times(throttleMultiplier.getAsDouble()), filterAxis(-theta.getAsDouble()), fieldOriented, SwerveModuleIO.ControlMode.kOpenLoop));
    }

    public Command zeroGyroCommand(double offset) {
        return this.runOnce(() -> m_gyro.zeroGyro(offset));
    }

    public Command toggleFieldOrientedCommand() {
        return this.runOnce(() -> {this.fieldOriented = !fieldOriented;});
    }

    private Translation2d filterXY(double x, double y) {
        Translation2d translation = new Translation2d(x, y);
        Translation2d circularizedTranslation = new Translation2d(
            x * 1.08 * Math.abs(Math.cos(translation.getAngle().getRadians())),
            y * 1.08 * Math.abs(Math.sin(translation.getAngle().getRadians())));
        return new Translation2d(filterAxis(Math.min(circularizedTranslation.getNorm(), 1)), translation.getAngle());
    }

    private double filterAxis(double value) {
        double deadbandedValue = MathUtil.inverseInterpolate(ControllerConstants.kControllerDeadband, 1, MathUtil.applyDeadband(Math.abs(value), ControllerConstants.kControllerDeadband));
        return Math.pow(deadbandedValue, 2) * Math.signum(value);
    }

    public void setModuleStates(SwerveModuleState[] states, SwerveModuleIO.ControlMode controlMode) {
        SwerveDriveKinematics.desaturateWheelSpeeds(states, kMaxModuleSpeed); //TODO should this be kMaxModuleSpeed or kMaxTranslationSpeed?

        for (int i = 0; i <= 3; i++) {
            m_swerveModules[i].setState(states[i], controlMode);
        }
    }

    //Only needed for AdvantageScope
    @Log.NT
    double[] getTrueSwerveState() {
        return new double[]{
            m_swerveModules[0].getAzimuthDegrees(), m_swerveModules[0].getVelocity(),
            m_swerveModules[1].getAzimuthDegrees(), m_swerveModules[1].getVelocity(),
            m_swerveModules[2].getAzimuthDegrees(), m_swerveModules[2].getVelocity(),
            m_swerveModules[3].getAzimuthDegrees(), m_swerveModules[3].getVelocity()};
    }

    @Log.File
    SwerveModulePosition[] getSwerveModulePositions() {
        return new SwerveModulePosition[]{
            new SwerveModulePosition(m_swerveModules[0].getDistance(), m_swerveModules[0].getAzimuth()),
            new SwerveModulePosition(m_swerveModules[1].getDistance(), m_swerveModules[1].getAzimuth()),
            new SwerveModulePosition(m_swerveModules[2].getDistance(), m_swerveModules[2].getAzimuth()),
            new SwerveModulePosition(m_swerveModules[3].getDistance(), m_swerveModules[3].getAzimuth())};
    }

    @Log.File
    SwerveModuleState[] getSwerveModuleStates() {
        return new SwerveModuleState[]{
            new SwerveModuleState(m_swerveModules[0].getVelocity(), m_swerveModules[0].getAzimuth()),
            new SwerveModuleState(m_swerveModules[1].getVelocity(), m_swerveModules[1].getAzimuth()),
            new SwerveModuleState(m_swerveModules[2].getVelocity(), m_swerveModules[2].getAzimuth()),
            new SwerveModuleState(m_swerveModules[3].getVelocity(), m_swerveModules[3].getAzimuth())};
    }

    @Log.File
    Pose2d getPose() {
        return m_pose;
    }

    public void resetPose(Pose2d newPose) {
        m_pose = newPose;
    }

    public ChassisSpeeds getChassisSpeeds() {
        return kKinematics.toChassisSpeeds(getSwerveModuleStates());
    }
}