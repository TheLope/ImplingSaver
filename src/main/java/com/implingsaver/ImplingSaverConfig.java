package com.implingsaver;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ImplingSaver")
public interface ImplingSaverConfig extends Config
{

	@ConfigItem(
		keyName = "easy",
		name = "Easy Clues",
		description = "Gourmet and young impling jars will be hidden when easy clue present"
	)
	default boolean easyMode() {
		return true;
	}

	@ConfigItem(
			keyName = "medium",
			name = "Medium Clues",
			description = "Eclectic and essence impling jars will be hidden when medium clue present"
	)
	default boolean mediumMode() {
		return true;
	}

	@ConfigItem(
			keyName = "hard",
			name = "Hard Clues",
			description = "Ninja and magpie impling jars will be hidden when hard clue present"
	)
	default boolean hardMode() {
		return true;
	}

	@ConfigItem(
			keyName = "elite",
			name = "Elite Clues",
			description = "Dragon impling jars will be hidden when elite clue present"
	)
	default boolean eliteMode() {
		return true;
	}
}
