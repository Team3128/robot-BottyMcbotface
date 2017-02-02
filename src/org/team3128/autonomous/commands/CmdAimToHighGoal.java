package org.team3128.autonomous.commands;

import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.util.Log;
import org.team3128.common.util.PIDCalculator;
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.util.units.Angle;
import org.team3128.main.MainFerb;
import org.team3128.narwhalvision.AveragedTargetInformation;
import org.team3128.narwhalvision.NarwhalVisionReceiver;
import org.team3128.narwhalvision.TargetInformation;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command which points the robot at the high goal using the vision system
 * @author Narwhal
 *
 */
public class CmdAimToHighGoal extends Command
{
	private NarwhalVisionReceiver visionRec;
	private SRXTankDrive drive;
	private PIDCalculator pidCalc;
	private Servo visionAimServo;
	private long lastVisionUpdateTime;
	
	// offset of the goal from the center of the vision system when the robot is pointed toward the goal and at the correct distance.
	private static int GOAL_PIXEL_OFFSET = -100;
	
	public CmdAimToHighGoal(MainFerb ferb, PIDConstants pidConstants, int timeout) 
	{
		super(timeout / 1000.0);
		this.drive = ferb.drive;
		this.visionRec = ferb.visionReceiver;
		this.visionAimServo = ferb.visionAimServo;
		pidCalc = new PIDCalculator(pidConstants, 10, 4 * Angle.DEGREES);
	}
	
	@Override
	public void initialize()
	{
		lastVisionUpdateTime = System.currentTimeMillis();
		drive.stopMovement();
		visionAimServo.setAngle(MainFerb.VISION_SERVO_GEAR_ANGLE);
	}
	
	@Override
	public void execute()
	{
		//only update if no packaet has been received
		if(lastVisionUpdateTime < visionRec.getLastPacketReceivedTime())
		{
			TargetInformation[] targets = visionRec.getMostRecentTargets();
			
			if(targets.length == 2)
			{
				lastVisionUpdateTime = visionRec.getLastPacketReceivedTime();
				
				AveragedTargetInformation goalLocation = new AveragedTargetInformation(GOAL_PIXEL_OFFSET, 0, targets);
				double output = pidCalc.update(goalLocation.getHorizontalAngle());
				drive.tankDrive(-output, output);
			}
			else
			{
				Log.unusual("AimToHighGoalCmd", "Got wrong number of vision targets.  Expected 2, got " + targets.length);
			}
		}
	}

	@Override
	protected boolean isFinished() 
	{
		//time out if the command timer expires, or if we did not receive a vision packet for more than 2 seconds
		boolean timeout = isTimedOut() || System.currentTimeMillis() - lastVisionUpdateTime > 2000;
		
		return timeout || pidCalc.getNumUpdatesInsideThreshold() > 2;
	}

}
