package frc.robot.io.pivot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.io.PivotIO;
import frc.robot.utils.CTREConfigurer;
import monologue.Annotations.Log;
import static frc.robot.Constants.PivotConstants.*;

import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

public class PivotFalcon extends PivotIO {
    DutyCycleEncoder m_absEncoder;
    TalonFX m_motor;
    PositionVoltage m_positionVoltageRequest;
    
    public PivotFalcon() {
        m_motor = new TalonFX(kMotorID);
        m_absEncoder = new DutyCycleEncoder(kEncoderChannel);
        m_absEncoder.setDutyCycleRange(0, 1);
        CTREConfigurer.configureMotor(
            CTREConfigurer.getInstance().pivotConfig,
            m_motor,
            "Pivot Motor");
        m_positionVoltageRequest = new PositionVoltage(Units.degreesToRotations(55))
            .withSlot(0)
            .withEnableFOC(false);
        Timer.delay(0.2);
        zeroFalconToAbsEncoder();
    }

    @Override
    public void zeroFalconToAbsEncoder() {
        if (m_absEncoder.isConnected()) {
            m_motor.setPosition(getAngle().getRotations());
        }
    }

    public void moveOpenLoop(double volts) {
        m_motor.setControl(new VoltageOut(MathUtil.applyDeadband(volts, 0.1)));
    }

    public void setAngle(Rotation2d angle) {
        m_motor.setControl(m_positionVoltageRequest.withPosition(angle.getRotations()));
    }

    public Rotation2d getAngle() {
        return Rotation2d.fromDegrees(((m_absEncoder.getAbsolutePosition() * 360) - kEncoderOffset) % 360);
    }

    @Log
    public double getAngleDegrees() {
        return ((m_absEncoder.getAbsolutePosition() * 360) - kEncoderOffset) % 360;
    }

    @Log
    public Rotation2d getFalconAngle() {
        return Rotation2d.fromRotations(m_motor.getPosition().getValueAsDouble());
    }

    @Log
    public double getFalconAngleDegrees() {
        return Units.rotationsToDegrees(m_motor.getPosition().getValueAsDouble());
    }

    @Log
    public double getFalconAngleRotations() {
        return m_motor.getPosition().getValueAsDouble();
    }

    @Log
    public boolean getAbsEncoderConnected() {
        return m_absEncoder.isConnected();
    }

    public boolean atLowerLimit() {
        return getAngle().getDegrees() <= kLowerLimit;
    }

    public boolean atUpperLimit() {
        return getAngle().getDegrees() >= kUpperLimit;
    }

}
