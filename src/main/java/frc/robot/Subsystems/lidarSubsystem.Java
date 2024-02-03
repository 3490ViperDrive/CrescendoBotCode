public class lidarSubsystem extends subsystemBase{

    AnalogInput input = new AnalogInput(0);
    input.setAverageBits(2);
    
    AnalogPotentiometer potentiometer = new AnalogPotentiometer(input,180,30);
}