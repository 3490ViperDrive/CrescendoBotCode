package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IndexerConstants;
import frc.robot.io.BreakBeam;

public class Indexer extends SubsystemBase{
    private CANSparkMax indexer1Motor;
    private final BreakBeam mBeam;

    public Indexer(){

        int indexer1MotorID = 15;
        indexer1Motor = new CANSparkMax(indexer1MotorID, MotorType.kBrushless);

        mBeam = new BreakBeam(IndexerConstants.indexSensor);

        //do we need to invert motors?
        //intake1Motor.setInverted(true);
        //intake2Motor.setInverted(true);

        // indexer1Motor.burnFlash();
    
    
    }

    public void forward(){
        indexer1Motor.set(IndexerConstants.indexMotorSpeed);
    }

    public void reverse(){
        indexer1Motor.set(-IndexerConstants.indexMotorSpeed);
    }

    public void hold() {
        indexer1Motor.set(0);
    }

    public boolean getIndexSensor() {
        return !mBeam.get();
    }

    @Override
    public void periodic(){
        SmartDashboard.putBoolean("Intake Sense", !mBeam.get());
    }

}
