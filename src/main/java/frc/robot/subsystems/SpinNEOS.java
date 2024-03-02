package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SpinNEOS extends SubsystemBase {

    int motor1ID = 15;
    int motor2ID = 16;

    CANSparkMax motor1;
    CANSparkMax motor2;

    public SpinNEOS() {
        motor1 = new CANSparkMax(motor1ID, MotorType.kBrushless);
        motor2 = new CANSparkMax(motor2ID, MotorType.kBrushless);
        SmartDashboard.putString("Enable motors by holding A", "Configure motor speed in Preferences tab");
        Preferences.initDouble("motor 1 speed [-1, 1]", 0);
        Preferences.initDouble("motor 2 speed [-1, 1]", 0);
    }  

    @Override
    public void periodic() {};

    public Command spinnyCommand() {
        return run(() -> {
            motor1.set(MathUtil.clamp(Preferences.getDouble("motor 1 speed [-1, 1]", 0), -1, 1));
            motor2.set(MathUtil.clamp(Preferences.getDouble("motor 2 speed [-1, 1]", 0), -1, 1));
        });
    }

    public Command stopMotorCommand() {
        return run(() -> {
            motor1.stopMotor();
            motor1.set(0);
            motor2.stopMotor();
            motor2.set(0);
        });
    }
}
