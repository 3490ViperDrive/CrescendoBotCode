package frc.robot.subsystems.vision;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.VisionConstants.*;

public class Optometrist extends SubsystemBase{
    
    AnalogPotentiometer lidar = new AnalogPotentiometer(kLidarPort);

    public Optometrist(){
        Shuffleboard.getTab("Sensors").add(lidar);
    }

    public Command peek(){
        return(this.runOnce(
            ()->{
                SmartDashboard.putNumber("Lidar Reading", lidar.get());
            }
        ));
    }
    
}
