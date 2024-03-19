package frc.robot.utils.omnihid.controlschemes;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import monologue.Annotations.IgnoreLogged;
import frc.robot.RobotContainer;

import static frc.robot.utils.omnihid.OmniHID.ControllerType.*;
import static frc.robot.utils.omnihid.OmniHID.ControllerPairing;

public class SingleXbox extends ControlScheme {

    @IgnoreLogged
    final RobotContainer robotContainer;

    CommandXboxController driverController = new CommandXboxController(0); 

    public SingleXbox(RobotContainer robotContainer) {
        super("Single Xbox Controller Scheme",
            new ControllerPairing(kGamepad, kNone));
            this.robotContainer = robotContainer;
    }

    @Override
    public void addDefaultCommands() {
        robotContainer.m_drivetrain.setDefaultCommand( //TODO swap to driveTeleopGeneric and do input filtering here
            robotContainer.m_drivetrain.driveTeleopCommand(
            driverController::getLeftY,
            driverController::getLeftX,
            driverController::getRightX,
            driverController::getLeftTriggerAxis,
            driverController.getHID()::getAButton,
            driverController.getHID()::getBButton,
            driverController.getHID()::getXButton,
            driverController.getHID()::getYButton,
            () -> driverController.getRightTriggerAxis() > DriverXbox.kThumbstickDeadband));
    }

    @Override
    public void configureBindings() {
        driverController.start().onTrue(robotContainer.m_drivetrain.zeroYawCommand());
    }
}
