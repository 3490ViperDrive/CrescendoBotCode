package frc.robot.io.pivot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import frc.robot.io.PivotIO;
import frc.robot.utils.CTREConfigurer;
import monologue.Annotations.Log;
import static frc.robot.Constants.PivotConstants.*;

import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

public class PivotFalcon extends PivotIO {
    DutyCycleEncoder m_absEncoder;
    TalonFX m_motor;
    
    PivotFalcon() {
        m_motor = new TalonFX(kMotorID);
        m_absEncoder = new DutyCycleEncoder(kEncoderChannel);
        m_absEncoder.setDutyCycleRange(0, 1);
        m_motor.setPosition(getAngle().getRotations());
        CTREConfigurer.configureMotor(
            CTREConfigurer.getInstance().pivotConfig,
            m_motor,
            "Pivot Motor");
    }

    public void moveOpenLoop(double volts) {
        m_motor.setControl(new VoltageOut(MathUtil.applyDeadband(volts, 0.1)));
    }

    public void setAngle(Rotation2d angle) {
        //TODO add once numbers look good
    }

    public Rotation2d getAngle() {
        return Rotation2d.fromDegrees(((m_absEncoder.getAbsolutePosition() * 360) - kEncoderOffset) % 360);
    }

    @Log
    public Rotation2d getFalconAngle() {
        return Rotation2d.fromRotations(m_motor.getPosition().getValueAsDouble());
    }

    public boolean atLowerLimit() {
        return getAngle().getDegrees() <= kLowerLimit;
    }

    public boolean atUpperLimit() {
        return getAngle().getDegrees() >= kUpperLimit;
    }

}
