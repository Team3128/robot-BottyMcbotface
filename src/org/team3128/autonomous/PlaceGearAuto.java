package org.team3128.autonomous;

import org.team3128.common.autonomous.primitives.CmdDelay;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class PlaceGearAuto extends CommandGroup
{
	boolean deposit;
	public PlaceGearAuto(MainFerb robot, boolean deposit)
	{
		addSequential(robot.gearRollerBackDoor.new CmdDepositGear(deposit));
		
	}
}
