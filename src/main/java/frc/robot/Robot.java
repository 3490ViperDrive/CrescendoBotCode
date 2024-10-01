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

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.utils.omnihid.OmniHID;
import frc.robot.utils.omnihid.controlschemes.ControlScheme;
import frc.robot.utils.omnihid.controlschemes.SingleJoystick;
import frc.robot.utils.omnihid.controlschemes.SingleXbox;

public class Robot extends TimedRobot implements Logged {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;


  ControlScheme m_singleJoystickScheme;
  ControlScheme m_singleXboxScheme;
  OmniHID m_omniHID;

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();

    m_singleJoystickScheme = new SingleJoystick(m_robotContainer);
    m_singleXboxScheme = new SingleXbox(m_robotContainer);
    m_omniHID = new OmniHID(m_robotContainer::configureControllerAgnosticBindings, 
      //Ugly
      new Subsystem[] {m_robotContainer.m_drivetrain,
                       m_robotContainer.m_pivot, 
                       m_robotContainer.m_shooter,
                       m_robotContainer.m_intake,
                       m_robotContainer.m_climber,
                       m_robotContainer.m_lift},
      m_singleJoystickScheme,
      m_singleXboxScheme);

    DriverStation.silenceJoystickConnectionWarning(true);
    Monologue.setupMonologue(this, "Robot", false, false);
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog(), true); //Log joystick data
    CameraServer.startAutomaticCapture(); //TODO add driver overlay

    //TODO: move from Robot.java into <as of yet undefined> UIManager
    dashboardUI();
    
    addPeriodic(() -> Monologue.setFileOnly(DriverStation.isFMSAttached() && Robot.isReal()), 1);
    addPeriodic(m_omniHID::refreshControllers, 3);
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
    /*
    m_controllerchoice.setDefaultOption("Xbox Controller", XboxController);
    m_controllerchoice.addOption("Joystick", Joystick);
    SmartDashboard.putData("Controller Choice", m_controllerchoice);*/
  }
}