package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import monologue.Logged;
import monologue.Annotations.Log;

import static frc.robot.Constants.IntakeConstants.*;

import java.time.Instant;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;
    ShuffleboardTab testingTab = Shuffleboard.getTab("SEVEN");

    public Intake(){
        intakeMotor = new CANSparkMax(kIntakeMotorID, MotorType.kBrushless);
    }

    @Override
    public void periodic() {};

    public Command takeIn(double speed) {
        // return run(() -> {
        //     intakeMotor.set(kIntakeSpeed);
        // });
        return new StartEndCommand(() -> intakeMotor.set(speed), () -> intakeMotor.stopMotor(), this);
    }

    public Command takeInFancy() {
        return new ParallelRaceGroup(takeIn(1), takeInFancyDeadline());
        
    }

    private Command takeInFancyDeadline() {
        return new SequentialCommandGroup(
            new InstantCommand(()->{
                SmartDashboard.putString("SEVENTH", "intaking lul");
            }),
            new WaitCommand(kCurrentSpikeTime),
            new WaitUntilCommand(() -> getCurrentAboveThreshold()),
            new WaitCommand(kPullInTime)
        );
    }

    // public Command stopMotorCommand() {
    //     return runOnce(() -> {
    //         intakeMotor.set(kIntakeMotorStopSpeed);
    //         intakeMotor.stopMotor();
    //     });
    // } 

    @Log
    public double getVelocity() {
        return intakeMotor.getEncoder().getVelocity();
    }

    @Log
    public double getCurrent() {
        return intakeMotor.getOutputCurrent();
    }

    @Log
    public boolean getCurrentAboveThreshold() {
        return intakeMotor.getOutputCurrent() > kCurrentThreshold;
    }
}
