package com.github.maxopoly.MemeMana.command;

import vg.civcraft.mc.civmodcore.command.CommandHandler;
public class MemeManaCommandHandler extends CommandHandler{

	@Override
	public void registerCommands() {
		addCommands(new CmdManaInspect("CmdManaInspect"));
		addCommands(new CmdManaIncrease("CmdManaIncrease"));
		addCommands(new CmdManaReset("CmdManaReset"));
		addCommands(new CmdManaShow("CmdManaShow"));
		addCommands(new CmdManaRefill("CmdManaRefill"));
		addCommands(new CmdManaTransfer("CmdManaTransfer"));
	}
}
