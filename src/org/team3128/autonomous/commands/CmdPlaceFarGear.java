package org.team3128.autonomous.commands;

import org.team3128.autonomous.PlaceGearAuto;
import org.team3128.common.drive.SRXTankDrive.CmdMoveForward;
import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class CmdPlaceFarGear extends CommandGroup
{
	public CmdPlaceFarGear(MainFerb robot, Direction dir){
		//real numbers still need to be added for the move and turn commands
		addSequential(robot.drive.new CmdMoveForward(150*Length.in, 6000, 0.5));
		addSequential(robot.drive.new CmdInPlaceTurn(135, 1000, dir));
		addSequential(robot.drive.new CmdMoveForward(50*Length.in, 3000, 0.50));
		addSequential(new PlaceGearAuto(robot, true));
		addSequential(robot.drive.new CmdMoveForward(-36*Length.in, 6000, 0.5));
		addSequential(new PlaceGearAuto (robot, false));
	}
}
