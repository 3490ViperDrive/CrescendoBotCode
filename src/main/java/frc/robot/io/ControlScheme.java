package frc.robot.io;

import frc.robot.utils.OmniHID;
import monologue.Logged;


public abstract class ControlScheme implements Logged {

    public final String name;
    public final OmniHID.ControllerPairing pairing;

    protected ControlScheme(String name, OmniHID.ControllerPairing pairing) {
        this.name = name;
        this.pairing = pairing;
    }

    /**
     * X, Y, and Theta for the drive teleop command.
     * Should be of length 3 and each entry should be [-1, 1].
     * This should follow NWU CC+ convention of WPILib - some axes may need to be inverted!
     */
    public abstract double[] driveAxes();

    /** [0, 1] where 0 does not reduce robot speed and 1 about halves robot speed. */
    public abstract double crawlMode();

    public abstract boolean robotCentricToggle();

    public abstract boolean intake();

    public abstract boolean outtake();

    public abstract boolean shoot();

    public abstract boolean resetGyro();

    //Probably want to override these, but they're not super necessary
    public boolean presetDirectionUp() {
        return false;
    }

    public boolean presetDirectionDown() {
        return false;
    }

    public boolean presetDirectionLeft() {
        return  false;
    }

    public boolean presetDirectionRight() {
        return false;
    }

    
}
