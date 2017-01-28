package org.team3128.main;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.hardware.encoder.both.QuadratureEncoder;
import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.misc.TwoSpeedGearshift;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.POVValue;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.listener.controltypes.Button;
import org.team3128.common.listener.controltypes.POV;
import org.team3128.common.util.Log;
import org.team3128.common.util.units.Length;
import org.team3128.main.mechanisms.GearRollerBackDoor;
import org.team3128.main.mechanisms.Shooter;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Victor;

public class MainFerb extends NarwhalRobot 
{
	private final double LOW_GEAR_RATIO = 1;
	private final double HIGH_GEAR_RATIO = 1;
	
	Shooter shooter;
	GearRollerBackDoor gearRollerBackDoor;

	public SRXTankDrive drive;
	public DigitalInput gearInputSensor;
	
	MotorGroup intakeMotors, gearMotors;
	
	public CANTalon leftDriveFront, leftDriveBack;
	public CANTalon rightDriveFront, rightDriveBack;
	
	public CANTalon shooterMotorRight, shooterMotorLeft;

	public Victor lowerIntakeMotor, shooterIntakeMotor;
	
	public Victor lifterMotor;
	
	public Victor gearRollerMotor;
	
	public ListenerManager lmRight;
	public ListenerManager lmLeft;
	
	public Joystick rightJoystick;
	public Joystick leftJoystick;
	
	public TwoSpeedGearshift gearshift;
	public Piston gearshiftPistons;
	
	public Piston doorPiston, gearPiston;

	public PowerDistributionPanel powerDistPanel;
	public Compressor compressor;
	
	@Override
	protected void constructHardware() {
		intakeMotors = new MotorGroup();
		gearMotors = new MotorGroup();
		gearMotors.addMotor(gearRollerMotor);
		intakeMotors.addMotor(lowerIntakeMotor);
		intakeMotors.addMotor(shooterIntakeMotor);
		gearPiston = new Piston(1, 2);
		doorPiston = new Piston(3, 4);
		gearInputSensor = new DigitalInput(5);

		lmRight = new ListenerManager(rightJoystick, leftJoystick);
		
		addListenerManager(lmRight);
		
		shooter = new Shooter(shooterMotorLeft, intakeMotors);
		gearRollerBackDoor = new GearRollerBackDoor(doorPiston, gearPiston, gearMotors, gearInputSensor);
		gearRollerMotor = new Victor(0);
		
		leftDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		leftDriveBack.changeControlMode(TalonControlMode.Follower);
		leftDriveBack.set(leftDriveFront.getDeviceID());
		
		rightDriveBack.changeControlMode(TalonControlMode.Follower);
		rightDriveBack.set(rightDriveFront.getDeviceID());
		
		gearshift = new TwoSpeedGearshift(false, gearshiftPistons);
		
		// Add actual measurements... :)
		drive = new SRXTankDrive(leftDriveFront, rightDriveBack, (4 * Math.PI)*Length.in, 0, 0, 28.45*Length.in);
		
		shooterMotorRight.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		shooterMotorLeft.changeControlMode(TalonControlMode.Follower);
		shooterMotorLeft.set(shooterMotorRight.getDeviceID());
		shooterMotorLeft.reverseOutput(true);
		
		powerDistPanel = new PowerDistributionPanel();
		compressor = new Compressor();
		
		gearshift.shiftToHigh();
		drive.setGearRatio(HIGH_GEAR_RATIO);
		
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
		lmRight.nameControl(new Button(3), "ClearStickyFaults");
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
		
		lmRight.addButtonDownListener("ClearStickyFaults", powerDistPanel::clearStickyFaults);
		
		lmRight.addButtonDownListener("Shift", () -> {
			gearshift.shiftToOtherGear();
			
			if(gearshift.isInHighGear())
			{
				drive.setGearRatio(HIGH_GEAR_RATIO);
			}
			else
			{
				drive.setGearRatio(LOW_GEAR_RATIO);
			}
		});
		
		lmRight.addButtonDownListener("StartCompressor", compressor::start);
		
		lmRight.addButtonDownListener("StopCompressor", compressor::stop);
		
		lmRight.addButtonDownListener("Shoot", shooter::enableShooter);
		lmRight.addButtonUpListener("Shoot", shooter::disableShooter);
		lmRight.addButtonDownListener("GearRoller", gearRollerBackDoor::activateLoadingMode);
		lmRight.addButtonUpListener("GearRoller", gearRollerBackDoor::deactivateLoadingMode);
	
		lmRight.addListener("IntakePOV", (POVValue value) -> {
			
			switch(value.getDirectionValue())
			{
			//sides or center
			case 3:
			case 7:
			case 0:
				lowerIntakeMotor.set(0);
				break;
			//forwards
			case 1:
			case 2:
			case 8:
				lowerIntakeMotor.set(1);
				break;
			case 4:
			case 5:
			case 6:
				lowerIntakeMotor.set(-1);
				break;
			}
		});
	}

	@Override
	protected void teleopInit() {
		
	}

	@Override
	protected void autonomousInit() {
		
	}

}
