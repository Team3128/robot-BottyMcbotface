package org.team3128.autonomous.commands;

import org.team3128.autonomous.PlaceGearAuto;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdPlaceCloseGear extends CommandGroup
{
	public CmdPlaceCloseGear(MainFerb robot){
		addSequential(robot.drive.new CmdMoveForward(104*Length.in, 6000, 0.5));
		addSequential(new PlaceGearAuto(robot, true));
		addSequential(robot.drive.new CmdMoveForward(-36*Length.in, 6000, 0.5));
		addSequential(new PlaceGearAuto (robot, false));
	}
}
