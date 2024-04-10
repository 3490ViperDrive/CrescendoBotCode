//TODO: Finish y'know, getting all the ui stuff converted and placed here

package frc.robot.utils;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class UImanager {
    public static final String XboxController = "XboxController";
    public static final String JoyStick = "JoyStick";
 // public static final String N64 = "N64Controller"
    public static final SendableChooser<String> mControllerChoice = new SendableChooser<>(); // Tried getting rid of "M_" for the new CamelCase tho tbh idk what a CamelCase is.


    public void dashboardControllerChoice(){
        mControllerChoice.setDefaultOption("JoyStick", JoyStick);
        mControllerChoice.addOption("XboxController", XboxController);
     // mControllerChoice.addOptoString("N64Controller", thingy)
        SmartDashboard.putData("Controller Choice", mControllerChoice);
    }
}
