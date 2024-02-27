package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import com.ctre.phoenix6.hardware.TalonFX;

import static frc.robot.Constants.ClimbConstants.*;

public class Climb extends SubsystemBase {

    TalonFX climbMotor0;
    TalonFX climbMotor1;

    public Climb() {
        climbMotor0 = new TalonFX(kClimbMotorID0);
        climbMotor1 = new TalonFX(kClimbMotorID1);
    }

    @Override
    public void periodic() {};

    public Command climb(){
        return run(() -> {
            climbMotor0.set(kClimbSpeed);
            climbMotor1.set(kClimbSpeed);
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            climbMotor0.set(kClimbMotorStopSpeed);
            climbMotor0.stopMotor();

            climbMotor1.set(kClimbMotorStopSpeed);
            climbMotor1.stopMotor();
        });
    }
}
