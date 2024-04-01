package frc.robot.utils;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.*;

//According to the docs this is a 'non-static command factory'
public class CommandContainer {
    Intake intake;
    Pivot pivot;
    Shooter shooter;
    Climber climber;
    TrapLift lift;
    
    public CommandContainer(Intake intake, Pivot pivot, Shooter shooter, Climber climber, TrapLift lift) {
        this.intake = intake;
        this.pivot = pivot;
        this.shooter = shooter;
        this.climber = climber;
        this.lift = lift;

        NamedCommands.registerCommand("PPIntake", intake.takeInFancy().withTimeout(3));
        NamedCommands.registerCommand("PPShoot", shootFancy(0.5).withTimeout(1));
        //NamedCommands.registerCommand("PPShoot", shootFancier(0.5, 0.5));
        NamedCommands.registerCommand("PPSpinShoot", shooter.Autoshoot(0.5));
        //TODO: Change made - adjusted command that gets called during Auto to have original delay
    }

    public Command shootFancy(double speed) {
        return shooter.shoot(speed)
            .alongWith(new SequentialCommandGroup(
                new InstantCommand(()->{
                    SmartDashboard.putString("ocho", "shoot go boom");
                }),
                new WaitCommand(0.5), //(went from .5 to .75 )
                //TODO: nvm, reverted it back because separation wasn't working
                intake.takeIn(1)
            ));
    }

    //TODO: wet code will get converted to main after tournament
    public Command shootFancier(double speed){
        return shooter.shoot(speed)
            .alongWith(new SequentialCommandGroup(
                new InstantCommand(()->{
                    SmartDashboard.putString("ocho", "shoot go boom");
                }),
                new WaitCommand(0.75),
                intake.takeIn(1)
            ));
    }

    public Command wetShoot(double speed, double angle){
        return pivot.requestPosition(angle).alongWith(shootFancy(speed));
    }

    public Command ampHandoffScore() { 
        return new SequentialCommandGroup(
            intake.takeIn(0.75).withTimeout(0.5).raceWith(
                shooter.shoot(0.05)
            ),
            lift.requestPosition(19.5).raceWith(
                new SequentialCommandGroup(
                intake.toggleNoteStatus(),
                new WaitCommand(0.75),
                pivot.requestPosition(-31).raceWith(
                    new SequentialCommandGroup(
                        new WaitCommand(0.5),
                        shooter.shoot(0.45).withTimeout(0.5))
                ))
            ),
            lift.requestPosition(0).withTimeout(0.125)
        );
    }

    public Command retractIntakeFancy() {
        return new ParallelCommandGroup(
            intake.takeIn(-0.75),
            shooter.shoot(-0.05)
        );
    }

    public Command retractIntakeFancier(){
        //TODO runs the regular "retractIntakeFancy()" command and then toggles the note status
        return new ParallelCommandGroup(
            intake.takeIn(-0.75),
            shooter.shoot(-0.05)
        ).andThen(intake.toggleNoteStatus());
    }

    public Command raisePivotLiftForClimb() {
        return new ParallelCommandGroup(
            lift.requestPosition(17.75),
            pivot.requestPosition(-30)
        );
    }
}
