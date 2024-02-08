package frc.robot;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CANcoderConfigurator;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants.DrivetrainConstants.AzimuthMotorClosedLoopConstants;
import frc.robot.Constants.DrivetrainConstants.DriveMotorClosedLoopConstants;

import static frc.robot.Constants.DrivetrainConstants.*;

public final class CTREConfigurer { //TODO is this a good application of the singleton paradigm?
    public final TalonFXConfiguration kDriveMotor;
    public final TalonFXConfiguration kAzimuthMotor;
    public final CANcoderConfiguration kAbsEncoder;

    private static final CTREConfigurer instance = new CTREConfigurer();

    private CTREConfigurer() {
        kDriveMotor = new TalonFXConfiguration();

        kDriveMotor.MotorOutput.Inverted = kInvertDriveMotor;
        kDriveMotor.MotorOutput.NeutralMode = kDriveMotorIdleMode;

        kDriveMotor.Feedback.SensorToMechanismRatio = kDriveGearing;
        kDriveMotor.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RotorSensor;

        kDriveMotor.CurrentLimits.StatorCurrentLimitEnable = kEnableDriveCurrentLimits;
        kDriveMotor.CurrentLimits.SupplyCurrentLimitEnable = kEnableDriveCurrentLimits;
        kDriveMotor.CurrentLimits.StatorCurrentLimit = kDriveStatorCurrentLimit;
        kDriveMotor.CurrentLimits.SupplyCurrentLimit = kDriveSupplyCurrentLimit;
        kDriveMotor.CurrentLimits.SupplyCurrentThreshold = kDriveSupplyCurrentLimitThreshold;
        kDriveMotor.CurrentLimits.SupplyTimeThreshold = kDriveSupplyCurrentLimitTime;

        kDriveMotor.Slot0.kP = DriveMotorClosedLoopConstants.kP;
        kDriveMotor.Slot0.kI = DriveMotorClosedLoopConstants.kI;
        kDriveMotor.Slot0.kD = DriveMotorClosedLoopConstants.kD;
        kDriveMotor.Slot0.kS = DriveMotorClosedLoopConstants.kS;
        kDriveMotor.Slot0.kV = DriveMotorClosedLoopConstants.kV;

        kAzimuthMotor = new TalonFXConfiguration();

        kAzimuthMotor.MotorOutput.Inverted = kInvertAzimuthMotor;
        kAzimuthMotor.MotorOutput.NeutralMode = kAzimuthMotorIdleMode;

        kAzimuthMotor.Feedback.SensorToMechanismRatio = kAzimuthGearing;
        kAzimuthMotor.Feedback.RotorToSensorRatio = kAzimuthGearing;
        //Set to RotorSensor for internal sensor seeding strategy, RemoteCanCoder for absolute encoder all the time, or FusedCanCoder for sensor fusion strategy (requires Phoenix Pro)
        kAzimuthMotor.Feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RotorSensor;

        kAzimuthMotor.CurrentLimits.SupplyCurrentLimitEnable = kEnableAzimuthCurrentLimits;
        kAzimuthMotor.CurrentLimits.SupplyCurrentLimit = kAzimuthSupplyCurrentLimit;
        kAzimuthMotor.CurrentLimits.SupplyCurrentThreshold = kAzimuthSupplyCurrentLimitThreshold;
        kAzimuthMotor.CurrentLimits.SupplyTimeThreshold = kAzimuthSupplyCurrentLimitTime;

        kAzimuthMotor.Slot0.kP = AzimuthMotorClosedLoopConstants.kP;
        kAzimuthMotor.Slot0.kI = AzimuthMotorClosedLoopConstants.kI;
        kAzimuthMotor.Slot0.kD = AzimuthMotorClosedLoopConstants.kD;

        kAzimuthMotor.ClosedLoopGeneral.ContinuousWrap = true;

        kAbsEncoder = new CANcoderConfiguration();

        kAbsEncoder.MagnetSensor.SensorDirection = kInvertAbsEncoder;
        kAbsEncoder.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Unsigned_0To1;
    }

