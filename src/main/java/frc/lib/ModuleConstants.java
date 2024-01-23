package frc.lib;

import edu.wpi.first.math.geometry.Rotation2d;

//Container class for module constants
public final class ModuleConstants {
    public final int driveMotorID;
    public final int azimuthMotorID;
    public final int absEncoderID;
    public final Rotation2d absEncoderOffset;
    public final String moduleName;

    public ModuleConstants(int driveMotorID, int azimuthMotorID, int absEncoderID, Rotation2d absEncoderOffset, String moduleName) {
        this.driveMotorID = driveMotorID;
        this.azimuthMotorID = azimuthMotorID;
        this.absEncoderID = absEncoderID;
        this.absEncoderOffset = absEncoderOffset;
        this.moduleName = moduleName;
    }
}
