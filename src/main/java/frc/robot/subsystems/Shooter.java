package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.hardware.TalonFX;

import frc.robot.HardwareIds;

public class Shooter extends SubsystemBase {

    TalonFX shooterMotor;
    
    public Shooter() {
        shooterMotor = new TalonFX(HardwareIds.Canbus.kShooterID);

        var slot0Configs = new Slot0Configs();
        slot0Configs.kS = 0.05; // Add 0.05 V output to overcome static friction
        // slot0Configs.kV = 0.12; // A velocity target of 1 rps results in 0.12 V output
        slot0Configs.kP = 0.11; // An error of 1 rps results in 0.11 V output
        // slot0Configs.kI = 0; // no output for integrated error
        // slot0Configs.kD = 0; // no output for error derivative
      
        shooterMotor.getConfigurator().apply(slot0Configs);
      
        // // create a velocity closed-loop request, voltage output, slot 0 configs
        // final VelocityVoltage m_request = new VelocityVoltage(0).withSlot(0);
    }

    @Override
    public void periodic() {};


    public Command shoot(double speed, Intake aIntake) {
        return shoot(speed);
    }

    public Command shoot(double speed){
        return new StartEndCommand(() -> shooterMotor.set(-speed), () -> shooterMotor.set(0), this);
    }

    public Command Autoshoot(double speed){
        return new InstantCommand(() -> shooterMotor.set(-speed));
    } 
}

