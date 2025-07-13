package com.implingsaver;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import static net.runelite.api.ItemID.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

@Slf4j
@PluginDescriptor(
        name = "ImplingSaver",
		description = "Blocks looting while corresponding clue is banked",
		tags = {"imp", "impling", "clue"}
)
public class ImplingSaverPlugin extends Plugin
{
    public ArrayList<Integer> implingJarIds = new ArrayList<Integer>()
	{{
        add(BABY_IMPLING_JAR);
        add(YOUNG_IMPLING_JAR);
        add(GOURMET_IMPLING_JAR);
        add(EARTH_IMPLING_JAR);
        add(ESSENCE_IMPLING_JAR);
        add(ECLECTIC_IMPLING_JAR);
        add(NATURE_IMPLING_JAR);
        add(MAGPIE_IMPLING_JAR);
        add(NINJA_IMPLING_JAR);
        add(CRYSTAL_IMPLING_JAR);
        add(DRAGON_IMPLING_JAR);
    }};

	public ArrayList<Integer> beginnerImplingJarIds = new ArrayList<Integer>()
	{{
		add(BABY_IMPLING_JAR);
		add(YOUNG_IMPLING_JAR);
	}};

	public ArrayList<Integer> easyImplingJarIds = new ArrayList<Integer>()
	{{
		add(BABY_IMPLING_JAR);
		add(YOUNG_IMPLING_JAR);
		add(GOURMET_IMPLING_JAR);
	}};

	public ArrayList<Integer> mediumImplingJarIds = new ArrayList<Integer>()
	{{
		add(EARTH_IMPLING_JAR);
		add(ESSENCE_IMPLING_JAR);
		add(ECLECTIC_IMPLING_JAR);
	}};

	public ArrayList<Integer> hardImplingJarIds = new ArrayList<Integer>()
	{{
		add(NATURE_IMPLING_JAR);
		add(MAGPIE_IMPLING_JAR);
		add(NINJA_IMPLING_JAR);
	}};

	public ArrayList<Integer> eliteImplingJarIds = new ArrayList<Integer>()
	{{
		add(CRYSTAL_IMPLING_JAR);
		add(DRAGON_IMPLING_JAR);
	}};

	public static final String BEGINNER_ITEM_NAME = "Clue scroll (beginner)";
	public static final String EASY_ITEM_NAME = "Clue scroll (easy)";
	public static final String MEDIUM_ITEM_NAME = "Clue scroll (medium)";
	public static final String MEDIUM_CHALLENGE_ITEM_NAME = "Challenge scroll (medium)";
	public static final String HARD_ITEM_NAME = "Clue scroll (hard)";
	public static final String HARD_CHALLENGE_ITEM_NAME = "Challenge scroll (hard)";
	public static final String ELITE_ITEM_NAME = "Clue scroll (elite)";
	public static final String ELITE_CHALLENGE_ITEM_NAME = "Challenge scroll (elite)";

    @Inject
    private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	private InfoBox infoBox = null;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	PluginManager pluginManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ImplingSaverOverlay infoOverlay;

	@Inject
	private ImplingSaverConfig config;

