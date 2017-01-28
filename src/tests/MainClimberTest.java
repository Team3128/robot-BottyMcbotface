package tests;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;
import org.team3128.narwhalvision.NarwhalVisionReceiver;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.VictorSP;

public class MainClimberTest extends NarwhalRobot {
	Joystick joystick;
	ListenerManager lm;
	NarwhalVisionReceiver visionRec;
	VictorSP climber;
	ADXRS450_Gyro gyro;
	
	@Override
	protected void constructHardware()
	{
		climber = new VictorSP(0);
		
		joystick = new Joystick(0);
		lm = new ListenerManager(joystick);
		addListenerManager(lm);
		gyro = new ADXRS450_Gyro();
	}
	
	@Override
	protected void setupListeners() {
		lm.nameControl(ControllerExtreme3D.JOYY, "Move");
		lm.addListener("Move", (double value)-> {
			climber.set(RobotMath.clamp(value, -1, 0));
		});
		
	}
	
	@Override
	public void teleopInit()
	{
		
	}
	
	@Override
	public void teleopPeriodic()
	{
		Log.info("Gyro Angle: ", gyro.getAngle() + " degrees");
	}

	@Override
	protected void autonomousInit()
	{
		
	}
	
	@Override
	protected void updateDashboard() {

	}

}
