package frc.robot.utils;

import java.util.HashMap;
import java.util.Objects;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Notifier;
import frc.robot.Constants.ControllerConstants.DriverXbox;
import frc.robot.io.ControlScheme;
import monologue.Logged;
import monologue.Annotations.Log;

public class OmniHID implements Logged {

    public enum ControllerType {
        kNone,
        kUnrecognized,
        kGamepad,
        kJoystick
    }

    //should a different data structure be used to store this?
    public record ControllerPairing(ControllerType mainController, ControllerType auxController) {
        public ControllerPairing {
            Objects.requireNonNull(mainController);
            Objects.requireNonNull(auxController);
        }
    }

    public static final GenericHID kMainHID = new GenericHID(0);
    public static final GenericHID kAuxHID = new GenericHID(1);

    ControlScheme currentScheme;
    ControlScheme defaultScheme;
    ControllerPairing lastDetectedPairing;
    private HashMap<ControllerPairing, ControlScheme> schemeMap;
    Notifier updateNotifier;

    public OmniHID(ControlScheme defaultScheme, ControlScheme... additionalSchemes) {
        Objects.requireNonNull(defaultScheme);
        this.currentScheme = defaultScheme;
        this.defaultScheme = defaultScheme;
        schemeMap = new HashMap<ControllerPairing, ControlScheme>(additionalSchemes.length + 1);
        //Set up all control schemes in the scheme map
        schemeMap.put(defaultScheme.pairing, defaultScheme);
        reportStatus(
            "Registered default control scheme with pairing "
            + defaultScheme.pairing
            + " and name "
            + defaultScheme.name, false);
        for (ControlScheme scheme : additionalSchemes) {
            if (schemeMap.containsKey(scheme.pairing)) {
                reportStatus(
                    "Attempted to register additional control scheme with pairing "
                    + scheme.pairing
                    + " and name "
                    + scheme.name
                    + ", but a scheme with this pairing already exists: "
                    + schemeMap.get(scheme.pairing).name
                    + "!", true);
            } else {
                schemeMap.put(scheme.pairing, scheme);
                reportStatus(
                    "Registered additional control scheme with pairing "
                    + scheme.pairing
                    + " and name "
                    + scheme.name, false);
            }
        }
        lastDetectedPairing = getCurrentControllerPairing();
        updateNotifier = new Notifier(this::refreshControllers);
        updateNotifier.startPeriodic(1); //TODO adjust this for optimal overhead
        reportStatus("Setup complete", false);
    }

    @Log
    private ControllerPairing getCurrentControllerPairing() {
        return new ControllerPairing(
            getCurrentControllerType(kMainHID.getType()),
            getCurrentControllerType(kAuxHID.getType()));
    }

    private ControllerType getCurrentControllerType(GenericHID.HIDType type) {
        if(Objects.isNull(type)) {
            return ControllerType.kNone;
        }
        switch(type) {
            case kHIDGamepad:
            case kXInputGamepad:
            return ControllerType.kGamepad;
            case kHIDJoystick:
            return ControllerType.kJoystick;
            default:
            return ControllerType.kUnrecognized;
        }
    }

    public static double applyDeadbandSpecial(double value, double deadband) {
        return MathUtil.inverseInterpolate(deadband, 1, MathUtil.applyDeadband(Math.abs(value), deadband)) * Math.signum(value);
    }

    public static double squareInput(double value) {
        return Math.pow(Math.abs(value), 2) * Math.signum(value);
    }

    public static double applyMultiplier(double value, double multiplier) {
        return 1 - (value * multiplier);
    }

    private void reportStatus(String message, boolean warn) {
        if (warn) {
            DriverStation.reportWarning("[OmniHID] " + message, false);
        }
        DataLogManager.log("[OmniHID] " + message);
    }

    private void refreshControllers() {
        ControllerPairing currentPairing = getCurrentControllerPairing();
        if (lastDetectedPairing.equals(currentPairing)) return;
        if (currentPairing.mainController == ControllerType.kNone && currentPairing.auxController == ControllerType.kNone) {
            currentScheme = defaultScheme;
            reportStatus(
                "No controllers are connected!\n"
                + "Are all controllers properly plugged in?\n"
                + "Falling back to default control scheme...", true);
        } else {
            if (!schemeMap.containsKey(currentPairing)) {
                currentScheme = defaultScheme;
                reportStatus(
                    "Controller pairing "
                    + currentPairing
                    + " has no corresponding registered control scheme!\n"
                    + "Are all controllers plugged in, and are all controllers properly ordered in the DriverStation interface?\n"
                    + "Falling back to default control scheme...", true);
            } else {
                currentScheme = schemeMap.get(currentPairing);
                reportStatus("Controller pairing set to " + currentScheme.name, false);
            }
        }
        lastDetectedPairing = currentPairing;
    }
}
