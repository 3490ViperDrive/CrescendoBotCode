package frc.robot.utils.omnihid;

import java.util.HashMap;
import java.util.Objects;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.utils.omnihid.controlschemes.ControlScheme;
import edu.wpi.first.wpilibj.Notifier;
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

    ControlScheme currentScheme;
    ControlScheme defaultScheme;
    ControllerPairing lastDetectedPairing;
    HashMap<ControllerPairing, ControlScheme> schemeMap;
    Subsystem[] subsystems;
    Runnable controllerAgnosticSetter;

    public OmniHID(Runnable controllerAgnosticSetter, Subsystem[] subsystems, ControlScheme defaultScheme, ControlScheme... additionalSchemes) {
        Objects.requireNonNull(defaultScheme);
        this.currentScheme = defaultScheme;
        this.defaultScheme = defaultScheme;
        this.subsystems = subsystems;
        this.controllerAgnosticSetter = controllerAgnosticSetter;
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
        replaceCurrentPairing(defaultScheme);
        lastDetectedPairing = getCurrentControllerPairing();
        reportStatus("Setup complete", false);
    }

    @Log
    private ControllerPairing getCurrentControllerPairing() {
        return new ControllerPairing(
            getCurrentControllerType(0),
            getCurrentControllerType(1));
    }

    private ControllerType getCurrentControllerType(int joystickID) {
        GenericHID.HIDType type = HIDType.of(DriverStation.getJoystickType(joystickID));
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

    public void refreshControllers() {
        ControllerPairing currentPairing = getCurrentControllerPairing();
        if (lastDetectedPairing.equals(currentPairing)) return;
        if (currentPairing.mainController == ControllerType.kNone && currentPairing.auxController == ControllerType.kNone) {
            replaceCurrentPairing(defaultScheme);
            reportStatus(
                "No controllers are connected!\n"
                + "Are all controllers properly plugged in?\n"
                + "Falling back to default control scheme...", true);
        } else {
            if (!schemeMap.containsKey(currentPairing)) {
                replaceCurrentPairing(defaultScheme);
                reportStatus(
                    "Controller pairing "
                    + currentPairing
                    + " has no corresponding registered control scheme!\n"
                    + "Are all controllers plugged in, and are all controllers properly ordered in the DriverStation interface?\n"
                    + "Falling back to default control scheme...", true);
            } else {
                replaceCurrentPairing(schemeMap.get(currentPairing));
                reportStatus("Controller pairing set to " + currentScheme.name, false);
            }
        }
        lastDetectedPairing = currentPairing;
    }

    private void replaceCurrentPairing(ControlScheme scheme) {
        //Remove current default commands
        for (Subsystem subsystem : subsystems) {
            CommandScheduler.getInstance().getDefaultCommand(subsystem).cancel();
            CommandScheduler.getInstance().removeDefaultCommand(subsystem);
        }
        //Clear the default button event loop
        CommandScheduler.getInstance().getDefaultButtonLoop().clear();
        //Add new default commands and bindings
        scheme.addDefaultCommands();
        scheme.configureBindings();
        controllerAgnosticSetter.run();
        currentScheme = scheme;
    }

}
