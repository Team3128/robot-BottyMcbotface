package org.team3128.main;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.util.GenericSendableChooser;
import org.team3128.narwhalvision.NarwhalVisionReceiver;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class MainTargetTrackingTest extends NarwhalRobot{
		Servo trackingServo;
		Joystick joystick;
		ListenerManager lm;
		NarwhalVisionReceiver visionRec;
	
		@Override
		protected void constructHardware()
		{	
			joystick = new Joystick(0);
			lm = new ListenerManager(joystick);
			addListenerManager(lm);
			trackingServo = new Servo(1);
			visionRec =  new NarwhalVisionReceiver();
		}
		
		@Override
		protected void setupListeners()
		{
			lm.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
			lm.addMultiListener(()-> {
				lm.getAxis("MoveForwards");
			}, "MoveForwards");
		}
		@Override
		protected void teleopPeriodic(){
			double power = joystick.getRawAxis(ControllerExtreme3D.JOYY.getIndex());
			//Log.info("info", "The motor power is " + power);
			//trackingServo.setPosition(.5);
			
		}

		@Override
		protected void disabledInit()
		{

		}

		@Override
		protected void autonomousInit()
		{
			
		}
		
		@Override
		protected void teleopInit()
		{	
			
		}
		
		@Override
		protected void constructAutoPrograms(GenericSendableChooser<CommandGroup> autoChooser)
		{
			
		}

		@Override
		protected void updateDashboard()
		{
//			if(System.currentTimeMillis() - visionRec.getLastPacketReceivedTime() < 100 && visionRec.getMostRecentTargets().length > 0)
//			{
//				Log.debug("MTTT", "Most recent target: " + visionRec.getMostRecentTargets()[0]);
//			}
		}

	}
