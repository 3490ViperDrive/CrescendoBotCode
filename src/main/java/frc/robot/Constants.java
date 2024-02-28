package frc.robot;

import edu.wpi.first.math.util.Units;
import frc.robot.generated.TunerConstants;

public final class Constants {

    public static final class ShooterConstants{
        public static final double kShooterSpeed = 0.5;
        public static final int kShooterMotorID = 1;
    }
    public static final class IntakeConstants{
        public static final double kIntakeSpeed = 0.75;
         public static final int kIntakeMotorID = 2;
    }

    public static final class ControllerConstants {
        public static final class DriverXbox {
            public static final int kControllerID = 0;
            public static final double kThumbstickDeadband = 0.15;
            public static final double kTriggerDeadband = 0.3;

            public static final double kCrawlTranslationMultiplier = 0.2;
            public static final double kCrawlRotationMultiplier = 0.3;
            public static final double kRotationDesaturationFactor = 0.1;
        }
        public static final class OperatorXbox {
            //Add if necessary
        }
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

        public static final class HeadingPID {
            public static final double kP = 15;
            public static final double kD = 0.2;
        }

        public static final class PathPlannerTranslationPID {
            public static final double kP = 5;
        }

        public static final class PathPlannerRotationPID {
            public static final double kP = 5;
        }
    }

    public static final class VisionConstants{
            public static final int kLidarPort = 0;
        }

    
}
