package frc.robot.utils.omnihid.controlschemes;

import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Pivot;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.TrapLift;
import frc.robot.utils.CommandContainer;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

import static frc.robot.utils.omnihid.OmniHID.ControllerType.*;
import static frc.robot.utils.omnihid.OmniHID.ControllerPairing;


public class SingleJoystick extends ControlScheme {
    
    final Drivetrain drivetrain;
    final Intake intake;
    final Pivot pivot;
    final Shooter shooter;
    final TrapLift lift;
    final Climber climber;
    final CommandContainer commandContainer;

    CommandJoystick driverJoystick = new CommandJoystick(0); 

    public SingleJoystick(Drivetrain drivetrain, Intake intake, Pivot pivot, Shooter shooter, TrapLift lift, Climber climber, CommandContainer commandContainer) {
        super("Single Joystick Scheme",
            new ControllerPairing(kJoystick, kNone));
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
        drivetrain.setDefaultCommand(
          drivetrain.driveTeleopCommandGeneric(
            ()-> driverJoystick.getRawAxis(1),
            ()-> driverJoystick.getRawAxis(0),
            ()-> -driverJoystick.getRawAxis(2),
            ()-> driverJoystick.button(7).getAsBoolean())
          );
    }

    @Override
    public void configureBindings() {
        driverJoystick.button(1).whileTrue(intake.takeInFancy());
        driverJoystick.button(2).whileTrue(commandContainer.shootFancy(0.6125)); //Shoot regular;
        driverJoystick.button(5).whileTrue(commandContainer.retractIntakeFancy());
        //TODO add shoot low power
        //TODO make button 8 "crawl" (button press)
        //TODO robot oriented toggle on 12
        driverJoystick.button(3).onTrue(commandContainer.ampHandoffScore()); //Score Amp
        driverJoystick.button(9).whileTrue(climber.climb(0.75)); //TODO "lift up"
        driverJoystick.button(11).whileTrue(climber.climb(-0.75)); //TODO "lift down"
        driverJoystick.button(10).toggleOnTrue(commandContainer.raisePivotLiftForClimb());
        driverJoystick.button(12).onTrue(drivetrain.zeroYawCommand());
    }

}
