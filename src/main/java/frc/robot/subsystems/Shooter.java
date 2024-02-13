package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.*;

import com.ctre.phoenix6.hardware.TalonFX;

public class Shooter extends SubsystemBase {

    private static final double kShooterSpeed = 0;

    int shooterMotorID = 0;

    TalonFX shooterMotor; 
    
    public Shooter(){
        shooterMotor = new TalonFX(shooterMotorID);
        Preferences.initDouble("shooter motor speed [-1, 1]", 0);
    }

    @Override
    public void periodic() {};

    // I did nothing wrong ; Everything's correct from my side
    // If something is goes wrong, it's because of the government

    public Command shoot() {
        return run(() -> {
            shooterMotor.set(0);
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            shooterMotor.set(0);
            shooterMotor.stopMotor();
        });
    }
}
