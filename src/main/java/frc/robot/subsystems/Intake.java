package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import monologue.Logged;
import monologue.Annotations.Log;
// simport frc.robot.subsystems.*;

import static frc.robot.Constants.IntakeConstants.*;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;

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

    DigitalInput beambreaker = new DigitalInput(1);

    //Trigger beamIsBroken = beambreaker.get();

    public void  BreakTheBeam(){
        Shuffleboard.getTab("Digital Input").add(beambreaker);
    }
    
        public Command DIValue() {
            return(this.runOnce(
                () -> {
                    SmartDashboard.putBoolean("Beambreaker Reading", beambreaker.get());
                } ));
    }

    //TODO:         
    // Goal: Code that detects the note is in and stops the motor UNTIL the shooter achieves it's desired speed for shooting   
    // 1. Run the intake
    // 2. Once the note is in, make sure that the sensor detecs the beam is broken by the note
    // 3. Once the beam is broken, stop the motor for an amount of time (I guess)
    // 4. Run the shooter and somehow (I guess a timer?)  discontinue the 'stopMotorCommand()' for the intake

    // public stopIntake() {
    //    if(beam breaks){
    //    stopMotor for 2 seconds
    // }
    // continue the normal behavior
    //} 
    }
