package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix6.hardware.TalonFX;

public class Shooter extends SubsystemBase {
    
    public Command shoot(){
        // The command that runs motors and shoots the note.

        // Detect if the note is in or not (beam breaker)

        TalonFX shooterTalon = new TalonFX(0);

        System.out.print("Return something");

        return null;
    }

}
