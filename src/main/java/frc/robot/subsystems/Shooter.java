package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import com.ctre.phoenix6.hardware.TalonFX;

import static frc.robot.Constants.ShooterSpeed.*;

public class Shooter extends SubsystemBase {

    int shooterMotorID = 0;

    TalonFX shooterMotor; 
    
    public Shooter(){
        shooterMotor = new TalonFX(shooterMotorID);
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
            shooterMotor.set(0);
            shooterMotor.stopMotor();
        });
    }
}
