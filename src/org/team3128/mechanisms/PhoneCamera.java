package org.team3128.mechanisms;

import org.team3128.common.util.Log;
import org.team3128.common.util.PIDCalculator;
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.util.units.Angle;
import org.team3128.narwhalvision.AveragedTargetInformation;
import org.team3128.narwhalvision.NarwhalVisionReceiver;
import org.team3128.narwhalvision.SwitchSlotCommand;
import org.team3128.narwhalvision.TargetInformation;

import edu.wpi.first.wpilibj.Servo;

public class PhoneCamera extends NarwhalVisionReceiver {
	//protected final static double VISION_SERVO_GEAR_ANGLE = 0;
	//protected final static double VISION_SERVO_SHOOTER_ANGLE = 32.53;
	
	// offsets of the goal from the center of the vision system when the robot is pointed toward the goal and at the correct distance.
	protected final int GOAL_PIXEL_HORIZ_OFFSET = -100;
	protected final int GOAL_PIXEL_VERTICAL_OFFSET = 0;
		
	//protected final static int GEAR_PIXEL_OFFSET = -100;
	
	public enum AimDirection {
		//HORIZONTAL_GEAR,
		HORIZONTAL_SHOOTER,
		VERTICAL;
	}
	
//	public enum AimMode {
//		GEAR(VISION_SERVO_GEAR_ANGLE, GEAR_PIXEL_OFFSET, 0),
//		SHOOTER(VISION_SERVO_SHOOTER_ANGLE, GOAL_PIXEL_OFFSET, 1),
//		None(0, 0, 0);
//		
//		double angle = 0;
//		int offset = 0;
//		int slot = 0;
//		
//		private AimMode(double angle, int offset, int slot) {
//			this.angle = angle;
//			this.offset = offset;
//			this.slot = slot;
//		}
//		
//		public double getCameraAngle() {
//			return angle;
//		}
//		
//		public int getOffset() {
//			return offset;
//		}
//		
//		public int getSlot() {
//			return slot;
//		}
//	}
	
	// private Servo aimServo;
	// private PIDCalculator horizGearAnglePIDCalc;
	private PIDCalculator horizShooterAnglePIDCalc;
	private PIDCalculator verticalAnglePIDCalc;
	private long lastVisionUpdateTime;
	
	//private AimMode mode;
	
	public PhoneCamera(PIDConstants horizShooterAnglePIDConstants, PIDConstants verticalAnglePIDConstants) {
		//this.aimServo = visionAimServo;
		
		horizShooterAnglePIDCalc = new PIDCalculator(horizShooterAnglePIDConstants, 10, 4 * Angle.DEGREES);
		verticalAnglePIDCalc = new PIDCalculator(verticalAnglePIDConstants, 10, 2 * Angle.DEGREES);
		
		//horizGearAnglePIDCalc = new PIDCalculator(horizShooterAnglePIDConstants, 10, 4 * Angle.DEGREES);
	}
	
//	public void setMode(AimMode mode) {
//		this.mode = mode;
//		aimServo.setAngle(mode.getCameraAngle());
//		sendCommand(new SwitchSlotCommand(mode.getSlot()));
//		
//		lastVisionUpdateTime = System.currentTimeMillis();
//	}
	
	private boolean didRecievePacket() {
		return lastVisionUpdateTime < getLastPacketReceivedTime();
	}
	
	public boolean inThreshold(AimDirection axis) {
		boolean result = false;
		if (axis == AimDirection.HORIZONTAL_SHOOTER) {
			if (horizShooterAnglePIDCalc.getNumUpdatesInsideThreshold() >= 2) {
				result = true;
			}
		}
		else {
			if (verticalAnglePIDCalc.getNumUpdatesInsideThreshold() >= 2) {
				result = true;
			}
		}
//		else if (axis == AimDirection.HORIZONTAL_GEAR) {
//			if (horizGearAnglePIDCalc.getNumUpdatesInsideThreshold() >= 2) {
//				result = true;
//			}
//		}
		
		return result;
	}
	
	public double getOutput(AimDirection axis) {
		double output = -1;
		if(didRecievePacket())
		{
			TargetInformation[] targets = getMostRecentTargets();
			
			if(targets.length == 2)
			{
				lastVisionUpdateTime = getLastPacketReceivedTime();
				
				AveragedTargetInformation goalLocation = new AveragedTargetInformation(GOAL_PIXEL_HORIZ_OFFSET, GOAL_PIXEL_VERTICAL_OFFSET, targets);
				
				if (axis == AimDirection.HORIZONTAL_SHOOTER) {
					output = horizShooterAnglePIDCalc.update(goalLocation.getHorizontalAngle());
				}		
				else {
					output = verticalAnglePIDCalc.update(goalLocation.getVerticalAngle());
				}
//				else if (axis == AimDirection.HORIZONTAL_GEAR) {
//					output = horizGearAnglePIDCalc.update(goalLocation.getHorizontalAngle());
//				}
				
			}
			else
			{
				Log.unusual("PhoneCamera", "Got wrong number of vision targets.  Expected 2, got " + targets.length);
			}
		}
		return output;
	}
	
	public double timeSinceLastPacket() {
		return System.currentTimeMillis() - lastVisionUpdateTime;
	}
}

