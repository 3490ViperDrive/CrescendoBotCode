package frc.robot.utils.omnihid.controlschemes;

import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Pivot;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.TrapLift;
import frc.robot.utils.CommandContainer;
import frc.robot.utils.omnihid.OmniHID.ControllerPairing;

import static frc.robot.utils.omnihid.OmniHID.ControllerType.*;
import static frc.robot.utils.omnihid.OmniHID.ControllerPairing;

import static frc.robot.Constants.ControllerConstants.*;

public class SingleXbox extends ControlScheme {

    final Drivetrain drivetrain;
    final Intake intake;
    final Pivot pivot;
    final Shooter shooter;
    final TrapLift lift;
    final Climber climber;
    final CommandContainer commandContainer;

    CommandXboxController driverController = new CommandXboxController(0); 

    public SingleXbox(Drivetrain drivetrain, Intake intake, Pivot pivot, Shooter shooter, TrapLift lift, Climber climber, CommandContainer commandContainer) {
        super("Single Xbox Controller Scheme",
            new ControllerPairing(kGamepad, kNone));
        this.drivetrain = drivetrain;
        this.intake = intake;
        this.pivot = pivot;
        this.shooter = shooter;
        this.lift = lift;
        this.climber = climber;
        this.commandContainer = commandContainer;
    }

    @Override
    public void addDefaultCommands() {
        drivetrain.setDefaultCommand( //TODO swap to driveTeleopGeneric and do input filtering here
            drivetrain.driveTeleopCommand(
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
        driverController.start().onTrue(drivetrain.zeroYawCommand());
    }
}
