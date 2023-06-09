package com.implingsaver;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ImplingSaver")
public interface ImplingSaverConfig extends Config {
    @ConfigItem(
            keyName = "beginner",
            name = "Beginner Clues",
            description = "Young and baby impling jars will be hidden when beginner clue present"
    )
    default boolean beginnerMode() {
        return true;
    }

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
            description = "Ninja, magpie, and nature impling jars will be hidden when hard clue present"
    )
    default boolean hardMode() {
        return true;
    }

    @ConfigItem(
            keyName = "elite",
            name = "Elite Clues",
            description = "Dragon and crystal impling jars will be hidden when elite clue present"
    )
    default boolean eliteMode() {
        return true;
    }

    @ConfigItem(
            keyName = "jarCooldown",
            name = "Add open cooldown",
            description = "Add a cooldown to opening Impling Jars so you can spam click."
    )
    default boolean jarCooldown() {
        return true;
    }
}
