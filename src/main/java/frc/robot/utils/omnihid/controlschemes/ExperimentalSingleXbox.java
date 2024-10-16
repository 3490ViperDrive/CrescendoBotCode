package frc.robot.utils.omnihid.controlschemes;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RobotContainer;
import frc.robot.utils.omnihid.InputFilteringUtil;
import frc.robot.utils.omnihid.OmniHID.ControllerPairing;

import static frc.robot.utils.omnihid.OmniHID.ControllerType.*;

import static frc.robot.utils.omnihid.OmniHID.ControllerPairing;
import frc.robot.subsystems.Drivetrain.DriveMode;
import monologue.Logged;
import monologue.Annotations.IgnoreLogged;

public class ExperimentalSingleXbox extends ControlScheme implements Logged {
    
    public static final double kThumbstickDeadband = 0.1;
    public static final double kTriggerDeadband = 0.15;
    public static final double kCrawlMultiplier = 0.225;
    //public static double kBindAngleDebounce = 2; //seconds

    public static final double kAmpAngle = 90;
    public static final double kShootCenterAngle = 180;
    public static final double kIntakeAngle = 125;
    public static final double kShootLeftAngle = 135; //driver's left and right
    public static final double kShootRightAngle = 205;

    @IgnoreLogged
    private final RobotContainer robotContainer;

    private CommandXboxController driverController = new CommandXboxController(1);

    //trusting that this class won't put itself into an invalid state
    private boolean isAngleBound = false;
    private double angle = 0;

    public ExperimentalSingleXbox(RobotContainer robotContainer) {
        super("Experimental Single Xbox Controller Scheme",
            new ControllerPairing(kNone, kGamepad)); //TODO this is bad
            this.robotContainer = robotContainer;
    }

    @Override
    public void addDefaultCommands() {
        robotContainer.m_drivetrain.setDefaultCommand(
            robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterLeftStick(driverController.getLeftX(), 
                                      driverController.getLeftY()),
                () -> filterTriggers(driverController.getLeftTriggerAxis(),
                                     driverController.getRightTriggerAxis()),
            DriveMode.kFieldCentric)
        );
    }

    @Override
    public void configureBindings() {
        driverController.back().onTrue(robotContainer.m_drivetrain.zeroYawCommand());

        driverController.rightBumper().whileTrue( //backup cam driving
            robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterLeftStick(driverController.getLeftX(), 
                                      -driverController.getLeftY()).times(kCrawlMultiplier),
                () -> filterTriggers(driverController.getLeftTriggerAxis(),
                                     driverController.getRightTriggerAxis()) * kCrawlMultiplier,
                DriveMode.kRobotCentric));

        driverController.rightBumper().negate().and(() -> isAngleBound).whileTrue(robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterLeftStick(driverController.getLeftX(), 
                                      driverController.getLeftY()),
                () -> angle,
                DriveMode.kFieldCentricFacingAngle));

        new Trigger(() -> axesAreActive(kTriggerDeadband, driverController.getLeftTriggerAxis(), driverController.getRightTriggerAxis()))
            .and(() -> isAngleBound)
            .onTrue(disableAngleBindCmd());

        driverController.a().whileTrue(new ConditionalCommand( //shoot
            enableAngleBindCmd(kShootCenterAngle),
            robotContainer.m_commandContainer.shootFancy(0.6125), 
            driverController.leftBumper()));
        
        driverController.start().whileTrue(new ConditionalCommand( //amp
            enableAngleBindCmd(kAmpAngle),
            robotContainer.m_commandContainer.ampHandoffScore(), 
            driverController.leftBumper()));

        driverController.y().whileTrue(new ConditionalCommand( //intake
            enableAngleBindCmd(kIntakeAngle),
            robotContainer.m_intake.takeIn().until(() -> !robotContainer.m_intake.getBeamBreak()), 
            driverController.leftBumper()));

        driverController.b().whileTrue(new ConditionalCommand( //downfield 45 deg to the right ; retract intake
            enableAngleBindCmd(kShootRightAngle),
            robotContainer.m_commandContainer.retractIntakeFancy(), 
            driverController.leftBumper()));

        driverController.x().whileTrue(new ConditionalCommand( //downfield 45 deg to the left ; alt shoot cmd
            enableAngleBindCmd(kShootLeftAngle),
            robotContainer.m_commandContainer.wetShoot(0.8, 37.5), 
            driverController.leftBumper()));
    }    

    //should these methods get moved to InputFilteringUtil?
    private double filterAxis(double axisValue, double deadband) {
        return InputFilteringUtil.squareInput(
            InputFilteringUtil.applyDeadbandSpecial(axisValue, deadband));
    }

    private double filterTriggers(double leftTrigger, double rightTrigger) {
        return InputFilteringUtil.squareInput(
            InputFilteringUtil.applyDeadbandSpecial(leftTrigger, kTriggerDeadband)
            - InputFilteringUtil.applyDeadbandSpecial(rightTrigger, kTriggerDeadband));
    }

    private Translation2d filterLeftStick(double leftX, double leftY) {
        //Convert cartesian to polar
        double translationDistance = Math.hypot(-leftX, -leftY);
        double translationAngle = Math.atan2(-leftY, -leftX);
        translationDistance = filterAxis(Math.min(translationDistance, 1), kThumbstickDeadband);
        //Convert (filtered) polar back to cartesian
        double x = translationDistance * Math.cos(translationAngle);
        double y = translationDistance * Math.sin(translationAngle);
        //Use the "X right, Y up" option; center origin; 2x2
        log("Controller X for Ascope", new double[]{leftX, -x, 0});
        log("Controller Y for Ascope", new double[]{-leftY, y, 0});
        return new Translation2d(y, x); //Y up, X left (controller axes) -> X up, Y left (WPILib NWU convention)
    }

    private boolean axesAreActive(double deadband, double... axes) {
        boolean activeAxisExists = false;
        for (double currentAxis : axes) {
            if (Math.abs(currentAxis) > deadband) {
                activeAxisExists = true;
            }
        }
        return activeAxisExists;
    }

    private Command enableAngleBindCmd(double angle) {
        return new InstantCommand(() -> {
            this.angle = angle;
            isAngleBound = true;
        });
    }

    private Command disableAngleBindCmd() {
        return new InstantCommand(() -> {
            isAngleBound = false;
            this.angle = 0;
        });
    }
}
