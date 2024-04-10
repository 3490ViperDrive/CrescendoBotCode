// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import monologue.Logged;
import monologue.Monologue;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSource;
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


  //TODO: move these fields out of Robot.java and into ___ 
  public static final String XboxController = "XboxController";
  public static final String Joystick = "JoyStick";
  public static final SendableChooser<String> m_controllerchoice = new SendableChooser<>();

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();

    DriverStation.silenceJoystickConnectionWarning(true);
    Monologue.setupMonologue(this, "Robot", false, false);
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog());
    CameraServer.startAutomaticCapture(); 

    //TODO: move from Robot.java into <as of yet undefined> UIManager
    dashboardUI();
    
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
    //TODO this can live in RobotContainer's constructor
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
    //TODO we might not need this to exist
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

  //TODO move to <as yet undefined> UIManager class
  private void dashboardUI(){
    // Colton's code below ;w;
    m_controllerchoice.setDefaultOption("Xbox Controller", XboxController);
    m_controllerchoice.addOption("Joystick", Joystick);
    SmartDashboard.putData("Controller Choice", m_controllerchoice);
  }
}