package org.team3128.main;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.drive.SRXTankDrive;
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
import org.team3128.mechanisms.GearRollerBackDoor;
import org.team3128.mechanisms.Shooter;
import org.team3128.narwhalvision.NarwhalVisionReceiver;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
	//public ListenerManager lmLeft;
	
	public Joystick rightJoystick;
	//public Joystick leftJoystick;
	
	public TwoSpeedGearshift gearshift;
	public Piston gearshiftPistons;
	
	public Piston doorPiston, gearPiston;

	public PowerDistributionPanel powerDistPanel;
	public Compressor compressor;
	
	public ADXRS450_Gyro gyro;
	
	public NarwhalVisionReceiver visionReceiver;
	
	public Servo visionAimServo;
	
	// preset aingles for aiming the phone
	public final static double VISION_SERVO_GEAR_ANGLE = 0;
	public final static double VISION_SERVO_SHOOTER_ANGLE = .5;
	
	@Override
	protected void constructHardware() {
		gearMotors = new MotorGroup();
		gearMotors.addMotor(gearRollerMotor);
		
		intakeMotors = new MotorGroup();
		intakeMotors.addMotor(lowerIntakeMotor);
		intakeMotors.addMotor(shooterIntakeMotor);
		
		gearRollerBackDoor = new GearRollerBackDoor(doorPiston, gearPiston, gearMotors, gearInputSensor);
		
		leftDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		rightDriveFront.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		leftDriveBack.changeControlMode(TalonControlMode.Follower);
		leftDriveBack.set(leftDriveFront.getDeviceID());
		
		rightDriveBack.changeControlMode(TalonControlMode.Follower);
		rightDriveBack.set(rightDriveFront.getDeviceID());
		
		gearshift = new TwoSpeedGearshift(false, gearshiftPistons);
		
		
		shooterMotorRight.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
		
		shooterMotorLeft.changeControlMode(TalonControlMode.Follower);
		shooterMotorLeft.set(shooterMotorRight.getDeviceID());
		shooterMotorLeft.reverseOutput(true);
		
		shooter = new Shooter(shooterMotorRight, intakeMotors);

		
		powerDistPanel = new PowerDistributionPanel();
		compressor = new Compressor();
		
		gearshift.shiftToHigh();
		drive.setGearRatio(HIGH_GEAR_RATIO);
		
		rightJoystick = new Joystick(0);
		lmRight = new ListenerManager(rightJoystick);
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();
		
		visionReceiver = new NarwhalVisionReceiver();
		visionAimServo.setAngle(VISION_SERVO_GEAR_ANGLE);
		
		//lmLeft = new ListenerManager(leftJoystick);
		//addListenerManager(lmLeft);
		
		Log.info("[MainFerb]", "Activating Ferb");
        Log.info("[MainFerb]", "Hey! Where's Perry?");
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
		lmRight.nameControl(new Button(11),"LoadGear");
		lmRight.nameControl(new Button(12),"DepositGear");
		
		lmRight.addMultiListener(() -> {
			drive.arcadeDrive(lmRight.getAxis("MoveTurn"),
					lmRight.getAxis("MoveForwards"),
					lmRight.getButton("FullSpeed"));
		
		}, "MoveTurn", "MoveForwards", "Throttle", "FullSpeed");
		
		lmRight.addButtonDownListener("ClearStickyFaults", powerDistPanel::clearStickyFaults);
		
		lmRight.addButtonDownListener("GearShift", () -> {
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
		
		lmRight.addButtonDownListener("LoadGear", gearRollerBackDoor::activateLoadingMode);
		lmRight.addButtonUpListener("LoadGear", gearRollerBackDoor::deactivateLoadingMode);
		
		lmRight.addButtonDownListener("DepositGear", gearRollerBackDoor::activateDepositingMode);
		lmRight.addButtonUpListener("DepositGear", gearRollerBackDoor::deactivateDepositingMode);
		
		lmRight.addButtonDownListener("Climb", () -> 
		{
			lifterMotor.set(1);	
		});
		lmRight.addButtonUpListener("Climb", ()->
		{
			lifterMotor.set(0);
		});
	
		lmRight.addListener("IntakePOV", (POVValue value) -> {
			switch(value.getDirectionValue())
			{
			// Sides or Center
			case 3:
			case 7:
			case 0:
				lowerIntakeMotor.set(0);
				break;
			// Forwards
			case 1:
			case 2:
			case 8:
				lowerIntakeMotor.set(1);
				break;
			// Backwards
			case 4:
			case 5:
			case 6:
				lowerIntakeMotor.set(-1);
				break;
			}
		});
		
		addListenerManager(lmRight);

	}

	@Override
	protected void teleopInit() {
		
	}

	@Override
	protected void autonomousInit() {
		
	}
	
	@Override
	protected void updateDashboard() {
		SmartDashboard.putNumber("Gyro Angle", gyro.getAngle());
	}

}
