package frc.robot.utils.omnihid.controlschemes;

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
import monologue.Annotations.IgnoreLogged;

public class ExperimentalSingleXbox extends ControlScheme {
    
    public static double kThumbstickDeadband = 0.1;
    public static double kTriggerDeadband = 0.3;
    public static double kCrawlMultiplier = 0.225;
    public static double kBindAngleDebounce = 2; //seconds

    @IgnoreLogged
    private final RobotContainer robotContainer;

    private CommandXboxController driverController = new CommandXboxController(1); 
    private XboxController driverControllerHID = driverController.getHID();

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
        /*
        robotContainer.m_drivetrain.setDefaultCommand( //TODO swap to driveTeleopGeneric and do input filtering here
            robotContainer.m_drivetrain.driveTeleopCommand(
                driverController::getLeftY,
                driverController::getLeftX,
                () -> driverController.getRightTriggerAxis() - driverController.getLeftTriggerAxis(),
                () -> (driverControllerHID.getLeftBumper()) ? 1 : 0,
                () -> false,
                () -> false,
                () -> false,
                () -> false,
                () -> driverControllerHID.getRightBumper()));
        */
        robotContainer.m_drivetrain.setDefaultCommand(
            robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterAxis(-driverController.getLeftY(), kThumbstickDeadband),
                () -> filterAxis(-driverController.getLeftX(), kThumbstickDeadband),
                () -> filterAxis(driverController.getLeftTriggerAxis() - driverController.getRightTriggerAxis(), kTriggerDeadband),
                DriveMode.kFieldCentric));
    }

    @Override
    public void configureBindings() {
        driverController.back().onTrue(robotContainer.m_drivetrain.zeroYawCommand());

        driverController.rightBumper().whileTrue( //backup cam driving
            robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterAxis(driverController.getLeftY(), kThumbstickDeadband) * kCrawlMultiplier,
                () -> filterAxis(driverController.getLeftX(), kThumbstickDeadband) * kCrawlMultiplier,
                () -> filterAxis(driverController.getLeftTriggerAxis() - driverController.getRightTriggerAxis(), kTriggerDeadband) * kCrawlMultiplier,
                DriveMode.kRobotCentric));

        driverController.rightBumper().negate().and(() -> isAngleBound).whileTrue(robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterAxis(-driverController.getLeftY(), kThumbstickDeadband),
                () -> filterAxis(-driverController.getLeftX(), kThumbstickDeadband),
                () -> angle,
                DriveMode.kFieldCentricFacingAngle));

        new Trigger(() -> axesAreActive(kTriggerDeadband, driverController.getLeftTriggerAxis(), driverController.getRightTriggerAxis()))
            .and(() -> isAngleBound)
            .onTrue(disableAngleBindCmd());

        // driverController.a().and(bindAngleMode(true))
        //     .onTrue(new PrintCommand("yippee"));
        //     driverController.a().and(bindAngleMode(false))
        //     .onTrue(new PrintCommand("wahoo"));

        //TODO constantify and finish controls
        driverController.a().whileTrue(new ConditionalCommand( //shoot
            enableAngleBindCmd(180),
            robotContainer.m_commandContainer.shootFancy(0.6125), 
            driverController.leftBumper()));
        
        driverController.start().whileTrue(new ConditionalCommand( //amp
            enableAngleBindCmd(90),
            robotContainer.m_commandContainer.ampHandoffScore(), 
            driverController.leftBumper()));

        driverController.y().whileTrue(new ConditionalCommand( //intake
            enableAngleBindCmd(125),
            robotContainer.m_intake.takeInFancy().until(() -> !robotContainer.m_intake.getBeamBreak()), 
            driverController.leftBumper()));

        driverController.b().whileTrue(new ConditionalCommand( //downfield 45 deg to the right ; retract intake
            enableAngleBindCmd(205),
            robotContainer.m_commandContainer.retractIntakeFancy(), 
            driverController.leftBumper()));

        driverController.x().whileTrue(new ConditionalCommand( //downfield 45 deg to the left ; alt shoot cmd
            enableAngleBindCmd(135),
            robotContainer.m_commandContainer.wetShoot(0.8, 37.5), 
            driverController.leftBumper()));
    }    

    private double filterAxis(double axisValue, double deadband) {
        return InputFilteringUtil.squareInput(
            InputFilteringUtil.applyDeadbandSpecial(axisValue, deadband));
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

    // private BooleanSupplier bindAngleMode(boolean checkIfEnabled) {
    //     BooleanSupplier mode;
    //     if (checkIfEnabled) {
    //         mode = driverController.leftBumper();
    //     } else {
    //         mode = driverController.leftBumper().negate().debounce(kBindAngleDebounce, DebounceType.kFalling);
    //     }
    //     return mode;
    // }

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
