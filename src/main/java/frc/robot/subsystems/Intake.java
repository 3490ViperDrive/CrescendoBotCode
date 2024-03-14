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
// import frc.robot.subsystems.*;

import static frc.robot.Constants.IntakeConstants.*;

public class Intake extends SubsystemBase implements Logged {

    CANSparkMax intakeMotor;

    @Override
    public void periodic() {};

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

    // Beam Breaker Code

    DigitalInput beambreaker = new DigitalInput(1);

    private Command whenBeamBreaks() {
        return new SequentialCommandGroup(
            // detect the beam is broken,
            // stop the intake for once,
            // get the shooter up to speed,
            // continue the intake and send the note to the shooter
        ); 
    }
        // https://docs.wpilib.org/en/stable/docs/software/commandbased/commands.html#commands
        // https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj2/command/Command.html

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isBeamBroken = false;

    public void BeamBreak(){
        Shuffleboard.getTab("Digital Input").add(beambreaker);
    }

    // Can I do a sequential command group here?

    public void getIntake(){
        if (beambreaker.get()) {
            isBeamBroken = true;
            pauseIntake();
        }
        else{
            isBeamBroken = false;
        }
    }

    public void pauseIntake(){
        intakeMotor.stopMotor();
    }

    public void resumeIntake(){
        intakeMotor.set(1);
    }

    public Command beambreakerState(){
        return new Command(){
            @Override 
            public void execute() {
                SmartDashboard.putBoolean("Beambreaker Reading", beambreaker.get());
            }

            @Override 
            public boolean isFinished() {
                return false;
            }
        };
    }
}
