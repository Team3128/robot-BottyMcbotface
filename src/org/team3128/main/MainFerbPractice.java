package org.team3128.main;

import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.motor.MotorGroup;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.VictorSP;

public class MainFerbPractice extends MainFerb {
	
	public MainFerbPractice() {
		super();
	}
	
	@Override
	public void constructHardware() 
	{
		gearMotors = new MotorGroup(new VictorSP(2));
		
		floorIntakeMotor = new VictorSP(1);
		
		climberMotor = new MotorGroup(new VictorSP(0));
		climberMotor.invert();
		
		gearPiston = new Piston(3, 4);
		doorPiston = new Piston(2, 5);
		
		gearshiftPistons = new Piston(1, 6);
		gearshiftPistons.invertPiston();
		
		gearInputSensor = new DigitalInput(5);
		
		// visionAimServo = new Servo(9);
						
		super.constructHardware();
		compressor.stop();

		leftDriveFront.reverseSensor(true);
		leftDriveFront.reverseOutput(true);
		rightDriveFront.reverseSensor(false);
		rightDriveFront.reverseOutput(false);
		
		leftDriveFront.configNominalOutputVoltage(1.5, -1.5);
		leftDriveFront.setAllowableClosedLoopErr(32);
		
		rightDriveFront.configNominalOutputVoltage(1.5, -1.5);
		rightDriveFront.setAllowableClosedLoopErr(32);
		
		drive.setRightSpeedScalar(1);
		
		//CameraServer cameraServer = CameraServer.getInstance();
		
//		UsbCamera camera = cameraServer.startAutomaticCapture(0);
//   	camera.setResolution(120, 80);
//		camera.setFPS(20);
		
		//cameraServer.startAutomaticCapture(0).setFPS(20);
		
	}
}
