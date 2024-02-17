package frc.robot.AutoRoutine;



import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.*;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.io.SwerveModuleIO;
import frc.robot.subsystems.Drivetrain;

import java.util.function.DoubleSupplier;

public class StartOnRight extends SequentialCommandGroup {
    
    //basic auto commented out. Will slip back in when needed

    public StartOnRight( Drivetrain drive, shooterSubsystem Shooter, intakeSubsystem intake){

        //find a way to give doubble suppliers a value, then pipe those in

        /*addCommands(
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                ()-> drive.driveOpenLoopCommand(() -> 2.0,() -> 0.0, 0.0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
                //April tag here to check for speaker
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(() -> 0.0,() -> 0.0,90)
            ).withTimeout(1),
                //Check for tag to make sure robot is turned properly
            Commands.runOnce(
                () -> drive.OpenLoopCommand(() -> 2.0,() -> 0.0,0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(() -> 0.0,() -> 0.0,-90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(() -> 0.0,() -> 0.0,90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.OpenLoopCommand(() -> 2.0,() -> 0.0,0)
            ).withTimeout(1),
            Commands.runOnce(
                () -> intake.intakeNote(1)
            ).withTimeout(1),
            Commands.runOnce(
                () -> drive.openLoopCommand(() -> 0.0,() -> 0.0,-90)
            ).withTimeout(1),
            Commands.runOnce(
                () -> Shooter.ShootymcShootface
            ).withTimeout(1),
        );*/
       

 // good code :]
       
    }
}
