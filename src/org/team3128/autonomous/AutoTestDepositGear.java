package org.team3128.autonomous;

import org.team3128.main.MainFerb;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoTestDepositGear extends CommandGroup
{
	public AutoTestDepositGear(MainFerb robot){
		addSequential(robot.gearShovel.new CmdDepositGear(robot));
	}
}
