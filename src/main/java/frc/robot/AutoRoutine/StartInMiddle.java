package frc.robot.AutoRoutine;


import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.*;


public class StartInMiddle extends SequentialCommandGroup {
    
    public StartInMiddle(Drivetrain drive, Shooter shooter, intakeSubsystem intake){
        /*addCommands(
            Commands.runOnce(
                () -> shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                ()-> drive.driveOpenLoopCommand(() -> 2,() -> 0,() -> 0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
                //Shoot
                //Use tag for "aim assit"
            Commands.runOnce(
                () -> shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.driveOpenLoopCommand(() -> -1.5,() -> 0,() -> 0)
            ).withTimeout(1),
                //Use tag to course correct and make sure you are aligned
            Commands.runOnce(
                () -> drive.driveOpenLoopCommand(() ->0,() -> 2.0,() -> 0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.driveOpenLoopCommand(() -> 1.5, () -> 0, () -> 0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
                //Aim assist
            Commands.runOnce(
                () -> shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.driveOpenLoopCommand(() -> -1.5,() -> 0,() -> 0)
            ).withTimeout(1),
                //Course correct for alignment
            Commands.runOnce(
                () -> drive.driveOpenLoopCommand(() -> 0,() -> -4.0,() -> 0)
            ).withTimeout(1),
            Commands.runOnce(  
                () -> drive.driveOpenLoopCommand(() -> 1.5, () -> 0,() ->  0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
                //Aim assist
            Commands.runOnce(
                () -> shooter.ShootymcShootface(1)
            ).withTimeout(1)
        );*/

    }

}
