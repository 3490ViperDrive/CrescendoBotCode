package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import com.ctre.phoenix6.hardware.TalonFX;

import static frc.robot.Constants.ShooterConstants.*;

public class Shooter extends SubsystemBase {

    TalonFX shooterMotor;
    
    public Shooter(){
        shooterMotor = new TalonFX(kShooterMotorID);
    }

    @Override
    public void periodic() {};

    public Command shoot() {
        return run(() -> {
            shooterMotor.set(kShooterSpeed);
        });
    }
    public Command stopMotorCommand() {
        return runOnce(() -> {
            shooterMotor.set(kShooterMotorStopSpeed);
            shooterMotor.stopMotor();
        });
    }
}