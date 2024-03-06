package frc.robot;

import edu.wpi.first.math.util.Units;
import frc.robot.generated.TunerConstants;

public final class Constants {

    // My Constant Id's aren't the right ones 

    public static final class ShooterConstants {
        public static final double kShooterSpeed = 0.5;
        public static final int kShooterMotorID = 13;
        public static final double kShooterMotorStopSpeed = 0;
    }

    public static final class IntakeConstants {
        public static final double kIntakeSpeed = 1;
        public static final int kIntakeMotorID = 20;
        public static final double kIntakeMotorStopSpeed = 0;
        public static final double kCurrentThreshold = 18; //amps
        public static final double kCurrentSpikeTime = 0.2; //secs
        //1 second is literally perfect for a handoff at 0.75 intake speed
        public static final double kPullInTime = 0.35;
    }

    public static final class ClimberConstants {
        public static final double kLiftSpeed = 0.25;
        public static final int kLiftMotorID = 21;
        public static final double kLiftMotorStopSpeed = 0;
    }

    public static final class DigitalInputConstants {
        public static final int kDigitalInputPort = 1;
        public static final int kSparkDIO = 2;
        //public static final int kDigitalInputLimit = 1;
    }

    // Remove zeros and put meaningful int

    // kp - The proportional coefficient.
    // ki - The integral coefficient.
    // kd - The derivative coefficient.
    public static final class ShooterPID {
        public static final double kP = 0;
        public static final double kD = 0;
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
            public static final int kControllerID = 1;
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

    public static final class LiftConstants {
        public static final int kMotorID = 14;

        public static final double kHeightFromGround = 5.94; //All in inches
        public static final double kLowerLimitHeight = 12.5; //Relative to height from ground
        public static final double kUpperLimitHeight = 32.5;
        //I'm not sure why an upper limit of 6 works, but testing shows it just does, so I won't question it for now
        public static final double kUpperLimitDistance = 6; //kUpperLimitHeight - kLowerLimitHeight; //Relative to lower limit
        public static final double kLowerLimitDistance = 0;
        public static final double kSetpointTolerance = 0.25; //tune this

        public static final double kGearRatio = 70;
        public static final double kInchesToRotationsRatio = Math.PI * 1.790 * 2 * 3.854; //Circumference of sprocket pitch diameter x2 for two stage times dark number
        public static final double kRotationsToInchesRatio = 1/kInchesToRotationsRatio;

        public static final double kFwdVoltageLimit = 4; //Voltage limits are used as a simple 'speed limit' for the lift.
        public static final double kBkwdVoltageLimit = -2;

        public static final class PIDGains {
            public static final double kP = 6;
            public static final double kD = 0.1;
        }

        public static final class FFGains {
            public static final double kG = 0.7;
        }
    }

    public static final class PivotConstants {
        public static final int kMotorID = 15;
        public static final int kEncoderChannel = 0;
        public static final double kEncoderOffset = 273; //degrees; adjust such that 0 degrees matches with horizontal

        public static final double kLowerLimit = -45;
        public static final double kUpperLimit = 58;

        public static final double kSetpointTolerance = 2; //tune this

        public static final double kGearRatio = 100; //2 10:1 versaplanetary stages

        public static final double kFwdVoltageLimit = 8;
        public static final double kBkwdVoltageLimit = -8;

        public static final class PIDGains {
            public static final double kP = 128;
            public static final double kD = 0;
        }

        public static final class FFGains {
            
        }
    }

    public static enum LiftPivotSetpoint {
        kStowed(0, 45, "Stowed"),
        kShoot(0, 55, "Shoot"),
        kAmp(4, -30, "Amp"),
        kTrap(19, -45, "Trap"); //TODO find empirical setpoints; all of these are guessed

        public final double liftDistance; //inches
        public final double pivotAngle; //degrees
        private final String name;

        private LiftPivotSetpoint(double liftDistance, double pivotAngle, String name) {
            this.liftDistance = liftDistance;
            this.pivotAngle = pivotAngle;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public static final class VisionConstants{
            public static final int kLidarPort = 0;
        }
}
