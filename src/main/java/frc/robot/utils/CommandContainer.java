package frc.robot.utils;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.*;

//According to the docs this is a 'non-static command factory'
public class CommandContainer {
    Intake intake;
    Pivot pivot;
    Shooter shooter;
    Climber climber;
    
    public CommandContainer(Intake intake, Pivot pivot, Shooter shooter, Climber climber) {
        this.intake = intake;
        this.pivot = pivot;
        this.shooter = shooter;
        this.climber = climber;
    }

    public Command shootFancy(double speed) {
        return shooter.shoot(speed)
            .alongWith(new SequentialCommandGroup(
                new WaitCommand(0.5), //tune this
                intake.takeIn(1)
            ));
    }
}
