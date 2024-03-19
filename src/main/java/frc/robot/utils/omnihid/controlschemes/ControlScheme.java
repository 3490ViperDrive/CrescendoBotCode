package frc.robot.utils.omnihid.controlschemes;

import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.utils.omnihid.OmniHID;
import frc.robot.utils.omnihid.OmniHID.ControllerPairing;
import monologue.Logged;


public abstract class ControlScheme implements Logged {

    public final String name;
    public final OmniHID.ControllerPairing pairing;

    /**
     * Creates a new ControlScheme.
     * @param name the name of the control scheme that will appear in logs and on the dashboard.
     * @param pairing the types of controllers this control scheme is designed for.
     */
    protected ControlScheme(String name, OmniHID.ControllerPairing pairing) {
        this.name = name;
        this.pairing = pairing;
    }

    public abstract void addDefaultCommands();

    /** Any controls should bind to the Command Scheduler's default button loop. */
    public abstract void configureBindings();
}