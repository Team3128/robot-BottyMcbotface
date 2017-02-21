package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.VictorSP;

public class MainFerbCompetition extends MainFerb {
	
	public MainFerbCompetition() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{
		gearRollerMotor = new VictorSP(1);
		
		floorIntakeMotor = new VictorSP(2);
		climberMotor = new MotorGroup(new VictorSP(3));
				
		gearPiston = new Piston(2, 5);
		doorPiston = new Piston(1, 6);
		
		gearshiftPistons = new Piston(0, 7);
		
		gearInputSensor = new DigitalInput(5);
		
		visionAimServo = new Servo(9);
		
		super.constructHardware();
	}
}
