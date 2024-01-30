package frc.robot.io.swervemodule;

import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.lib.ModuleConstants;
import frc.robot.CTREConfigurer;
import frc.robot.io.SwerveModuleIO;
import monologue.Annotations.Log;

import static frc.robot.Constants.DrivetrainConstants.*;

public class CTRESwerveModule extends SwerveModuleIO {
    TalonFX m_driveMotor;
    TalonFX m_azimuthMotor;
    CANcoder m_absEncoder;
    ModuleConstants moduleConstants;

    DutyCycleOut openLoopDriveRequest = new DutyCycleOut(0);
    VelocityVoltage closedLoopDriveRequest = new VelocityVoltage(0);
    PositionVoltage azimuthRequest = new PositionVoltage(0);

    public CTRESwerveModule(ModuleConstants moduleConstants) {
        super(moduleConstants);
        this.moduleConstants = moduleConstants;
        m_driveMotor = new TalonFX(moduleConstants.driveMotorID);
        m_azimuthMotor = new TalonFX(moduleConstants.azimuthMotorID);
        m_absEncoder = new CANcoder(moduleConstants.absEncoderID);

        CTREConfigurer.configureModule(m_driveMotor, m_azimuthMotor, m_absEncoder, moduleConstants.absEncoderOffset, moduleConstants.moduleName);
        m_driveMotor.setPosition(0);
        m_azimuthMotor.setPosition(getAzimuth().getRotations()); //"Seed" the integrated sensor's position via the abs encoder
        //super.dashboardInit();
    }
  
    public void setState(SwerveModuleState state, ControlMode controlMode) {
        SwerveModuleState optimizedState = SwerveModuleState.optimize(state, getMotorAzimuth());
        switch(controlMode) {
            case kOpenLoop:
            m_driveMotor.setControl(openLoopDriveRequest.withOutput(optimizedState.speedMetersPerSecond * kWheelCircumference)); //TODO are these conversions right
            break;
            case kClosedLoop:
            m_driveMotor.setControl(closedLoopDriveRequest.withVelocity(optimizedState.speedMetersPerSecond * kWheelCircumference)); //TODO check logs if 4 * kDriveGearing was wrong and if kWheelCircumference theoretically fixes it
            break;
            default:
            break;
        }
        if (Math.abs(optimizedState.speedMetersPerSecond) > 0.01) { //Thanks 364
            m_azimuthMotor.setControl(azimuthRequest.withPosition(optimizedState.angle.getRotations())); 
        }
    }

    public Rotation2d getAzimuth() {
        return Rotation2d.fromRotations(m_absEncoder.getAbsolutePosition().getValueAsDouble());
    }

    @Log.NT
    public Rotation2d getMotorAzimuth() {
        return Rotation2d.fromRotations(m_azimuthMotor.getPosition().getValueAsDouble());
    }

    public double getDistance() {
        return m_driveMotor.getPosition().getValueAsDouble() * kWheelCircumference;
    }

    public double getVelocity() {
        return m_driveMotor.getVelocity().getValueAsDouble() * kWheelCircumference;
    }
}
