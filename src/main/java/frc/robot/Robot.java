//TODO: Remove the shuffleboard stuff from robot.java and move it 'dashboardUI'
//TODO: Actually add code for autonomous in/under center(default) autonomous 

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import com.ctre.phoenix6.SignalLogger;

import monologue.Logged;
import monologue.Monologue;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;


import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;


// import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
// import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
// import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
// import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
// import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot implements Logged {
  private Command m_autonomousCommand;

  //private double temp = 123;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private RobotContainer m_robotContainer;

  //TODO: declutter the Robot class, move these variables into a more appropriate context
  private static final String kDefaultCenterAuto = "Center, Default Autonomous";
  private static final String kRightAuto = "Right Autonomous";
  private static final String kLeftAuto = "Left Autonomous"; 
  private String m_autoSelected;

  //double counter = 0.0;

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();

    DriverStation.silenceJoystickConnectionWarning(true);
    DataLogManager.start();
    DriverStation.startDataLog(DataLogManager.getLog(), true);
    //Comment this out if SysID testing needs to be run; this should save some overhead in most cases
    SignalLogger.stop();
    ShuffleBoardUI();
    dashboardUI();
    coltonsCode();
    Monologue.setupMonologue(this, "Robot", false, false);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
    Monologue.setFileOnly(DriverStation.isFMSAttached());
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

    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kDefaultCenterAuto:
        default: 
        // Shoot the pre-loaded note
        // Drive and pick up a note from ground
        // Drive back
        // Shoot the note into the speaker

        break;

      case kRightAuto:
        // Autonomous that can shoot into the center speaker AND/OR can place the note into the right amp
        // Cross the line as well

        break;

      case kLeftAuto:
       // Drive to amp --> turn facing towards the amp
       // Place in the note
       // Turn
       // Drive towards to pick a note
       // Pick a note
       // Drive back to amp
       // Place the note into the amp

        break;

        //TODO: Make different autonomous routines depending on where we start and things we have to do depending on the situation

    }
  }

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    //SmartDashboard.putNumber("Counter", counter++);

  }

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    //CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  private void dashboardUI(){
    m_chooser.setDefaultOption("Default, Center Auto", kDefaultCenterAuto);
    m_chooser.addOption("Right Auto", kRightAuto);
    m_chooser.addOption("Left Auto", kLeftAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  public void ShuffleBoardUI() {
   ShuffleboardTab tab = Shuffleboard.getTab("ShuffleBoard test");

    Shuffleboard.getTab("ShuffleBoard test")
      .add("Started?", 1);

      Shuffleboard.getTab("ShuffleBoard test")
        .add("testing 2", 1 + 214);

      // Shuffleboard.getTab("ShuffleBoard test")
      //   .add("Variable thing", temp);

      Shuffleboard.getTab("ShuffleBoard test")
        .add("Slider test", 1)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .getEntry();
  }

   //Colton's code from github
  private void coltonsCode(){
    //ShuffleboardTab tab = 
      Shuffleboard.getTab("Practice")
        .add("Slider Test", 1)
        .withWidget(BuiltInWidgets.kNumberSlider)
        .getEntry();
  }
}
  