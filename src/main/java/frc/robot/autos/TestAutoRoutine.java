/*package frc.robot.autos;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.Drivetrain;

public class TestAutoRoutine extends SequentialCommandGroup{

    public TestAutoRoutine(Drivetrain m_drivetrain){
        // Drive forward the specified distance
       new RepeatCommand(new InstantCommand(() -> m_drivetrain.drive(new Translation2d(-0, 0), 0.0, false, false),
        m_drivetrain)).withTimeout(0);
        //new WaitCommand(1.5),
        new InstantCommand(() -> m_drivetrain.drive(new Translation2d(0, 0), 0.0, false, false));
       
        //TODO: Add "add command" functions
    }
    
}*/

