// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.reflect.InaccessibleObjectException;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.*;
import frc.robot.subsystems.SpinNEOS;
import monologue.Logged;

public class RobotContainer {

  CommandXboxController m_driverController = new CommandXboxController(0);

  Drivetrain m_drivetrain = new Drivetrain();

  
  public RobotContainer() {

    m_drivetrain.setDefaultCommand(m_drivetrain.driveOpenLoopThrottleCommand(() -> m_driverController.getLeftY(), () -> m_driverController.getLeftX(), () -> m_driverController.getRightX(), () -> m_driverController.getLeftTriggerAxis()));
    configureBindings();
  }

  private void configureBindings() {
  
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
