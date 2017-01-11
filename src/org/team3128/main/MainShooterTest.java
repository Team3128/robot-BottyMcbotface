package org.team3128.main;


import org.team3128.common.NarwhalRobot;
import org.team3128.common.util.GenericSendableChooser;
import org.team3128.common.util.Log;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Test shooter for our 2017 robot, the Botty Mcbotface.
 */
public class MainShooterTest extends NarwhalRobot
{
	CANTalon shooterMotor;
	
	@Override
	protected void constructHardware()
	{	
		shooterMotor = new CANTalon(0);
		shooterMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		shooterMotor.reverseSensor(false);	
	}
	
	@Override
	protected void setupListeners()
	{
		
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
		shooterMotor.setProfile(0);
		shooterMotor.setD(0);
		shooterMotor.setP(0);
		shooterMotor.setI(0);
		shooterMotor.setF(0);
	}
	
	@Override
	protected void teleopPeriodic()
	{
		Log.info("[MainShooterTest]", "power: " + shooterMotor.get() + "; speed:" + shooterMotor.getSpeed());
	}
	
	@Override
	protected void constructAutoPrograms(GenericSendableChooser<CommandGroup> autoChooser)
	{
		
	}

	@Override
	protected void updateDashboard()
	{
		
	}

}
