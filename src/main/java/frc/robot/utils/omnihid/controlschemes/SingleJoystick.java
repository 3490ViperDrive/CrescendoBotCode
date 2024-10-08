package frc.robot.utils.omnihid.controlschemes;

import frc.robot.RobotContainer;
import frc.robot.utils.omnihid.OmniHID.ControllerPairing;
import monologue.Annotations.IgnoreLogged;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;

import static frc.robot.utils.omnihid.OmniHID.ControllerType.*;
import static frc.robot.utils.omnihid.OmniHID.ControllerPairing;


public class SingleJoystick extends ControlScheme {
    
    @IgnoreLogged //RobotContainer is already logged in Robot.java
    final RobotContainer robotContainer;

    CommandJoystick driverJoystick = new CommandJoystick(0); 

    public SingleJoystick(RobotContainer robotContainer) {
        super("Single Joystick Scheme",
            new ControllerPairing(kJoystick, kNone));
        this.robotContainer = robotContainer;
    }

    @Override
    public void addDefaultCommands() {
        robotContainer.m_drivetrain.setDefaultCommand(
          robotContainer.m_drivetrain.driveTeleopCommandGeneric(
            ()-> -driverJoystick.getRawAxis(1),
            ()-> -driverJoystick.getRawAxis(0),
            ()-> -driverJoystick.getRawAxis(2),
            ()-> driverJoystick.button(7).getAsBoolean())
          );
    }

    @Override
    public void configureBindings() {
        driverJoystick.button(1).onTrue(robotContainer.m_intake.takeIn().until(() -> !robotContainer.m_intake.getBeamBreak()));
        driverJoystick.button(2).whileTrue(robotContainer.m_commandContainer.shootFancy(0.6125));
        driverJoystick.button(3).onTrue(robotContainer.m_commandContainer.ampHandoffScore());
        driverJoystick.button(4).whileTrue(robotContainer.m_commandContainer.wetShoot(0.8, 37.5));
        driverJoystick.button(5).whileTrue(robotContainer.m_commandContainer.retractIntakeFancy());
        driverJoystick.button(7).onTrue(robotContainer.m_drivetrain.toggleRobotCentric());
        driverJoystick.button(8).onTrue(robotContainer.m_drivetrain.toggleCrawling());
        driverJoystick.button(9).whileTrue(robotContainer.m_climber.climb(-0.75));
        driverJoystick.button(10).toggleOnTrue(robotContainer.m_commandContainer.raisePivotLiftForClimb());
        driverJoystick.button(11).whileTrue(robotContainer.m_climber.climb(0.75));
        driverJoystick.button(12).onTrue(robotContainer.m_drivetrain.zeroYawCommand());
    }

}
