package frc.robot.subsystems;

// import com.revrobotics.CANSparkLowLevel.MotorType;
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
import monologue.Logged;
import monologue.Annotations.Log;

import static frc.robot.Constants.IntakeConstants.*;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;

    DigitalInput breambreaker = new DigitalInput(0);

    public boolean noteStatus = true;

    // Bream Breaker Code
    
    public void checkBream(){
        Shuffleboard.getTab("Digital Input").add(breambreaker);

        if(breambreaker.get() && !noteStatus){
            intakeMotor.stopMotor();
            noteStatus = true;
            }
    }

     public void setNoteStatus(boolean status){
        noteStatus = status;
    }
    
    @Override
    public void periodic() {
       SmartDashboard.putBoolean("Breambreaker Reading", breambreaker.get()); 
    }
             

    // Adam suggested me to make a command for breambreaker and add it into 'takeIneFancy' so that it allows the intake to feed the note 
    // into shooter by stopping the intake motor only once

    // According to him, the code in periodic will stop the intake motor for the rest of the match

    public Command takeIn(double speed) {
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
