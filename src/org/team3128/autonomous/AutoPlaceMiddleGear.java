package org.team3128.autonomous;

import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;
import org.team3128.mechanisms.GearRollerBackDoor.CmdSetDepositingMode;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPlaceMiddleGear extends CommandGroup
{
	public AutoPlaceMiddleGear(MainFerb robot){
		addSequential(robot.drive.new CmdMoveForward(-104 * Length.in, 6000, 0.5));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(true));
		
		addSequential(robot.drive.new CmdMoveForward(24 * Length.in, 6000, 0.5));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(false));
	}
}
