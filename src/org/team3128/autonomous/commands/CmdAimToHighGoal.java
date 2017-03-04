package org.team3128.autonomous.commands;

import org.team3128.common.drive.SRXTankDrive;
import org.team3128.main.MainFerb;
import org.team3128.mechanisms.PhoneCamera;
import org.team3128.mechanisms.PhoneCamera.AimDirection;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command which points the robot at the high goal using the vision system
 * 
 * @author Narwhal
 *
 */
public class CmdAimToHighGoal extends Command
{
	private PhoneCamera phoneCamera;
	private SRXTankDrive drive;
	
	public CmdAimToHighGoal(MainFerb ferb, int timeout) 
	{
		super(timeout / 1000.0);
		this.drive = ferb.drive;
		this.phoneCamera = ferb.phoneCamera;
	}
	
	@Override
	public void initialize()
	{
		drive.stopMovement();
	}
	
	@Override
	public void execute()
	{
		double output = 0;
		if (!phoneCamera.inThreshold(AimDirection.HORIZONTAL_SHOOTER)) {
			output = phoneCamera.getOutput(AimDirection.HORIZONTAL_SHOOTER);
			drive.tankDrive(output, -output);
		}
		else if (!phoneCamera.inThreshold(AimDirection.VERTICAL)) {
			output = phoneCamera.getOutput(AimDirection.VERTICAL);
			drive.tankDrive(output, output);
		}
		
	}
	
	@Override
	protected void end() {
		drive.stopMovement();
	}
	
	@Override
	protected void interrupted()
    {
    	end();
	}
	
	@Override
	protected boolean isFinished() 
	{
		//time out if the command timer expires, or if we did not receive a vision packet for more than 2 seconds
		boolean timeout = isTimedOut() || phoneCamera.timeSinceLastPacket() > 2000;
		
		return timeout || phoneCamera.inThreshold(AimDirection.HORIZONTAL_SHOOTER) && phoneCamera.inThreshold(AimDirection.VERTICAL);
	}

}
