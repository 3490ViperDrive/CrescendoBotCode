package frc.robot;

/**
 * Class that contains global constants for any IDs that usually must be unique.
 * This encompasses CAN IDs, DIO ports and analog ports on the RIO, and HID ports.
 */
public final class HardwareIds {
    public static final class Canbus {
        //TODO move drivetrain can ids here as well

        public static final int kShooterID = 13;
        public static final int kPivotID = 15;
        public static final int kLiftID = 14;
        public static final int kIntakeID = 20;
        public static final int kClimberID = 21;
    }

    public static final class Dio {
        public static final int kPivotEncoder = 0;
        public static final int kBeamBreak = 9;
        public static final int kLeds = 4;
    }

    public static final class Hid {
        public static final int kDriverPort = 0;
        public static final int kOperatorPort = 1; //if necessary for control scheme
    }
}
