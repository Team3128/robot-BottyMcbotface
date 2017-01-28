package org.team3128.testmainclasses;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controltypes.Button;

import edu.wpi.first.wpilibj.Joystick;

public class MainGearMechanismTest extends NarwhalRobot{
	Piston gearPiston;
	ListenerManager lmRight;
	
	Joystick rightJoy;
	
	boolean pistonOn = false;
	
	@Override
	protected void constructHardware()
	{
		gearPiston = new Piston(0,1,true,false);
		gearPiston.setPistonOff();
		
		rightJoy = new Joystick(0);
		
		lmRight = new ListenerManager(rightJoy);
		
		addListenerManager(lmRight);
	}
	
	@Override
	protected void setupListeners() {
		lmRight.nameControl(new Button(2), "TogglePiston");
		
		lmRight.addButtonDownListener("TogglePiston", () ->
		{
			gearPiston.setPistonInvert();
//			if (pistonOn) {
//				gearPiston.setPistonOff();
//			}
//			else {
//				gearPiston.setPistonOn();
//			}
		});
		
	}
	
	@Override
	public void teleopInit()
	{
		
	}
	
	@Override
	public void teleopPeriodic()
	{
		
	}

	@Override
	protected void autonomousInit()
	{
		
	}
	
	@Override
	protected void updateDashboard() {

	}
}
