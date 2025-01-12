/*
 * Copyright (c) 2024, TheLope <https://github.com/TheLope>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.implingsaver;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("ImplingSaver")
public interface ImplingSaverConfig extends Config
{
	String CONFIG_GROUP = "ImplingSaver";
	String BEGINNER_BANKED = "beginnerBanked";
	String EASY_BANKED = "easyBanked";
	String MEDIUM_BANKED = "mediumBanked";
	String HARD_BANKED = "hardBanked";
	String ELITE_BANKED = "eliteBanked";

    @ConfigItem(
		keyName = "beginner",
		name = "Beginner Clues",
		description = "Looting baby and young impling jars is blocked when beginner clue in bank",
		position = 0
    )
    default boolean beginnerMode()
	{
        return true;
    }

    @ConfigItem(
		keyName = "easy",
		name = "Easy Clues",
		description = "Looting baby, young, and gourmet impling jars is blocked when easy clue in bank",
		position = 1
    )
    default boolean easyMode()
	{
        return true;
    }

    @ConfigItem(
		keyName = "medium",
		name = "Medium Clues",
		description = "Looting earth, eclectic, and essence impling jars is blocked when medium clue in bank",
		position = 2
    )
    default boolean mediumMode()
	{
        return true;
    }

    @ConfigItem(
		keyName = "hard",
		name = "Hard Clues",
		description = "Looting nature, magpie, and ninja impling jars is blocked when hard clue in bank",
		position = 3
    )
    default boolean hardMode()
	{
        return true;
    }

    @ConfigItem(
		keyName = "elite",
		name = "Elite Clues",
		description = "Looting crystal and dragon impling jars is blocked when elite clue in bank",
		position = 4
    )
    default boolean eliteMode()
	{
        return true;
    }

	@ConfigSection(name = "Overlays", description = "Options that effect overlays", position = 5)
	String overlaysSection = "Overlays";

	@ConfigItem(
		keyName = "showChatMessage",
		name = "Show chat message",
		description = "Show chat message indicating when implings are being saved",
		section = overlaysSection,
		position = 0
	)
	default boolean showChatMessage()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showInfobox",
		name = "Show infobox",
		description = "Show infobox indicating when implings are being saved",
		section = overlaysSection,
		position = 1
	)
	default boolean showInfobox()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showTooltip",
		name = "Show tooltip",
		description = "Show tooltip on implings \"Loot\" hover when implings are being saved",
		section = overlaysSection,
		position = 2
	)
	default boolean showTooltip()
	{
		return true;
	}
}
