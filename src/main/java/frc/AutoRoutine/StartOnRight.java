package frc.AutoRoutine;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class StartOnRight extends SequentialCommandGroup {
    
    //basic auto commented out. Will slip back in when needed
    /*public StartOnRight( driveSubsystem Drive, shooterSubsystem Shooter){

        addCommands(
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1),
            Commands.runOnce(
                ()-> Drive.swervedrive(1,0,0)
            ).withTimeout(2)
        );
        
        //just drive auto
        addCommands(
            Commands.runOnce(
                () -> Drive.swervedrive(1,0,0)
            ).withTimeout(2)
        
        );

        //just shoot auto
        addCommands(
            Commands.runOnce(
                () -> Shooter.ShootymcShootface(1)
            ).withTimeout(1)
        
        );


       
    }*/
}
