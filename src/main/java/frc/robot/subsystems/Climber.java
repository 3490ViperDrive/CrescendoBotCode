// Changed Climb -> Lift
// Lift = Arms

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import static frc.robot.Constants.ClimberConstants.*;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

public class Climber extends SubsystemBase {

    CANSparkMax liftMotor;

    public Climber() {
        liftMotor = new CANSparkMax(kLiftMotorID, MotorType.kBrushless);
    }

    @Override
    public void periodic() {};

    public Command climb(double speed){
        // return run(() -> {
        //     liftMotor.set(kLiftSpeed);
        // });
        return new StartEndCommand(() -> liftMotor.set(kLiftSpeed), () -> liftMotor.stopMotor(), this);
    }

    // public Command stopMotorCommand() {
    //     return runOnce(() -> {
    //         liftMotor.set(kLiftMotorStopSpeed);
    //         liftMotor.stopMotor();
    //     });
    // }
}
