package TempleRun.Util;

import org.bukkit.configuration.file.FileConfiguration;

import TempleRun.TempleRun;

public class InfoUtil {

	private TempleRun main;

	public InfoUtil(TempleRun main) {
		this.main = main;
	}

	public void addTopPlayer(String name, int time, int coins) {
		main.cload.load();
		if (hasScore(name) == false) {
			main.cload.getConfig().set("Players." + name + ".Time", time);
			main.cload.getConfig().set("Players." + name + ".Coins", coins);
			main.cload.save();
			return;
		} else {
			int oldtime = main.cload.getConfig().getInt("Players." + name + ".Time");
			int oldcoins = main.cload.getConfig().getInt("Players." + name + ".Coins");

			if (oldtime > time) {
				main.cload.getConfig().set("Players." + name + ".Time", time);
			}
			if (oldcoins < coins) {
				main.cload.getConfig().set("Players." + name + ".Coins", coins);
			}
			main.cload.save();
		}
	}

	public int getBestTime(String name) {
		FileConfiguration topplayers = main.cload.getConfig();
		if (hasScore(name)) {
			return topplayers.getInt("Players." + name + ".Time");
		}
		return 0;
	}
	
	public int getBestCoins(String name) {
		FileConfiguration topplayers = main.cload.getConfig();
		if (hasScore(name)) {
			return topplayers.getInt("Players." + name + ".Coins");
		}
		return 0;
	}

	public boolean hasScore(String name) {
		if (main.cload.getConfig().getString("Players." + name) != null)
			return true;
		return false;
	}
	
	private String replaceColor(String message) {
		return message.replaceAll("(?i)&([a-n0-9])", "§$1");
	}
	
	public String replace(String msg, String coins, String time) {
		return replaceColor(msg).replaceAll("%coins", coins).replaceAll("%time", time).replaceAll("%prefix", replaceColor(main.getConfig().getString("Messages.prefix")));
	}

}
