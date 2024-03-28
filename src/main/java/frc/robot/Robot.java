// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import monologue.Logged;
import monologue.Monologue;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot implements Logged {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  public static final String XboxController = "XboxController";
  public static final String Joystick = "JoyStick";
  public static final SendableChooser<String> m_controllerchoice = new SendableChooser<>();

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();

    DriverStation.silenceJoystickConnectionWarning(true);
    Monologue.setupMonologue(this, "Robot", false, false);
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog()); //Log joystick data
    CameraServer.startAutomaticCapture(); //TODO add driver overlay

    dashboardUI();
    DataLogManager.log("Robot code running on commit with hash " + BuildConstants.GIT_SHA
      + "\ncreated on " + BuildConstants.GIT_DATE 
      + "\non branch " + BuildConstants.GIT_BRANCH
      //VSCode will complain. Ignore it
      + ((BuildConstants.DIRTY == 1) ? ", with some uncommitted changes." : ".")
      + "\nCode built on " + BuildConstants.BUILD_DATE + ".");
    // This does not need to be called every 20ms, but still needs to be called periodically
    addPeriodic(() -> Monologue.setFileOnly(DriverStation.isFMSAttached() && Robot.isReal()), 1);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    Monologue.updateAll();
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
    m_robotContainer.setDriverPerspective();
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
    m_robotContainer.setDriverPerspective();
  }

  @Override
  public void teleopPeriodic() {}

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  private void dashboardUI(){
    // Colton's code below ;w;
    m_controllerchoice.setDefaultOption("Xbox Controller", XboxController);
    m_controllerchoice.addOption("Joystick", Joystick);
    SmartDashboard.putData("Controller Choice", m_controllerchoice);
  }
}