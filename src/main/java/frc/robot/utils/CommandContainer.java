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
    }

    public Command shootFancy(double speed) {
        return shooter.shoot(speed)
            .alongWith(new SequentialCommandGroup(
                new InstantCommand(()->{
                    SmartDashboard.putString("ocho", "shoot go boom");
                }),
                new WaitCommand(0.5), //tune this
                intake.takeIn(1)
            ));
    }

    public Command ampHandoffScore() { //todo tune all of this
        return new SequentialCommandGroup(
            intake.takeIn(0.75).withTimeout(0.5).raceWith(
                shooter.shoot(0.05)
            ),
            lift.requestPosition(17.95).raceWith(
                new SequentialCommandGroup(
                new WaitCommand(0.75),
                pivot.requestPosition(-33).raceWith(
                    new SequentialCommandGroup(
                        new WaitCommand(0.5),
                        shooter.shoot(0.3).withTimeout(0.5))
                ))
            )
        );
    }

    public Command retractIntakeFancy() {
        return new ParallelCommandGroup(
            intake.takeIn(-0.75),
            shooter.shoot(-0.05)
        );
    }

    public Command raisePivotLiftForClimb() {
        return new ParallelCommandGroup(
            lift.requestPosition(17.75),
            pivot.requestPosition(-30)
        );
    }
}
