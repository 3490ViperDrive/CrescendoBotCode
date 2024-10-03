package frc.robot.utils.omnihid.controlschemes;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RobotContainer;
import frc.robot.utils.omnihid.InputFilteringUtil;

import static frc.robot.utils.omnihid.OmniHID.ControllerType.*;

import java.util.function.BooleanSupplier;

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
        driverController.rightBumper().whileTrue(
            robotContainer.m_drivetrain.driveTeleopSimpleCmd(
                () -> filterAxis(-driverController.getLeftY(), kThumbstickDeadband) * kCrawlMultiplier,
                () -> filterAxis(-driverController.getLeftX(), kThumbstickDeadband) * kCrawlMultiplier,
                () -> filterAxis(driverController.getLeftTriggerAxis() - driverController.getRightTriggerAxis(), kTriggerDeadband) * kCrawlMultiplier,
                DriveMode.kRobotCentric));
        
        driverController.back().onTrue(robotContainer.m_drivetrain.zeroYawCommand());

        // driverController.a().and(bindAngleMode(true))
        //     .onTrue(new PrintCommand("yippee"));
        //     driverController.a().and(bindAngleMode(false))
        //     .onTrue(new PrintCommand("wahoo"));

        //TODO add actual controls
        driverController.a().onFalse(new ConditionalCommand(
            new PrintCommand("bind angle a"),
            new PrintCommand("intake"), 
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

}
