package org.team3128.autonomous;

import org.team3128.common.util.units.Length;
import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoTestDepositGear extends CommandGroup
{
	public AutoTestDepositGear(MainFerb robot){
		
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(true));
		
		addSequential(robot.drive.new CmdMoveForward(4 * Length.in, 6000, 0.5));
		addSequential(robot.gearRollerBackDoor.new CmdSetDepositingMode(false));
	}
}
