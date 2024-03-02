package frc.robot.io.controlscheme;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.io.ControlScheme;
import frc.robot.utils.OmniHID;
import static frc.robot.utils.OmniHID.ControllerType;
import static frc.robot.utils.OmniHID.ControllerPairing;

public class SingleXboxScheme extends ControlScheme {
    XboxController m_controller = new XboxController(0);

    public SingleXboxScheme() {
        super("Single Xbox Control Scheme", new ControllerPairing(ControllerType.kGamepad, ControllerType.kNone));
    }

    public double[] driveAxes() {
        Translation2d input = new Translation2d(-m_controller.getLeftY(), -m_controller.getLeftX()); //fix for NW CC+
        //double quadrantAngle = Math.abs(input.getAngle().getDegrees()) % 90;
        // input = input.div(1.12);
        // if (Math.abs(x) >= 0.99 || Math.abs(y) >= 0.99) {
        //     input = new Translation2d(1, input.getAngle());
        // } else {
        //     input = new Translation2d(squareInput(applyDeadbandSpecial(input.getNorm())), input.getAngle());
        // }
        if (input.getNorm() > 1) {
            input = new Translation2d(1, input.getAngle());
        }
        double newTheta = OmniHID.squareInput(OmniHID.applyDeadbandSpecial(-m_controller.getRightX(), DriverXbox.kThumbstickDeadband));
        input = new Translation2d(Math.min(input.getNorm(), OmniHID.applyMultiplier(Math.abs(newTheta), DriverXbox.kRotationDesaturationFactor)), input.getAngle()); //Mildly reduce translation speed to boost rotation speed when moving at full speed
        double[] newInputs = new double[]{input.getX(), input.getY(), newTheta};
        return newInputs;
    }

    public double crawlMode() {
        return m_controller.getLeftTriggerAxis();
    }

    public boolean robotCentricToggle() {
        return m_controller.getRightTriggerAxis() > 0.1;
    }

    public boolean intake() {
        return m_controller.getRightBumper();
    }

    public boolean outtake() {
        return m_controller.getLeftBumper();
    }

    public boolean shoot() {
        return m_controller.getPOV() == 90;
    }

    public boolean resetGyro() {
        return m_controller.getStartButton();
    }
}
