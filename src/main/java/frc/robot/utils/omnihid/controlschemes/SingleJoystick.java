package frc.robot.utils.omnihid.controlschemes;

import frc.robot.RobotContainer;
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
        driverJoystick.button(1).whileTrue(robotContainer.m_intake.takeInFancy());
        driverJoystick.button(2).whileTrue(robotContainer.m_commandContainer.shootFancy(0.6125)); //Shoot regular;
        driverJoystick.button(5).whileTrue(robotContainer.m_commandContainer.retractIntakeFancy());
        //TODO add shoot low power
        //TODO make button 8 "crawl" (button press)
        //TODO robot oriented toggle on 12
        driverJoystick.button(3).onTrue(robotContainer.m_commandContainer.ampHandoffScore()); //Score Amp
        driverJoystick.button(9).whileTrue(robotContainer.m_climber.climb(0.75)); //TODO "lift up"
        driverJoystick.button(11).whileTrue(robotContainer.m_climber.climb(-0.75)); //TODO "lift down"
        driverJoystick.button(10).toggleOnTrue(robotContainer.m_commandContainer.raisePivotLiftForClimb());
        driverJoystick.button(12).onTrue(robotContainer.m_drivetrain.zeroYawCommand());
    }

}
