package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ControllerConstants;
import frc.robot.io.SwerveModuleIO;
import frc.robot.swervemodule.CTRESwerveModule;
import frc.robot.swervemodule.SimSwerveModuleLogging;
import edu.wpi.first.math.MathUtil;

import static frc.robot.Constants.DrivetrainConstants.*;

import java.util.function.DoubleSupplier;

public class Drivetrain extends SubsystemBase {
    
    private AHRS m_gyro;
    private SwerveModuleIO[] m_swerveModules;

    Rotation2d m_yawOffset;
    boolean fieldOriented;

    public enum ControlMode {
        kOpenLoop,
        kClosedLoop
    } 

    public Drivetrain() {
        m_gyro = new AHRS(SPI.Port.kMXP, (byte) 200); //No gyro interface boowomp
        zeroGyro();
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
            new SimSwerveModuleLogging(kFrontLeftModule),
            new SimSwerveModuleLogging(kFrontRightModule),
            new SimSwerveModuleLogging(kBackRightModule),
            new SimSwerveModuleLogging(kBackLeftModule)
            };
        }
        

        //TODO ADD ODOMETRY

        for (SwerveModuleIO module : m_swerveModules) {
            module.dashboardInit();
        }
    }

    @Override
    public void periodic() {
        for (SwerveModuleIO module : m_swerveModules) {
            module.dashboardPeriodic();
        }
        SmartDashboard.putBoolean("Field Oriented?", fieldOriented);
    }

    //TODO move gyro code to independent IO class at some point
    public void zeroGyro() {
        zeroGyro(0);
    }
    
    public void zeroGyro(double offset){
        DataLogManager.log("Gyro zeroed with offset " + offset);
        m_gyro.zeroYaw();
        m_yawOffset = Rotation2d.fromDegrees(offset);
    }

    public Rotation2d getGyroYaw() {
        return m_gyro.getRotation2d().minus(m_yawOffset);
    }

    //TODO args could probably be more descriptive
    //TODO change SwerveModuleIO.ControlMode to ControlMode once odometry is added
    public void drive(double x, double y, double theta, boolean fieldRelative, SwerveModuleIO.ControlMode controlMode) {
        ChassisSpeeds desiredChassisSpeeds;
        if (fieldRelative) {
            desiredChassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(x, y, theta, getGyroYaw());
        } else {
            desiredChassisSpeeds = new ChassisSpeeds(x, y, theta);
        }
        if (controlMode == SwerveModuleIO.ControlMode.kOpenLoop) {
            x *= kMaxTranslationSpeed;
            y *= kMaxTranslationSpeed;
            theta *= kMaxRotationSpeed;
        }
        SmartDashboard.putNumber("commanded Xspeed mps", x);
        SmartDashboard.putNumber("commanded Yspeed mps", y);
        SmartDashboard.putNumber("commanded Thetaspeed mps", theta);
        setModuleStates(kKinematics.toSwerveModuleStates(desiredChassisSpeeds), controlMode);
    }

    public void drive (Translation2d translation, double theta, boolean fieldRelative, SwerveModuleIO.ControlMode controlMode) {
        drive(translation.getX(), translation.getY(), theta, fieldRelative, controlMode);
    }

    public Command driveOpenLoopCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier theta) {
        return this.run(() -> drive(filterXY(x.getAsDouble(), y.getAsDouble()), filterAxis(theta.getAsDouble()), fieldOriented, SwerveModuleIO.ControlMode.kOpenLoop)); //TODO fix this awful command
    }

    public Command zeroGyroCommand(double offset) {
        return this.runOnce(() -> zeroGyro(offset));
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
}

