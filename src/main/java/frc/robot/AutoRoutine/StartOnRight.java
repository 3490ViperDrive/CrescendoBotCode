package frc.robot.AutoRoutine;

// import edu.wpi.first.math.geometry.Translation2d;
// import edu.wpi.first.wpilibj2.command.Commands;
// import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
//import frc.robot.io.SwerveModuleIO;
import frc.robot.subsystems.Drivetrain;

// import java.util.function.DoubleSupplier;

public class StartOnRight extends SequentialCommandGroup {
    
    //basic auto commented out. Will slip back in when needed
    public StartOnRight(Drivetrain Drive){}

        

    //     //TODO come back and add a "driveAuto" command
    //     addCommands(
    //         Commands.runOnce(
    //             () -> {Drive.drive(new Translation2d(1,0),0,false, SwerveModuleIO.ControlMode.kOpenLoop)).withTimeout(2));
    // }*/

}
