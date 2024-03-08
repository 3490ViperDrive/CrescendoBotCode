package frc.robot.subsystems.vision;

import static frc.robot.Constants.DigitalInputConstants.kDigitalInputPort;

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
    
}
