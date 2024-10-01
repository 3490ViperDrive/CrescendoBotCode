package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;

import frc.robot.HardwareIds;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

public class Climber extends SubsystemBase {

    CANSparkMax liftMotor;

    public Climber() {
        liftMotor = new CANSparkMax(HardwareIds.Canbus.kClimberID, MotorType.kBrushless);
    }

    @Override
    public void periodic() {};

    public Command climb(double speed){
        return new StartEndCommand(() -> liftMotor.set(speed), () -> liftMotor.stopMotor(), this);
    }
}
