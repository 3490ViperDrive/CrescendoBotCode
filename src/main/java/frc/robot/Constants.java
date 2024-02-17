package frc.robot;

import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import frc.lib.ModuleConstants;

public final class Constants {
    public static final class ControllerConstants {
        public static final int kControllerID = 0;
        public static final double kControllerDeadband = 0.15;
    }

    //Units of distance should be in metric
    public static final class DrivetrainConstants {
        public static final double kWheelbase = Units.inchesToMeters(27); //Guessed
        public static final double kTrackWidth = Units.inchesToMeters(27);

        public static final double kWheelDiameter = Units.inchesToMeters(4);
        public static final double kWheelCircumference = kWheelDiameter * Math.PI;

        public static final SwerveDriveKinematics kKinematics = new SwerveDriveKinematics( //Module locations are clockwise starting from the front left module
            new Translation2d(kWheelbase/2, kTrackWidth/2),
            new Translation2d(kWheelbase/2, -kTrackWidth/2),
            new Translation2d(-kWheelbase/2, -kTrackWidth/2),
            new Translation2d(-kWheelbase/2, kTrackWidth/2)
        );

        //TODO ensure IDs match robot
        public static final ModuleConstants kFrontLeftModule = new ModuleConstants(1, 5, 9, Rotation2d.fromDegrees(28.82), "Front Left Module");
        public static final ModuleConstants kFrontRightModule = new ModuleConstants(2, 6, 10, Rotation2d.fromDegrees(132.09), "Front Right Module");
        public static final ModuleConstants kBackRightModule = new ModuleConstants(3, 7, 11, Rotation2d.fromDegrees(196.00), "Back Right Module");
        public static final ModuleConstants kBackLeftModule = new ModuleConstants(4, 8, 12, Rotation2d.fromDegrees(237.30), "Back Left Module");

        public static final double kDriveGearing = 6.75; //6.75 : 1; MK4 L2 ratio
        public static final double kAzimuthGearing = 12.8; //12.8 : 1; steering ratio for all SDS modules

        public static final InvertedValue kInvertDriveMotor = InvertedValue.Clockwise_Positive;
        public static final InvertedValue kInvertAzimuthMotor = InvertedValue.CounterClockwise_Positive;
        public static final SensorDirectionValue kInvertAbsEncoder = SensorDirectionValue.CounterClockwise_Positive; //these all should be default

        public static final NeutralModeValue kDriveMotorIdleMode = NeutralModeValue.Brake;
        public static final NeutralModeValue kAzimuthMotorIdleMode = NeutralModeValue.Coast;

        public static final boolean kEnableDriveCurrentLimits = true;
        public static final boolean kEnableAzimuthCurrentLimits = true;
        
        public static final double kDriveSupplyCurrentLimit = 35;
        public static final double kDriveSupplyCurrentLimitThreshold = 60; //amps
        public static final double kDriveStatorCurrentLimit = 60; //amps
        public static final double kDriveSupplyCurrentLimitTime = 0.1; //sec

        public static final double kAzimuthSupplyCurrentLimit = 25; //amps
        public static final double kAzimuthSupplyCurrentLimitThreshold = 40; //amps
        public static final double kAzimuthSupplyCurrentLimitTime = 0.1; //sec

        public static final double kMaxTranslationSpeed = 4.5; //m/s; test on robot
        public static final double kMaxRotationSpeed = 10; //m/s; test on robot
        public static final double kMaxModuleSpeed = 10; //m/s; guessed

        //TODO ensure PID constants are appropriate
        public static final class DriveMotorClosedLoopConstants {
            public static final double kP = 6;
            public static final double kI = 0;
            public static final double kD = 0;
            public static final double kS = 0;
            public static final double kV = 0;
        }

        public static final class AzimuthMotorClosedLoopConstants {
            public static final double kP = 30; //TODO tune this
            public static final double kI = 0;
            public static final double kD = 0;
        }
    }
    
    public static final class IntakeConstants {
        public static final int indexSensor = 1;
        public static final double intakeMotorSpeed = 0.75;
    }
    public static final class VisionConstants{
            public static final int kLidarPort = 0;
        }

}
