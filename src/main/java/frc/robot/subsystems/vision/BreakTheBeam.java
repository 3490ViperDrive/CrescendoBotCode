package frc.robot.subsystems.vision;

import static frc.robot.Constants.DigitalInputConstants.*;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class BreakTheBeam extends SubsystemBase {

    DigitalInput beambreaker = new DigitalInput(kDigitalInputPort); ;

    public BreakTheBeam(){
        Shuffleboard.getTab("Digital Input").add(beambreaker);
    }
    
        public Command DIValue() {
            return(this.runOnce(
                () -> {
                    SmartDashboard.putBoolean("Beambreaker Reading", beambreaker.get());
                } ));
        }

    //TODO:         
    // Goal: Code that detects the note is in and stops the motor UNTIL the shooter achieves it's desired speed for shooting   
    // 1. Run the intake
    // 2. Once the note is in, make sure that the sensor detecs the beam is broken by the note
    // 3. Once the beam is broken, stop the motor for an amount of time (I guess)
    // 4. Run the shooter and somehow (I guess a timer?)  discontinue the 'stopMotorCommand()' for the intake

    // public stopIntake() {
    //    if(beam breaks){
    //    stopMotor for 2 seconds
    // }
    // continue the normal behavior
    //}
}
