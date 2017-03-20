package org.team3128.autonomous;

import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPlaceMiddleGear extends CommandGroup
{
	
	public AutoPlaceMiddleGear(MainFerb robot){
		addSequential(robot.drive.new CmdMoveForward(-79 * Length.in, 3500, 0.5));
		addSequential(robot.gearShovel.new CmdDepositGear(robot));
		
//		addSequential(robot.drive.new CmdArcTurn(110, 3000, Direction.RIGHT));
//		addSequential(robot.drive.new CmdMoveForward(1.5 * Length.m, 2000, 0.5));
//		addSequential(robot.drive.new CmdArcTurn(100, 3000, Direction.RIGHT));
//		addSequential(robot.drive.new CmdMoveForward(2.5 * Length.m, 5000, 0.5));
	}
}
