package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
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
import edu.wpi.first.wpilibj2.command.button.Trigger;
import monologue.Logged;
import monologue.Annotations.Log;

import static frc.robot.Constants.IntakeConstants.*;

import java.time.Instant;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;
    ShuffleboardTab testingTab = Shuffleboard.getTab("SEVEN");

    DigitalInput breaker = new DigitalInput(9);
    boolean beamIsWatching = false; 
    //TODO: default to false, since pre-loaded note should not prevent intake from running

    //I found the constructor
    public Intake(){
        intakeMotor = new CANSparkMax(kIntakeMotorID, MotorType.kBrushless);
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Port 9", breaker.get());
        SmartDashboard.putBoolean("Is beam watching?", beamIsWatching);
        //TODO: ensure that "breaker.get() == false == BEAM IS BROKEN"
        if(breaker.get() == false){
            if(beamIsWatching == true){
                intakeMotor.stopMotor();
                beamIsWatching = false;
                //TODO the beam should stop "watching" for a note after it successfully stops a note

                //TODO conditions in which beam needs to be re-engaged:
                //1. a shot is fired (normally) -- CommandContainer.shootFancy/Fancier()
                //2. The amp handoff is completed successfully (at the end of ampHandoffScore())
                //3. when a note is "spat out" completely by "retractIntake/Fancy/Fancier"
                    //3b. when a note is rolled back to a point BEFORE the beam in the intake after an overshoot
                    //TODO: quantify specifically when this happens
            }
        }
    };

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

    //TODO: uh oh, spaghetti c(oh's)de
    public void setBeamWatchingStatus(boolean status){
        beamIsWatching = status;
    }

    //TODO: changed from "setNoteStatus" to "toggleBeamStatus"
    public Command toggleBeamStatus(){
        return(runOnce(()->{
            setBeamWatchingStatus(false);
        }));
    }

    public Command setBeamStatus(boolean status){
        return(runOnce(()->{
            setBeamWatchingStatus(status);
        }));
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
