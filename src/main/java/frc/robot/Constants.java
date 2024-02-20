package frc.robot;

import edu.wpi.first.math.util.Units;
import frc.robot.generated.TunerConstants;

public final class Constants {
    public static final class ControllerConstants {
        public static final int kControllerID = 0;
        public static final double kControllerDeadband = 0.15;
    }

    public static final class DrivetrainConstants {
        // Most drivetrain-specific constants have been moved to TunerConstants.java, which is
        // maintained by Phoenix Tuner. If any drivetrain constants need to be modified, or new constants added, do so here;
        // TunerConstants.java may be overwritten in the future

        public static final double kMaxTranslationSpeed = 4.5; //m/s
        public static final double kMaxRotationSpeed = 10; //rad/s
        public static final double kMaxModuleSpeed = 4.5; //m/s

        public static final double kDrivebaseRadius = Math.hypot(
            Units.inchesToMeters(TunerConstants.kFrontLeftXPosInches),
            Units.inchesToMeters(TunerConstants.kFrontLeftYPosInches));
    }
    
}