    //TODO does this apply offset as expected?
    public static CANcoderConfiguration getAbsEncoderConfigWithMagnetOffset(Rotation2d offset) {
        CANcoderConfiguration newAbsEncoderConfig = CTREConfigurer.getInstance().kAbsEncoder;
        newAbsEncoderConfig.MagnetSensor.MagnetOffset = offset.getRotations();
        return newAbsEncoderConfig;
    }

    public static CTREConfigurer getInstance() {
        return instance;
    }

    //True if configuration was successful, false if a configuration failed
    public static boolean configureModule(TalonFX driveMotor, TalonFX azimuthMotor, CANcoder absEncoder, Rotation2d absEncoderOffset, String moduleName) {
        boolean configurationSuccess = true;
        configurationSuccess = configurationSuccess && configureMotor(CTREConfigurer.getInstance().kDriveMotor, driveMotor, moduleName + " Drive Motor");
        configurationSuccess = configurationSuccess && configureMotor(CTREConfigurer.getInstance().kAzimuthMotor, azimuthMotor, moduleName + " Azimuth Motor");
        configurationSuccess = configurationSuccess && configureEncoder(getAbsEncoderConfigWithMagnetOffset(absEncoderOffset), absEncoder, moduleName + " Absolute Encoder");
        return configurationSuccess;
        //TODO if persistent alerts are added, have this put an alert there if config fails
    }

    //Clears all current configurations, then attempts to fully configure the motor. Name is optional, use empty string to omit
    public static boolean configureMotor(TalonFXConfiguration config, TalonFX motor, String name) {
        boolean configurationSuccess = true;
        if (name == "") {
            name = "Falcon with CAN ID " + motor.getDeviceID();
        } else {
            name += " (CAN ID " + motor.getDeviceID() + ")";
        }
        TalonFXConfigurator configurator = motor.getConfigurator();
        StatusCode status = configurator.apply(config);
        //TODO if there is an equivalent of v5's ConfigFactoryDefault, put it here with nested if structure similar to below
        if (status != StatusCode.OK) {
            DataLogManager.log(name + " failed configuration with status code " + status.toString() + "! Retrying in 200ms...");
            Timer.delay(0.2); //TODO is this delay adequate?
            status = configurator.apply(config);
            if (status != StatusCode.OK) {
                DataLogManager.log(name + " failed configuration with status code " + status.toString() + "! Aborting configuration!");
                configurationSuccess = false;
                
            }
        }
        if (configurationSuccess) {
            DataLogManager.log(name + " successfully configured");
        }
        return configurationSuccess;
    }

    //TODO this is a lot of duplicated code, could the configure methods be combined into one?
    public static boolean configureEncoder(CANcoderConfiguration config, CANcoder encoder, String name) {
        boolean configurationSuccess = true;
        if (name == "") {
            name = "CANcoder with CAN ID " + encoder.getDeviceID();
        } else {
            name += " (CAN ID " + encoder.getDeviceID() + ")";
        }
        CANcoderConfigurator configurator = encoder.getConfigurator();
        StatusCode status = configurator.apply(config);
        //TODO if there is an equivalent of v5's ConfigFactoryDefault, put it here with nested if structure similar to below
        if (status != StatusCode.OK) {
            DataLogManager.log(name + " failed configuration with status code " + status.toString() + "! Retrying in 200ms...");
            Timer.delay(0.2); //TODO is this delay adequate?
            status = configurator.apply(config);
            if (status != StatusCode.OK) {
                DataLogManager.log(name + " failed configuration with status code " + status.toString() + "! Aborting configuration!");
                configurationSuccess = false;
                
            }
        }
        if (configurationSuccess) {
            DataLogManager.log(name + " successfully configured");
        }
        return configurationSuccess;
    }

}