package de.blockbuild.musikbot.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import de.blockbuild.musikbot.Bot;

public class BotConfiguration extends ConfigurationManager {
	public String token, trigger, game, ownerID;
	public Map<String, String> emojis;

	// Avatar
	// Name

	public BotConfiguration(Bot bot) {
		super(new File(bot.getMain().getDataFolder(), "BotConfig.yml"));

		readConfig();
		writeConfig();
	}

	public boolean writeConfig() {
		YamlConfiguration config = new YamlConfiguration();

		config.set("Bot_Token", this.token);
		config.set("Owner_ID", this.ownerID);
		config.set("Command_Trigger", this.trigger);
		config.set("Game", this.game);
		config.createSection("Emojis", this.emojis);

		return this.saveConfig(config);
	}

	public boolean readConfig() {
		try {
			YamlConfiguration config = this.loadConfig();

			this.token = config.getString("Bot_Token", "insert token here");
			this.ownerID = config.getString("Owner_ID", "12345");
			this.trigger = config.getString("Command_Trigger", "!");
			this.game = config.getString("Game", "Ready for playing music. !Play");

			this.emojis = new HashMap<>();
			if (!config.contains("Emojis")) {
				config.createSection("Emojis", this.emojis);
			}
			ConfigurationSection emojiList = config.getConfigurationSection("Emojis");
			this.emojis.put("Success", emojiList.get("Success", "\uD83D\uDE03").toString());
			this.emojis.put("Warning", emojiList.get("Warning", "\uD83D\uDE2E").toString());
			this.emojis.put("Error", emojiList.get("Error", "\uD83D\uDE26").toString());
			return true;
		} catch (Exception e) {
			System.out.println("Couldn't read BotConfiguration!");
			e.printStackTrace();
			return false;
		}
	}
}
