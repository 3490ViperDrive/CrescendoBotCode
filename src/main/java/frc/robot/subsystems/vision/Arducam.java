// //Copy of this
// //https://docs.photonvision.org/en/latest/docs/examples/aimingatatarget.html

// package frc.robot.subsystems.vision;

// import org.photonvision.PhotonCamera;

// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.units.Units;
// import edu.wpi.first.wpilibj.XboxController;
// import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
// import edu.wpi.first.wpilibj2.command.Command;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;

// public class Arducam extends SubsystemBase {

//     //Constants for camera and target height stored. Temp values until tuned.
//     final double CAMERA_HEIGHT_INCHES = Units.inchesToMeter(24);
//     final double TARGET_HEIGHT_INCHES = Units.inchesToMeter(2);

//     //Angle between horizonal and camera
//     final double CAMERA_PITCH_RADIANS = Units.degreesToRadians(0);

//   // How far from the target we want to be

//     final double GOAL_RANGE_METERS = Units.feetToMeters(3);


//     // Change this to match the name of your camera

//     PhotonCamera camera = new PhotonCamera("photonvision");


//     // PID constants should be tuned per robot

//     final double LINEAR_P = 0.1;

//     final double LINEAR_D = 0.0;

//     PIDController forwardController = new PIDController(LINEAR_P, 0, LINEAR_D);


//     final double ANGULAR_P = 0.1;

//     final double ANGULAR_D = 0.0;

//     PIDController turnController = new PIDController(ANGULAR_P, 0, ANGULAR_D);


//     XboxController xboxController = new XboxController(0);


//     // Drive motors

//     PWMVictorSPX leftMotor = new PWMVictorSPX(0);

//     PWMVictorSPX rightMotor = new PWMVictorSPX(1);

//     DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor);

//     @Override
//     public void periodic() {};

//     public Command aimAtTarget() {
//         double forwardSpeed;

//         double rotationSpeed;


//         forwardSpeed = -xboxController.getRightY();


//         if (xboxController.getAButton()) {

//             // Vision-alignment mode

//             // Query the latest result from PhotonVision

//             var result = camera.getLatestResult();


//             if (result.hasTargets()) {

//                 // Calculate angular turn power

//                 // -1.0 required to ensure positive PID controller effort _increases_ yaw

//                 rotationSpeed = -turnController.calculate(result.getBestTarget().getYaw(), 0);

//             } else {

//                 // If we have no targets, stay still.

//                 rotationSpeed = 0;

//             }

//         } else {

//             // Manual Driver Mode

//             rotationSpeed = xboxController.getLeftX();

//         }


//         // Use our forward/turn speeds to control the drivetrain

//         drive.arcadeDrive(forwardSpeed, rotationSpeed);
//     }
    
// }
