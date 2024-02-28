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

    // This could go in its separate file if this is wrong

    public static class Pivot extends SubsystemBase {

    TalonFX pivotMotor;

    public Pivot(){
        pivotMotor = new TalonFX(kPivotMotorID);
    }

    @Override
    public void periodic() {};

    public Command pivot() {
        return run(() -> {
            pivotMotor.set(kPivotSpeed);
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            pivotMotor.set(kPivotMotorStopSpeed);
            pivotMotor.stopMotor();
        });
    }
}

// I was just wondering if I could put this here; this could go in its separate file if this is wrong

public static class ShooterExtension extends SubsystemBase {

    TalonFX shooterExtensionMotor;

    public ShooterExtension() {
        shooterExtensionMotor = new TalonFX(kExtensionMotorID);
    }

    @Override
    public void periodic() {};

    public Command extend() {
        return run(() -> {
            shooterExtensionMotor.set(kExtensionMotorSpeed);
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            shooterExtensionMotor.set(kExtensionMotorStop);
            shooterExtensionMotor.stopMotor();
        });
    }
}
}