    private boolean beginnerInBank = false;
    private boolean easyInBank = false;
    private boolean mediumInBank = false;
    private boolean hardInBank = false;
    private boolean eliteInBank = false;
	private boolean loggingIn;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(infoOverlay);
		clientThread.invoke(this::loadFromConfig);
		loggingIn = true;
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(infoOverlay);
		removeInfoBox();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGING_IN)
		{
			loggingIn = true;
		}
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged e)
	{
		clientThread.invoke(this::loadFromConfig);
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.BANK.getId())
		{
			ItemContainer bankContainer = client.getItemContainer(InventoryID.BANK);

			if (bankContainer != null)
			{
				checkContainer(bankContainer);
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		// Consume Impling Loot events
		if (event.isItemOp() && implingJarIds.contains(event.getItemId()) && event.getMenuOption().equals("Loot"))
		{
			saveImpling(event);
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		handleInfoBox();

		if (loggingIn)
		{
			loggingIn = false;
			notifyPluginInstall();
		}
	}

	@Provides
	ImplingSaverConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ImplingSaverConfig.class);
	}

	private void checkContainer(ItemContainer container)
	{
		String[] cluesToFind = {
			BEGINNER_ITEM_NAME,
			EASY_ITEM_NAME,
			MEDIUM_ITEM_NAME,
			MEDIUM_CHALLENGE_ITEM_NAME,
			HARD_ITEM_NAME,
			HARD_CHALLENGE_ITEM_NAME,
			ELITE_ITEM_NAME,
			ELITE_CHALLENGE_ITEM_NAME
		};

		List<String> foundClues = new ArrayList<>();

		// Check bank for cluesToFind
		for (Item item : container.getItems())
		{
			for (String clueName : cluesToFind)
			{
				String itemName = getItemName(item.getId());
				if (itemName.equals(clueName))
				{
					foundClues.add(clueName);
					break;
				}
			}
		}

		// Set state of each clue based on what was found
		setBeginnerInBank(!Collections.disjoint(Collections.singletonList(BEGINNER_ITEM_NAME), foundClues));
		setEasyInBank(!Collections.disjoint(Collections.singletonList(EASY_ITEM_NAME), foundClues));
		setMediumInBank(!Collections.disjoint(Arrays.asList(MEDIUM_ITEM_NAME, MEDIUM_CHALLENGE_ITEM_NAME), foundClues));
		setHardInBank(!Collections.disjoint(Arrays.asList(HARD_ITEM_NAME, HARD_CHALLENGE_ITEM_NAME), foundClues));
		setEliteInBank(!Collections.disjoint(Arrays.asList(ELITE_ITEM_NAME, ELITE_CHALLENGE_ITEM_NAME), foundClues));
	}

	public String getItemName(Integer itemId)
	{
		return itemManager.getItemComposition(itemId).getName();
	}

	public String getCause(Integer itemId)
	{
		if (!(beginnerInBank || easyInBank || mediumInBank || hardInBank || eliteInBank))
		{
			return null;
		}

		StringBuilder savingCause = new StringBuilder()
			.append(ColorUtil.wrapWithColorTag("Impling Saver: ", Color.YELLOW))
			.append(ColorUtil.wrapWithColorTag("active", Color.GREEN))
			.append("<br>")
			.append(ColorUtil.wrapWithColorTag("Cause: ", Color.YELLOW));
		if (config.beginnerMode() && beginnerInBank && beginnerImplingJarIds.contains(itemId))
		{
			savingCause.append(ColorUtil.wrapWithColorTag("Beginner", Color.RED));
		}
		else if (config.easyMode() && easyInBank && easyImplingJarIds.contains(itemId))
		{
			savingCause.append(ColorUtil.wrapWithColorTag("Easy", Color.RED));
		}
		else if (config.mediumMode() && mediumInBank && mediumImplingJarIds.contains(itemId))
		{
			savingCause.append(ColorUtil.wrapWithColorTag("Medium", Color.RED));
		}
		else if (config.hardMode() && hardInBank && hardImplingJarIds.contains(itemId))
		{
			savingCause.append(ColorUtil.wrapWithColorTag("Hard", Color.RED));
		}
		else if (config.eliteMode() && eliteInBank && eliteImplingJarIds.contains(itemId))
		{
			savingCause.append(ColorUtil.wrapWithColorTag("Elite", Color.RED));
		}
		savingCause.append(" clue in bank<br>");
		return savingCause.toString();
	}

	private void handleInfoBox()
	{
		boolean isShowing = infoBox != null;
		boolean shouldShow = config.showInfobox() && (
				beginnerInBank || easyInBank || mediumInBank || hardInBank || eliteInBank
		);

		if (isShowing && !shouldShow)
		{
			removeInfoBox();
		}
		else if (shouldShow)
		{
			if (!isShowing)
			{
				infoBox = new InfoBox(itemManager.getImage(ECLECTIC_IMPLING_JAR), this)
				{
					@Override
					public String getText()
					{
						return "";
					}

					@Override
					public Color getTextColor()
					{
						return null;
					}
				};
			}

			StringBuilder tooltip = new StringBuilder();
			for (Integer implingJarId : implingJarIds)
			{
				if (isImplingToSave(implingJarId)){
					tooltip.append(getCause(implingJarId));
				}
			}

			removeDuplicateLines(tooltip);
			infoBox.setTooltip(tooltip.toString());

			if (!isShowing)
			{
				infoBoxManager.addInfoBox(infoBox);
			}
		}
	}

	private static void removeDuplicateLines(StringBuilder sb)
	{
		Set<String> lines = new HashSet<>();
		StringBuilder result = new StringBuilder();

		String[] linesArray = sb.toString().split("<br>");

		for (String line : linesArray)
		{
			if (!lines.contains(line))
			{
				result.append(line).append("<br>");
				lines.add(line);
			}
		}

		sb.setLength(0); // Clear the original StringBuilder
		sb.append(result);
	}

	public boolean isImplingToSave(Integer itemId)
	{
		return (beginnerImplingJarIds.contains(itemId) && config.beginnerMode() && beginnerInBank)
			|| (easyImplingJarIds.contains(itemId) && config.easyMode() && easyInBank)
			|| (mediumImplingJarIds.contains(itemId) && config.mediumMode() && mediumInBank)
			|| (hardImplingJarIds.contains(itemId) && config.hardMode() && hardInBank)
			|| (eliteImplingJarIds.contains(itemId) && config.eliteMode() && eliteInBank);
	}

	private void loadFromConfig()
	{
		Boolean loadBeginnerInBank = configManager.getRSProfileConfiguration(ImplingSaverConfig.CONFIG_GROUP, ImplingSaverConfig.BEGINNER_BANKED, Boolean.class);
		Boolean loadEasyInBank = configManager.getRSProfileConfiguration(ImplingSaverConfig.CONFIG_GROUP, ImplingSaverConfig.EASY_BANKED, Boolean.class);
		Boolean loadMediumInBank = configManager.getRSProfileConfiguration(ImplingSaverConfig.CONFIG_GROUP, ImplingSaverConfig.MEDIUM_BANKED, Boolean.class);
		Boolean loadHardInBank = configManager.getRSProfileConfiguration(ImplingSaverConfig.CONFIG_GROUP, ImplingSaverConfig.HARD_BANKED, Boolean.class);
		Boolean loadEliteInBank = configManager.getRSProfileConfiguration(ImplingSaverConfig.CONFIG_GROUP, ImplingSaverConfig.ELITE_BANKED, Boolean.class);

		if (loadBeginnerInBank != null) beginnerInBank = loadBeginnerInBank;
		if (loadEasyInBank != null) easyInBank = loadEasyInBank;
		if (loadMediumInBank != null) mediumInBank = loadMediumInBank;
		if (loadHardInBank != null) hardInBank = loadHardInBank;
		if (loadEliteInBank != null) eliteInBank = loadEliteInBank;
	}

	private void removeInfoBox()
	{
		if (infoBox != null)
		{
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
		}
	}

	private void saveImpling(MenuOptionClicked event)
	{
		if (isImplingToSave(event.getItemId()))
		{
			event.consume();
			if (config.showChatMessage())
			{
				String chatMessage = getCause(event.getItemId()).replace("<br>", " ");
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", chatMessage, "");
			}
		}
	}

	private void setBeginnerInBank(Boolean bool)
	{
		beginnerInBank = bool;
		manageRSProfileConfiguration(ImplingSaverConfig.BEGINNER_BANKED, bool);
	}

	private void setEasyInBank(Boolean bool)
	{
		easyInBank = bool;
		manageRSProfileConfiguration(ImplingSaverConfig.EASY_BANKED, bool);
	}
	private void setMediumInBank(Boolean bool)
	{
		mediumInBank = bool;
		manageRSProfileConfiguration(ImplingSaverConfig.MEDIUM_BANKED, bool);
	}

	private void setHardInBank(Boolean bool)
	{
		hardInBank = bool;
		manageRSProfileConfiguration(ImplingSaverConfig.HARD_BANKED, bool);
	}

	private void setEliteInBank(Boolean bool)
	{
		eliteInBank = bool;
		manageRSProfileConfiguration(ImplingSaverConfig.ELITE_BANKED, bool);
	}

	private void manageRSProfileConfiguration(String key, Boolean bool)
	{
		configManager.setRSProfileConfiguration(ImplingSaverConfig.CONFIG_GROUP, key, bool);
	}

	private void notifyPluginInstall()
	{
		if (pluginManager.getPlugins().stream().noneMatch(plugin -> plugin.getName().equals("Clue Saver")))
		{
			sendChatConsoleMessage("ImplingSaver functionality has been integrated into Clue Saver. " +
				"Please install Clue Saver to take advantage of the latest features.");
		}
	}

	private void sendChatConsoleMessage(String chatMessage)
	{
		final String message = new ChatMessageBuilder()
			.append(ChatColorType.HIGHLIGHT)
			.append(chatMessage)
			.build();

		chatMessageManager.queue(
			QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(message)
				.build());
	}
}
