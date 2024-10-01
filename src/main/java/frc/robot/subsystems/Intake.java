package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;

import monologue.Logged;
import monologue.Annotations.Log;

import static frc.robot.Constants.IntakeConstants.*;
import frc.robot.HardwareIds;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;

    ShuffleboardTab testingTab = Shuffleboard.getTab("SEVEN");

    DigitalInput breaker = new DigitalInput(HardwareIds.Dio.kBeamBreak);
    DigitalOutput leds = new DigitalOutput(HardwareIds.Dio.kLeds);
    boolean noteStatus = false; // default to false

    public Intake() {
        intakeMotor = new CANSparkMax(HardwareIds.Canbus.kIntakeID, MotorType.kBrushless);
    }

    public Command setIntakeMotor() {
        return runOnce(() -> {
        intakeMotor.set(1);
        }).withInterruptBehavior(InterruptionBehavior.kCancelSelf).andThen(setNoteStat(true));
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Port 9", breaker.get());
        SmartDashboard.putBoolean("noteStatus", noteStatus);
        leds.set(breaker.get());
    };

    public Command takeIn(double speed) {
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




    public void setNoteStatus(boolean status){
        noteStatus = status;
    }


    public Command setNoteStat(boolean tf){
        return(runOnce(()->{
            setNoteStatus(tf);
        }));
    }

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

    @Log
    public boolean getBeamBreak() {
        return breaker.get();
    }

}
