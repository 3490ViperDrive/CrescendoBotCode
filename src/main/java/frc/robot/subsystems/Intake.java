package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;

public class Intake extends SubsystemBase{

    private CANSparkMax intake1Motor;



    public Intake(){

        int intake1MotorID = 15;
        intake1Motor = new CANSparkMax(intake1MotorID, MotorType.kBrushless);

        //do we need to invert motors?
        //intake1Motor.setInverted(true);
        //intake2Motor.setInverted(true);

        intake1Motor.burnFlash();

        
        // Run the motors
       // Stop the mechanism once the note is in

        // Limit switch or beam breaker to check if the note is in so the intake can communicate with the shooter?

        // Use pid to run the motors on different speeds?

    }

    public void startIntake(){
        intake1Motor.set(IntakeConstants.intakeMotorSpeed);
    }

    public void reverseIntake(){
        intake1Motor.set(-IntakeConstants.intakeMotorSpeed);
    }

    public void stopIntake(){
        intake1Motor.set(0);
    }
}
