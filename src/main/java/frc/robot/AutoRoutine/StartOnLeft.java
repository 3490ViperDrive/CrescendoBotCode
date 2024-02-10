package frc.robot.AutoRoutine;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Subsystems.Drivetrain;

public class StartOnLeft extends SequentialCommandGroup {

    public StartOnLeft(Drivetrain drive, shooterSubsystem shooter, intakeSubsystem intake){

        /*addCommands(
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                ()-> Drive.driveOpenLoopCommand(2,0,0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(0,0,-90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(2,0,0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(0,0,90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(0,0,-90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(2,0,0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.openLoopCommand(0,0,90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> Shooter.ShootymcShootface
            ).withTimeout(1)
        );*/

    }
    
}
