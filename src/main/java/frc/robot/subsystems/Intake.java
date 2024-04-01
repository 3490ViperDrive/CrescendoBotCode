package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
// import edu.wpi.first.wpilibj2.command.button.Trigger;
import monologue.Logged;
import monologue.Annotations.Log;

import static frc.robot.Constants.IntakeConstants.*;

// import java.time.Instant;
import java.util.function.BooleanSupplier;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;

    ShuffleboardTab testingTab = Shuffleboard.getTab("SEVEN");

    public DigitalInput breaker = new DigitalInput(9);
    //DigitalInput breaker2 = new DigitalInput(8);
    // boolean noteStatus = false; // default to false

    //I found the constructor
    public Intake(){
        intakeMotor = new CANSparkMax(kIntakeMotorID, MotorType.kBrushless);
    }

    // public Command runIntakeNew(){
    //     return new SequentialCommandGroup(
    //         new WaitUntilCommand(() -> getBeambreaker())
    //         // Press the button (Toggle)
    //         // Run the Intake Until The beam is broken
    //         // Stop the motor for once when the beam is broken 
    //         // Get shooter ready and up to speed
    //         // Make sure of the behavior it is supposed to do while retracting 
    //         // Make the same trigger bind to retract intake
    //     );
    // };

    // I don't think we are using "setIntakeMotor"

    public Command setIntakeMotor(){
        return runOnce(() -> {
        intakeMotor.set(1);
        }).withInterruptBehavior(InterruptionBehavior.kCancelSelf).andThen();
    }

    @Override
    public void periodic() {
        SmartDashboard.putBoolean("Port 9", breaker.get());
       //SmartDashboard.putBoolean("Port 8", breaker2.get());
        // if(breaker.get() && !noteStatus){
        //     intakeMotor.stopMotor();
        //     noteStatus = true;
        // }
        // if(breaker.get() == false){
        //     if(noteStatus == false){
        //         intakeMotor.stopMotor();
        //         noteStatus = true;
        //     }
        // }
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

    // public void setNoteStatus(boolean status){
    //     noteStatus = status;
    // }

    // public Command toggleNoteStatus(){
    //     return(runOnce(()->{
    //         setNoteStatus(false);
    //     }));
    // }

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

    @Log
    public boolean getBeambreaker() {
        return breaker.get();
    }

    @Log
    public BooleanSupplier gadzooks(){
        return () -> !breaker.get();
        //return breaker.get();
    }
}
