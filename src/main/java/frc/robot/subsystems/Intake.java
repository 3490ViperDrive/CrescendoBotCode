package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import monologue.Logged;
import monologue.Annotations.Log;

import static frc.robot.Constants.IntakeConstants.*;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;

    public Intake(){
        intakeMotor = new CANSparkMax(kIntakeMotorID, MotorType.kBrushless);
    }

    @Override
    public void periodic() {};

    public Command takeIn() {
        return run(() -> {
            intakeMotor.set(kIntakeSpeed);
        });
    }

    public Command takeOut() {
        return run(() -> {
            intakeMotor.set(-kIntakeSpeed);
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            intakeMotor.set(kIntakeMotorStopSpeed);
            intakeMotor.stopMotor();
        });
    } 

    @Log
    public double getVelocity() {
        return intakeMotor.getEncoder().getVelocity();
    }
}
