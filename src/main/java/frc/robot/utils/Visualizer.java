package frc.robot.utils;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

import static frc.robot.Constants.LiftConstants.*;

/*
 * Helper class that holds Mechanism2ds for better visualisation of mechanisms.
 * Subsystems are responsible for updating this in their periodic methods.
 */
public class Visualizer {
    private static Visualizer instance = new Visualizer();

    private Mechanism2d liftPivotMechanism;
    private MechanismRoot2d root;
    private MechanismLigament2d liftBase;
    private MechanismLigament2d liftExtension;
    private MechanismLigament2d pivot;

    private final double lowerLimitOffset = Units.inchesToMeters(1);

    private Visualizer() {
        liftPivotMechanism = new Mechanism2d(Units.inchesToMeters(30), Units.inchesToMeters(48)); //TODO constantify all these magic numbers
        root = liftPivotMechanism.getRoot("Lift Pivot Mech Root", Units.inchesToMeters(24), Units.inchesToMeters(kHeightFromGround));
        liftBase = new MechanismLigament2d("Lift Base", Units.inchesToMeters(kLowerLimitHeight) - lowerLimitOffset, 90, 6, new Color8Bit(Color.kBlue));
        liftExtension = new MechanismLigament2d("Lift Extension", Units.inchesToMeters(lowerLimitOffset), 0, 3, new Color8Bit(Color.kCyan));
        pivot = new MechanismLigament2d("Pivot", Units.inchesToMeters(6), -45, 8, new Color8Bit(Color.kOrangeRed));
        root.append(liftBase);
        liftBase.append(liftExtension);
        liftExtension.append(pivot);
        SmartDashboard.putData("Lift Pivot Mechanism", liftPivotMechanism);
        DataLogManager.log("MechanismManager created");
    }

    public static Visualizer getInstance() {
        return instance;
    }

    //Degrees
    public void setPivotAngle(double angle) {
        pivot.setAngle(-(90 - angle));
    }

    //Meters
    public void setLiftDistance(double distance) {
        liftExtension.setLength(Units.inchesToMeters(distance) + lowerLimitOffset);
    }
}
