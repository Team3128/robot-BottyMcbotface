package org.team3128.autonomous;

import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPlaceGearFromRetrievalZone extends CommandGroup
{
	public AutoPlaceGearFromRetrievalZone(MainFerb robot, Alliance alliance)
	{
		//real numbers still need to be added for the move and turn commands
		addSequential(robot.drive.new CmdMoveForward(-75 * Length.in, 4000, 0.5));
		
		// turn left if we are on the blue side, right otherwise
		addSequential(robot.drive.new CmdArcTurn(40, 1750, alliance == Alliance.Blue ? Direction.LEFT : Direction.RIGHT));
		addSequential(robot.drive.new CmdMoveForward(-28 * Length.in, 3000, 0.50));
		addSequential(robot.gearShovel.new CmdDepositGear(robot));
	}
}
