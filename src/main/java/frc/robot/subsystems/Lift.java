// Changed Climb -> Lift
// Lift = Arms

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import static frc.robot.Constants.LiftConstants.*;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

public class Lift extends SubsystemBase {

    CANSparkMax liftMotor;

    public Lift() {
        liftMotor = new CANSparkMax(kLiftMotorID, MotorType.kBrushless);
    }

    @Override
    public void periodic() {};

    public Command lift(){
        return run(() -> {
            liftMotor.set(kLiftSpeed);
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            liftMotor.set(kLiftMotorStopSpeed);
            liftMotor.stopMotor();
        });
    }
}
