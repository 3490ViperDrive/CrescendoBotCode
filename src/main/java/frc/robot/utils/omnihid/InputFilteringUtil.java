package frc.robot.utils.omnihid;

import edu.wpi.first.math.MathUtil;

/**
 * Utility class for certain input filtering methods.
 */
public class InputFilteringUtil {
    private InputFilteringUtil() {}

    public static double applyDeadbandSpecial(double value, double deadband) {
        return MathUtil.inverseInterpolate(deadband, 1, MathUtil.applyDeadband(Math.abs(value), deadband)) * Math.signum(value);
    }

    public static double squareInput(double value) {
        return Math.pow(Math.abs(value), 2) * Math.signum(value);
    }

    public static double applyMultiplier(double value, double multiplier) {
        return 1 - (value * multiplier);
    }

    public static double triggerToAxis(double leftTrigger, double rightTrigger) {
        return rightTrigger - leftTrigger;
    }
}
