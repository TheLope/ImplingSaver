package com.implingsaver;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ImplingSaverTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ImplingSaverPlugin.class);
		RuneLite.main(args);
	}
}