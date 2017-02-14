package org.team3128.autonomous.commands;

import org.team3128.common.drive.SRXTankDrive;
import org.team3128.main.MainFerb;
import org.team3128.mechanisms.PhoneCamera;
import org.team3128.mechanisms.PhoneCamera.AimDirection;
import org.team3128.mechanisms.PhoneCamera.AimMode;

import edu.wpi.first.wpilibj.command.Command;

public class CmdAimForGearDeposit extends Command
{
	private PhoneCamera phoneCamera;
	private SRXTankDrive drive;
	
	public CmdAimForGearDeposit(MainFerb ferb, int timeout) 
	{
		super(timeout / 1000.0);
		this.drive = ferb.drive;
		this.phoneCamera = ferb.phoneCamera;
	}
	
	@Override
	public void initialize()
	{
		drive.stopMovement();
		phoneCamera.setMode(AimMode.GEAR);
	}
	
	@Override
	public void execute()
	{
		double output = 0;
		if (!phoneCamera.inThreshold(AimDirection.HORIZONTAL_GEAR)) {
			output = phoneCamera.getOutput(AimDirection.HORIZONTAL_GEAR);
			drive.tankDrive(output, -output);
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
		
		return timeout || phoneCamera.inThreshold(AimDirection.HORIZONTAL_GEAR);
	}

}
