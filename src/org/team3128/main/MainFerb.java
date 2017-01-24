package org.team3128.main;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.hardware.encoder.both.QuadratureEncoder;
import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.misc.TwoSpeedGearshift;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.listener.controltypes.Button;
import org.team3128.common.listener.controltypes.POV;
import org.team3128.common.util.Log;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Victor;

public class MainFerb extends NarwhalRobot {
	
	public SRXTankDrive drive;
	
	public CANTalon leftDriveFront, leftDriveBack;
	public CANTalon rightDriveFront, rightDriveBack;
	
	public CANTalon shooterMotorRight, shooterMotorLeft;

	public Victor lowerIntakeMotor, shooterIntakeMotor;
	
	public Victor lifterMotor;
	
	public Victor gearRollerMotor;
	
	public ListenerManager lmRight;
	public Joystick rightJoystick;
	
	public TwoSpeedGearshift gearshift;
	public Piston gearshiftPistons;
	
	public Piston rightGearPiston, leftGearPiston, topGearPiston; //...

	public PowerDistributionPanel powerDistPanel;
	public Compressor compressor;
	
	@Override
	protected void constructHardware() {
		lmRight = new ListenerManager(rightJoystick);
		
		addListenerManager(lmRight);
		
		leftDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		leftDriveBack.changeControlMode(TalonControlMode.Follower);
		leftDriveBack.set(leftDriveFront.getDeviceID());
		
		rightDriveBack.changeControlMode(TalonControlMode.Follower);
		rightDriveBack.set(rightDriveFront.getDeviceID());
		
		gearshift = new TwoSpeedGearshift(false, gearshiftPistons);
		
		// Add actual measurements... :)
		drive = new SRXTankDrive(leftDriveFront, rightDriveBack, 0, 0, 0, 0);
		
		shooterMotorRight.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		shooterMotorLeft.changeControlMode(TalonControlMode.Follower);
		shooterMotorLeft.set(shooterMotorRight.getDeviceID());
		shooterMotorLeft.reverseOutput(true);
		
		powerDistPanel = new PowerDistributionPanel();
		compressor = new Compressor();
		
		Log.info("MainFerb", "Activating Ferb");
        Log.info("MainFerb", "Come on Perry!");
	}

	@Override
	protected void setupListeners() {
		lmRight.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
		lmRight.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
		lmRight.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");
		lmRight.nameControl(ControllerExtreme3D.TRIGGER, "Shoot");
				
		lmRight.nameControl(new POV(0), "IntakePOV");
		lmRight.nameControl(new Button(2),"GearShift");
		lmRight.nameControl(new Button(4),"FullSpeed");
		lmRight.nameControl(new Button(7),"Climb");
		lmRight.nameControl(new Button(9), "StartCompressor");
		lmRight.nameControl(new Button(10), "StopCompressor");
		lmRight.nameControl(new Button(11),"GearRoller");
		lmRight.nameControl(new Button(12),"GearJab");
		
		lmRight.addMultiListener(() -> {
			drive.arcadeDrive(lmRight.getAxis("MoveTurn"),
					lmRight.getAxis("MoveForwards"),
					lmRight.getAxis("Throttle"),
					lmRight.getButton("FullSpeed"));
		
		}, "MoveTurn", "MoveForwards", "Throttle", "FullSpeed");
		
		lmRight.addListener("ClearStickyFaults", () ->
		{
			powerDistPanel.clearStickyFaults();
		});
		
		lmRight.addListener("Shift", () -> 
		{
			gearshift.shiftToOtherGear();
		
		});
		
		lmRight.addListener("StartCompressor", () -> 
		{
			compressor.start();
		});
		
		lmRight.addListener("StopCompressor", () -> 
		{
			compressor.stop();
		});
		
	}

	@Override
	protected void teleopInit() {
		
	}

	@Override
	protected void autonomousInit() {
		
	}

}
