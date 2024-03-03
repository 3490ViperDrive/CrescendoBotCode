package frc.robot.io.lift;

import frc.robot.io.LiftIO;
import frc.robot.utils.CTREConfigurer;

import static frc.robot.Constants.LiftConstants.*;

import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.math.MathUtil;

public class LiftFalcon extends LiftIO {
    TalonFX m_motor;

    LiftFalcon() {
        m_motor = new TalonFX(kMotorID);
        CTREConfigurer.configureMotor(
            CTREConfigurer.getInstance().liftConfig,
            m_motor, 
            "Lift Motor");
        //Assume the position on startup is at the init position
        //This is very, very bad, but until there is a limit switch,
        //there is no better method outside of current initialization
        m_motor.setPosition(0);
    }
    
    public void setDistance(double distance) {
        //add when numbers look good
    }
    
    public void moveOpenLoop(double volts) {
        m_motor.setControl(new VoltageOut(MathUtil.applyDeadband(volts, 0.1)));
    }

    public double getDistance() {
        return m_motor.getPosition().getValueAsDouble();
    }

    public boolean atLowerLimit() {
        return getDistance() <= kLowerLimitDistance;
    }

    public boolean atUpperLimit() {
        return getDistance() >= kUpperLimitDistance;
    }

}
