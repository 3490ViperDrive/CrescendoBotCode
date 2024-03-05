package frc.robot.utils;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CANcoderConfigurator;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.SoftwareLimitSwitchConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.configs.VoltageConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Timer;

import frc.robot.Constants.LiftConstants;
import frc.robot.Constants.PivotConstants;

public final class CTREConfigurer {

    private static final CTREConfigurer instance = new CTREConfigurer();

    public final TalonFXConfiguration pivotConfig = new TalonFXConfiguration();
    public final TalonFXConfiguration liftConfig = new TalonFXConfiguration();

    private CTREConfigurer() {
        pivotConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
        pivotConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        pivotConfig.Feedback.SensorToMechanismRatio = PivotConstants.kGearRatio;

        pivotConfig.Slot0 = new Slot0Configs()
            .withGravityType(GravityTypeValue.Arm_Cosine)
            .withKP(PivotConstants.PIDGains.kP)
            .withKD(PivotConstants.PIDGains.kD);

        pivotConfig.Voltage = new VoltageConfigs()
            .withPeakForwardVoltage(PivotConstants.kFwdVoltageLimit)
            .withPeakReverseVoltage(PivotConstants.kBkwdVoltageLimit);

        pivotConfig.SoftwareLimitSwitch = new SoftwareLimitSwitchConfigs()
            .withForwardSoftLimitEnable(true)
            .withForwardSoftLimitThreshold(Units.degreesToRotations(PivotConstants.kUpperLimit))
            .withReverseSoftLimitEnable(true)
            .withReverseSoftLimitThreshold(Units.degreesToRotations(PivotConstants.kLowerLimit));

        liftConfig.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
        liftConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

        liftConfig.Feedback.SensorToMechanismRatio = LiftConstants.kGearRatio * LiftConstants.kRotationsToInchesRatio;

        liftConfig.Slot0 = new Slot0Configs()
            .withGravityType(GravityTypeValue.Elevator_Static)
            .withKP(LiftConstants.PIDGains.kP)
            .withKD(LiftConstants.PIDGains.kD)
            .withKG(LiftConstants.FFGains.kG);
        
        liftConfig.Voltage = new VoltageConfigs()
            .withPeakForwardVoltage(LiftConstants.kFwdVoltageLimit)
            .withPeakReverseVoltage(LiftConstants.kBkwdVoltageLimit);

        liftConfig.SoftwareLimitSwitch = new SoftwareLimitSwitchConfigs()
            .withReverseSoftLimitEnable(true)
            .withForwardSoftLimitThreshold(LiftConstants.kLowerLimitDistance);
    }

    public static CTREConfigurer getInstance() {
        return instance;
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