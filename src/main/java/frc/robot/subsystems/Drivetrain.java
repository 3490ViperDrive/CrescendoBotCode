package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
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

import static frc.robot.Constants.DrivetrainConstants.*;

import java.util.function.DoubleSupplier;


public class Drivetrain extends SubsystemBase implements Logged {
    
    private GyroIO m_gyro;
    private SwerveModuleIO[] m_swerveModules;
    private SwerveDrivePoseEstimator m_poseEstimator;
    private Pose2d m_pose;

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
        m_pose = new Pose2d(5, 13.5, Rotation2d.fromDegrees(180));
        m_poseEstimator = new SwerveDrivePoseEstimator(kKinematics, m_gyro.getYaw(), getSwerveModulePositions(), m_pose);

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
    }

    //TODO args could probably be more descriptive
    //TODO change SwerveModuleIO.ControlMode to ControlMode once odometry is added
    public void drive(double x, double y, double theta, boolean fieldRelative, SwerveModuleIO.ControlMode controlMode) {
        ChassisSpeeds desiredChassisSpeeds;
        if (fieldRelative) {
            desiredChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(x, y, theta, m_gyro.getYaw());
        } else {
            desiredChassisSpeeds = new ChassisSpeeds(x, y, theta);
        }
        if (controlMode == SwerveModuleIO.ControlMode.kOpenLoop) {
            x *= kMaxTranslationSpeed;
            y *= kMaxTranslationSpeed;
            theta *= kMaxRotationSpeed;
        }
        log("Desired ChassisSpeeds", desiredChassisSpeeds);
        SwerveModuleState[] desiredModuleStates = kKinematics.toSwerveModuleStates(desiredChassisSpeeds);
        setModuleStates(desiredModuleStates, controlMode);
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

    //Throttle should be [0, 1]
    public Command driveOpenLoopThrottleCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier theta, DoubleSupplier throttle) {
        DoubleSupplier throttleMultiplier = () -> MathUtil.interpolate(0.4, 1, throttle.getAsDouble());
        log("throttle multiplier", throttleMultiplier.getAsDouble());
        return this.run(() -> drive(filterXY(-x.getAsDouble(), -y.getAsDouble()).times(throttleMultiplier.getAsDouble()), filterAxis(-theta.getAsDouble()), fieldOriented, SwerveModuleIO.ControlMode.kOpenLoop));
    }

    public Command zeroGyroCommand(double offset) {
        return this.runOnce(() -> m_gyro.zeroGyro(offset));
    }

    public Command toggleFieldOrientedCommand() {
        return this.runOnce(() -> {this.fieldOriented = !fieldOriented;});
    }

    private Translation2d filterXY(double x, double y) {
        Translation2d translation = new Translation2d(x, y);
        return new Translation2d(filterAxis(translation.getNorm()), translation.getAngle());
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
    Pose2d getPose() {
        return m_pose;
    }
}