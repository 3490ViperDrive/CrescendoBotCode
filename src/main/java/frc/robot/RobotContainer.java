// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandGenericHID;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;

import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.subsystems.*;
import frc.robot.utils.CommandContainer;

import monologue.Logged;

import com.pathplanner.lib.commands.PathPlannerAuto;

public class RobotContainer implements Logged {

  //TODO: player input will be handled by implementation of omnicontroller/genericHID/yadda
  CommandXboxController m_driverController = new CommandXboxController(HardwareIds.Hid.kDriverPort); //TODO: constant can live here
  CommandJoystick m_driverJoystick = new CommandJoystick(HardwareIds.Hid.kDriverPort);  
  
  Drivetrain m_drivetrain = new Drivetrain();
  Pivot m_pivot = new Pivot();
  Shooter m_shooter = new Shooter();
  Intake m_intake = new Intake();
  Climber m_climber = new Climber();
  TrapLift m_lift = new TrapLift();

  //TODO: move to UIManager class
  SendableChooser<Command> m_chooser = new SendableChooser<>();
   
  //TODO address later
  CommandContainer m_commandContainer = new CommandContainer(m_intake, m_pivot, m_shooter, m_climber, m_lift);

  public RobotContainer() {

    setupChooser();

    m_pivot.setDefaultCommand(m_pivot.requestPosition(55));
    m_lift.setDefaultCommand(m_lift.idle());

    //TODO replace "Joystick" magic string with variable retrieved from UIManager
    setDriveDefault(m_driverController, "Joystick");
    configureBindings();
  }

  private void configureBindings() {

    //TODO: implement with omnicontroller
    m_driverJoystick.button(1).onTrue(m_intake.takeIn(1).until(() -> !m_intake.getBeamBreak()));
    m_driverJoystick.button(2).whileTrue(m_commandContainer.shootFancy(0.6125));
    m_driverJoystick.button(3).onTrue(m_commandContainer.ampHandoffScore());
    m_driverJoystick.button(4).whileTrue(m_commandContainer.wetShoot(0.8, 37.5));
    m_driverJoystick.button(5).whileTrue(m_commandContainer.retractIntakeFancy());
    m_driverJoystick.button(7).onTrue(m_drivetrain.toggleRobotCentric());
    m_driverJoystick.button(8).onTrue(m_drivetrain.toggleCrawling());
    m_driverJoystick.button(9).whileTrue(m_climber.climb(-0.75));
    m_driverJoystick.button(10).toggleOnTrue(m_commandContainer.raisePivotLiftForClimb());
    m_driverJoystick.button(11).whileTrue(m_climber.climb(0.75));
    m_driverJoystick.button(12).onTrue(m_drivetrain.zeroYawCommand());

  }
  
  public Command getAutonomousCommand() {
    return m_chooser.getSelected();
  }

  //Robot.java needs to call this, but m_drivetrain is not visible. There may be a better way to resolve this
  public void setDriverPerspective() {
    m_drivetrain.setDriverPerspective();
  }


  //TODO move implementation from here to OmniController
  public void setDriveDefault(CommandGenericHID m_driverJoystick, String whichType){
    switch(whichType){
      case "Joystick":
          m_drivetrain.setDefaultCommand(
          m_drivetrain.driveTeleopCommandGeneric(
            ()-> m_driverJoystick.getRawAxis(1),
            ()-> m_driverJoystick.getRawAxis(0),
            ()-> -m_driverJoystick.getRawAxis(2),
            ()-> m_driverJoystick.button(7).getAsBoolean())
          );
        break;
      case "Xbox Controller":
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
          () -> m_driverController.getRightTriggerAxis() > DriverXbox.kThumbstickDeadband)
          );
        break;
      default:
        break;
    }
  }

  void setupChooser(){
    m_chooser.setDefaultOption("(All)2 note middle auto",new PathPlannerAuto("simpleCenter"));
    m_chooser.addOption("2 Note Amp Side", new PathPlannerAuto("(Blue)Amp2Note"));
    m_chooser.addOption("2 Note Stage Side", new PathPlannerAuto("(Blue)Stage2Note"));
    m_chooser.addOption("(All)3 note middle centerline auto", new PathPlannerAuto("middleCenterlineAuto"));
    m_chooser.addOption("3 Note, Amp -> Center", new PathPlannerAuto("leftCenterlineAuto"));
    m_chooser.addOption("3 Note, Stage -> Center", new PathPlannerAuto("rightCenterlineAuto"));
    m_chooser.addOption("4 Note Middle Auto", new PathPlannerAuto("(All)Middle4Note"));
    m_chooser.addOption("3 Note Auto, NO AMP", new PathPlannerAuto("BlueAmpless3Note"));
    m_chooser.addOption("3 Note Auto, NO STAGE", new PathPlannerAuto("BlueStageless3Note"));
    m_chooser.addOption("1 Note, Just Shoot", new PathPlannerAuto("(All)MiddleJustShoot"));
    m_chooser.addOption("1 Note, Just Shoot (FROM AMP)", new PathPlannerAuto("(Blue)AmpJustShoot"));
    m_chooser.addOption("1 Note, Just Shoot", new PathPlannerAuto("(Blue)StageJustShoot"));
    m_chooser.addOption("No Auto", new PrintCommand("No auto was selected. Why would you do this?"));
    SmartDashboard.putData("THE AutoChoices", m_chooser);
  }
}
