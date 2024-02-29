package frc.robot.subsystems.vision;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.VisionConstants.*;
import static frc.robot.Constants.DigitalInputConstants.*;
// import static frc.robot.Constants.IntakeConstants.*;

public class Optometrist extends SubsystemBase{

    DigitalInput input = new DigitalInput(kDigitalInputPort); 

    public void DigitalInput(){
        Shuffleboard.getTab("Digital Input").add(input);
    }
    
        public Command DIValue() {
            return(run(
                () -> {
                    SmartDashboard.getBoolean("Digital Input", input.get());
                }
            ));
        }
    
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

        // DigitalInput limit = new DigitalInput(kDigitalInputLimit);

        // Spark spark = new Spark(kSparkDIO);

        // @Override
        // public void periodic(){
        //     if(!limit.get()) {
        //         spark.set(kIntakeSpeed);
        //     } else {
        //         spark.set(kIntakeMotorStopSpeed);
        //     }
        // };
}
