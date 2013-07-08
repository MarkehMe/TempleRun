package de.jan.manager;

import java.util.concurrent.TimeUnit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Util {

	public static ItemStack getItemStackFromInput(Player player, String arg) {
		ItemStack is = null;

		if (!arg.contains(":")) {

			int id;

			if (!isInteger(arg)) {
				player.sendMessage("§7The ID have to be an Number!");
				return is;
			}

			id = Integer.valueOf(arg);

			if (idExists(id) == null) {
				player.sendMessage("§7Arena not found!");
				return is;
			}

			return is = new ItemStack(id);
		} else {

			String[] split = arg.split(":");
			int id;

			if (!isInteger(split[0])) {
				player.sendMessage("§7The ID have to be an Number!");
				return is;
			}

			id = Integer.valueOf(split[0]);

			if (!isInteger(split[1])) {
				player.sendMessage("§7The DATA have to be an Number!");
				return is;
			}

			if (split.length == 2) {
				return is = new ItemStack(id, 1, (byte) Byte.valueOf(split[1]));
			} else if (split.length >= 3) {

				int amount;

				if (!isInteger(split[2])) {
					player.sendMessage("§7The AMOUNT have to be an Number!");
					return is;
				}

				amount = Integer.valueOf(split[2]);
				return is = new ItemStack(id, amount, (byte) Byte.valueOf(split[1]));
			}
		}
		return is;
	}

	public static ItemStack getItemStackFromInput(String arg) {
		ItemStack is = null;

		if (!arg.contains(":")) {

			int id;

			if (!isInteger(arg)) {
				return is;
			}

			id = Integer.valueOf(arg);

			if (idExists(id) == null) {
				return is;
			}

			return is = new ItemStack(id);
		} else {

			String[] split = arg.split(":");
			int id;

			if (!isInteger(split[0])) {
				return is;
			}

			id = Integer.valueOf(split[0]);

			if (!isInteger(split[1])) {
				return is;
			}

			if (split.length == 2) {
				return is = new ItemStack(id, 1, (byte) Byte.valueOf(split[1]));
			} else if (split.length >= 3) {

				int amount;

				if (!isInteger(split[2])) {
					return is;
				}

				amount = Integer.valueOf(split[2]);
				return is = new ItemStack(id, amount, (byte) Byte.valueOf(split[1]));
			}
		}
		return is;
	}

	private static Material idExists(int id) {
		return Material.getMaterial(id);
	}

	public static boolean isInteger(String arg) {
		try {
			Integer.valueOf(arg);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static ItemStack getTempleRunCoin(int amount) {
		ItemStack is = new ItemStack(Material.GOLD_NUGGET, amount);

		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName("§6TempleRun §cCoin");

		is.setItemMeta(meta);
		return is;
	}
	
	public static ItemStack getMagnet(int amount) {
		ItemStack is = new ItemStack(Material.WOOL, amount, (short) 14);

		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName("§6TempleRun §aMagnet");

		is.setItemMeta(meta);
		return is;
	}

	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		String time = "";
		
		// If the time is lower then 10, then add a 0 before the time
		if(minutes < 10) {
			time += String.valueOf("0" + minutes);
		} else {
			time += String.valueOf(minutes);
		}
		
		time += ":";
		
		if(seconds < 10) {
			time += String.valueOf("0" + seconds);
		} else {
			time += String.valueOf(seconds);
		}
		return time;
	}
	
	// Replace all Color codes
	public static String replaceColorCodes(String message) {
		return message.replaceAll("(?i)&([a-n0-9])", "§$1");
	}
}
