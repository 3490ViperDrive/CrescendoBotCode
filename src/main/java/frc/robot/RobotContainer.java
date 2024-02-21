// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.subsystems.Drivetrain;
import monologue.Logged;
import static frc.robot.Constants.ControllerConstants.*;

public class RobotContainer implements Logged {
  CommandXboxController m_driverController = new CommandXboxController(DriverXbox.kControllerID);
  Drivetrain m_drivetrain = new Drivetrain();

  public RobotContainer() {
    configureBindings();
    m_drivetrain.setDefaultCommand(
      m_drivetrain.driveTeleopCommand(
        m_driverController::getLeftY,
        m_driverController::getLeftX,
        m_driverController::getRightX,
        m_driverController::getLeftTriggerAxis,
        m_driverController.getHID()::getAButton,
        m_driverController.getHID()::getBButton,
        m_driverController.getHID()::getXButton,
        m_driverController.getHID()::getYButton,
        () -> m_driverController.getRightTriggerAxis() > DriverXbox.kThumbstickDeadband) //this is evil but i can't think of a better way of doing it
    );
  }

  private void configureBindings() {

    // Bind different button to a specific functionality (do the thing)

    // thisButton.onTrue(do this thing)

    m_driverController.start().onTrue(m_drivetrain.zeroYawCommand());
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command(s) configured");
  }
}
