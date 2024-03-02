// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.commands.PathPlannerAuto;

// import java.lang.reflect.InaccessibleObjectException;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.*;
// import frc.robot.subsystems.SpinNEOS;
// import monologue.Logged;
import frc.robot.subsystems.vision.Optometrist;

public class RobotContainer {

  CommandXboxController m_driverController = new CommandXboxController(0);

  CommandXboxController m_operatorController = new CommandXboxController(1);

  CommandJoystick m_driverJoystick = new CommandJoystick(1);

  Drivetrain m_drivetrain = new Drivetrain();
  private Optometrist eyedoctor = new Optometrist();

  public Command getAutonomusCommand() {
      return new PathPlannerAuto("yetAnotherTestAuto");
  }

  
  public RobotContainer() {

    m_drivetrain.setDefaultCommand(m_drivetrain.driveOpenLoopThrottleCommand(
      /*() -> m_driverController.getLeftY(), 
      () -> m_driverController.getLeftX(), 
      () -> m_driverController.getRightX(), 
      () -> m_driverController.getLeftTriggerAxis()));*/
      () -> m_driverJoystick.getRawAxis(1),
      () -> m_driverJoystick.getRawAxis(2),
      () -> m_driverJoystick.getRawAxis(3),
      () -> m_driverJoystick.getRawAxis(4)));

    configureBindings();
  }

  private void configureBindings() {

    
    m_driverJoystick.button(1).onTrue(intake.takeIn);
    m_driverJoystick.button(2).onTrue(shooter.shoot);
    m_driverJoystick.button(3).onTrue(lift.lower);
    m_driverJoystick.button(5).onTrue(lift.raise);
    m_driverJoystick.button(6).onTrue(intake.takeOut);
    m_driverJoystick.button(7).whileTrue(crawlmode);
    m_driverJoystick.button(8).onTrue(climb.climbUp);
    m_driverJoystick.button(10).onTrue(climb.climbDown);
    m_driverJoystick.button(11).onTrue(lift.home);
    
    m_driverController.rightBumper().onTrue(eyedoctor.peek());
      

  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
