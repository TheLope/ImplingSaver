package com.implingsaver;

import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

public class ImplingSaverOverlay extends OverlayPanel
{
	private final Client client;
	private final ImplingSaverPlugin plugin;
	private final ImplingSaverConfig config;
	private final TooltipManager tooltipManager;

	@Inject
	public ImplingSaverOverlay(Client client, ImplingSaverPlugin plugin, ImplingSaverConfig config, TooltipManager tooltipManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.tooltipManager = tooltipManager;

		setPosition(OverlayPosition.TOOLTIP);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(PRIORITY_HIGHEST);
		setDragTargetable(false);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		renderMouseover();
		return null;
	}

	private void renderMouseover()
	{
		if (!config.showTooltip()) return;

		Menu menu = client.getMenu();
		MenuEntry[] menuEntries = menu.getMenuEntries();
		if (menuEntries.length == 0)
		{
			return;
		}

		MenuEntry entry = menuEntries[menuEntries.length - 1];
		String menuOption = entry.getOption();
		int itemId = entry.getItemId();

		if (plugin.isImplingToSave(itemId) && menuOption.equals("Loot"))
		{
			String tooltipText = plugin.getCause(itemId);

			if (tooltipText != null){
				tooltipManager.add(new Tooltip(tooltipText));
			}
		}
	}
}
