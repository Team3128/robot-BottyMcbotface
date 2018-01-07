package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;
import org.team3128.common.util.units.Length;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.VictorSP;

public class MainFerbCompetition extends MainFerb {
	
	public MainFerbCompetition() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{
		climberMotor = new MotorGroup(new VictorSP(0), new VictorSP(1));
		climberMotor.invert();
		

		wheelDiameter = 3.68 * Length.in;
		
		gearshiftPistons = new Piston(0, 7);

		lightSignal = new DigitalOutput(0);
		
		gearRoller = new MotorGroup(new VictorSP(2));
		
		//gearMotors = new MotorGroup(new VictorSP(2));
		//gearMotors.invert();
				
		//floorIntakeMotor = new VictorSP(1);
		//gearPiston = new Piston(2, 5);
		//doorPiston = new Piston(1, 6);
		//gearInputSensor = new DigitalInput(5);
		//visionAimServo = new Servo(9);
		
		super.constructHardware();
		
		
		drive.setReversedAutonomous(false);
	
		
		CameraServer cameraServer = CameraServer.getInstance();
		UsbCamera camera = cameraServer.startAutomaticCapture(0);
		camera.setFPS(15);
		camera.setResolution(240, 135);
	}
	
	
	
	@Override
	protected void teleopInit()
	{
		super.teleopInit();
		
		// inversions for teleop
		
		rightDriveFront.setInverted(false);
		rightDriveFront.setSensorPhase(false);
		rightDriveBack.setInverted(false);
		
		leftDriveFront.setInverted(true);
		leftDriveFront.setSensorPhase(false);
		leftDriveBack.setInverted(false);
	}

	@Override
	protected void autonomousInit()
	{
		super.autonomousInit();
		
		// inversions for autonomous
		
		rightDriveFront.setInverted(false);
		rightDriveFront.setSensorPhase(false);
		rightDriveBack.setInverted(false);
		
		leftDriveFront.setInverted(false);
		leftDriveFront.setSensorPhase(true);
		leftDriveBack.setInverted(true);
	}

	@Override
	protected void disabledPeriodic()
	{
		super.disabledPeriodic();
		
		/*
		armPivotMotor.configNominalOutputForward(.75, Constants.CAN_TIMEOUT);
		armPivotMotor.configNominalOutputReverse(.75, Constants.CAN_TIMEOUT);
		armPivotMotor.setNominalClosedLoopVoltage(12);
		*/
		
		drive.setRightSpeedScalar(1.0);
		drive.setLeftSpeedScalar(1.0);
	}
}
