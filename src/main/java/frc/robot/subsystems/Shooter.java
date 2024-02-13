// IDK WHAT I JUST COOOKED BUT I DID COOKED SOMETHING
// IDK WHAT THIS CODE DOES 
// THIS ENTIRE CODE MIGHT BE WRONG AND IF IT IS, I WILL FIX IT! 

package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.ctre.phoenix6.hardware.TalonFX;

public class Shooter extends SubsystemBase {

    int shooterMotorID = 0;

    TalonFX shooterMotor; 
    
    public Shooter(){
        shooterMotor = new TalonFX(shooterMotorID);
        Preferences.initDouble("shooter motor speed [-1, 1]", 0);
    }

    @Override
    public void periodic() {};

    public Command shoot() {
        return run(() -> {
            shooterMotor.set(MathUtil.clamp(Preferences.getDouble("shooter motor speed [-1, 1]", 0), 0, 0));
        });
    }

    public Command stopMotorCommand() {
        return runOnce(() -> {
            shooterMotor.stopMotor();
            shooterMotor.set(0);
        });
    }
}
