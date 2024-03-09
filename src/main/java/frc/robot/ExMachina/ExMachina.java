package frc.robot.ExMachina;

import java.util.HashMap;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class ExMachina extends CommandGenericHID{

    HashMap<String, Trigger> buttonLayout = new HashMap<String, Trigger>();
    
    public ExMachina(int portNumber){
        super(portNumber);
    }
}
