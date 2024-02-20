// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Drivetrain;
import monologue.Logged;

public class RobotContainer implements Logged {
  CommandXboxController m_controller = new CommandXboxController(0);
  Drivetrain m_drivetrain = new Drivetrain();

  public RobotContainer() {
    configureBindings();
    m_drivetrain.setDefaultCommand(
      m_drivetrain.driveTeleopCommand(
        m_controller::getLeftY,
        m_controller::getLeftX,
        m_controller::getRightX,
        m_controller::getLeftTriggerAxis,
        m_controller.getHID()::getAButton,
        m_controller.getHID()::getBButton,
        m_controller.getHID()::getXButton,
        m_controller.getHID()::getYButton)
    );
  }

  private void configureBindings() {

    // Bind different button to a specific functionality (do the thing)

    // thisButton.onTrue(do this thing)

    m_controller.start().onTrue(m_drivetrain.zeroYawCommand());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
