package frc.robot.io;

import edu.wpi.first.wpilibj.DigitalInput;

public class BreakBeam {
 
    private boolean lastStatus;
    private boolean tripped;
    private boolean cleared;

    private final DigitalInput mBreak;

    public BreakBeam(int channel){
        mBreak = new DigitalInput(channel);
    }
    
    public void update(){
        boolean value = get();
        tripped = value && !lastStatus;
        cleared = value && lastStatus;
        lastStatus = value;
    }

    public boolean get(){
        return !mBreak.get();
    }

    public boolean wasTripped(){
        return tripped;
    }

    public boolean wasCleared(){
        return cleared;
}
}
