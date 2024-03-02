// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.sql.Connection;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;


  private double temp = 123;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private RobotContainer m_robotContainer;

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();

    ShuffleBoardUI();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  public void ShuffleBoardUI() {
   ShuffleboardTab tab = Shuffleboard.getTab("ShuffleBoard tab");

   Shuffleboard.getTab("ShuffleBoard tab")
   .add("Speedometer", BuiltInWidgets.k3AxisAccelerometer);

   Shuffleboard.getTab("ShuffleBoard tab")
   .add("random thing", BuiltInWidgets.kGyro);

   Shuffleboard.getTab("ShuffleBoard tab")
   .add("Random thing v2", BuiltInWidgets.kCameraStream);

   Shuffleboard.getTab("ShuffleBoard tab")
    .add("Random widget", BuiltInWidgets.kField);

  Shuffleboard.getTab("ShuffleBoard tab")
    .add("random code", BuiltInWidgets.kCommand);
  }

  @Override
  public void testExit() {}
}
