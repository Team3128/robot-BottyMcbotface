package org.team3128.testmainclasses;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.POVValue;
import org.team3128.common.listener.controltypes.POV;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;

public class MainSolenoidTest extends NarwhalRobot 
{
	Solenoid sol0, sol1, sol2, sol3, sol4, sol5;
	
	Joystick rightJoystick;
	ListenerManager lmRight;
	
	@Override
	protected void constructHardware() {
		sol0 = new Solenoid(0);
		sol1 = new Solenoid(1);
		sol2 = new Solenoid(2);
		sol3 = new Solenoid(3);
		sol4 = new Solenoid(4);
		sol5 = new Solenoid(5);
	}

	@Override
	protected void setupListeners() {
		rightJoystick = new Joystick(0);
		lmRight = new ListenerManager(rightJoystick);
		
		lmRight.nameControl(new POV(0), "SolenoidPOV");
		
		lmRight.addListener("SolenoidPOV", (POVValue value) -> {
			switch(value.getDirectionValue())
			{
			case 0:
				sol0.set(true);
				break;
			case 1:
				sol1.set(true);
				break;
			case 2:
				sol2.set(true);
				break;
			case 3:
				sol3.set(true);
				break;
			default:
				break;
			
			}
		});
	}

	@Override
	protected void teleopInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void autonomousInit() {
		// TODO Auto-generated method stub
		
	}

}
