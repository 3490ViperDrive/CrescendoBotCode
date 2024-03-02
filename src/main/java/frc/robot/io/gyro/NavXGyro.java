package frc.robot.io.gyro;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.io.GyroIO;

public class NavXGyro extends GyroIO {
    Rotation2d m_yawOffset;
    AHRS m_gyro;

    public NavXGyro() {
        m_gyro = new AHRS(SPI.Port.kMXP, (byte) 200);
        zeroGyro(0);
    }

    public Rotation2d getYaw() {
        return m_gyro.getRotation2d().minus(m_yawOffset);
    }

    public void zeroGyro(double offset){
        DataLogManager.log("Gyro zeroed with offset " + offset);
        m_gyro.zeroYaw();
        m_yawOffset = Rotation2d.fromDegrees(offset);
    }

    public Rotation3d getRotation() {
        return m_gyro.getRotation3d();
    }

    public Translation3d getAcceleration() {
        return new Translation3d(
            m_gyro.getWorldLinearAccelX(),
            m_gyro.getWorldLinearAccelY(),
            m_gyro.getWorldLinearAccelZ())
            .times(9.81); //Thanks YAGSL
    }
}
