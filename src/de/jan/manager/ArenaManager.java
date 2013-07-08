package de.jan.manager;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import de.jan.TempleRun;
import de.jan.arena.Arena;

public class ArenaManager {

	private static FileConfiguration database;

	public static ArrayList<Arena> arenas = new ArrayList<Arena>();

	public ArenaManager(FileConfiguration database) {
		ArenaManager.database = database;
	}

	public void loadArenas() {
		for (String s : database.getKeys(false)) {
			if (s == null) {
				TempleRun.debug("No Arenas found.");
				return;
			}

			String arenaname = s;

			String world = database.getString(arenaname + ".Spawn.World");
			double x = database.getDouble(arenaname + ".Spawn.X");
			double y = database.getDouble(arenaname + ".Spawn.Y");
			double z = database.getDouble(arenaname + ".Spawn.Z");
			float yaw = (float) database.getDouble(arenaname + ".Spawn.Yaw");
			float pitch = (float) database.getDouble(arenaname + ".Spawn.Pitch");

			ArrayList<String> winitems = (ArrayList<String>) database.getStringList(arenaname + ".WinItems");

			// New Arraylist with all Win Items
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			for (String st : winitems) {
				ItemStack is = Util.getItemStackFromInput(st);

				if (is != null)
					items.add(is);
			}

			Location spawn = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			Arena arena = new Arena(arenaname, spawn, items);

			arenas.add(arena);
		}
		TempleRun.debug("All Arenas loaded.");
	}

	/**
	 * Creating a new Arena. Saving to disk and creating an new Object
	 * @param arenaname
	 * @param loc
	 */
	public void createArena(String arenaname, Location loc) {

		arenaname = arenaname.toLowerCase();

		database.set(arenaname + ".Spawn.World", loc.getWorld().getName());
		database.set(arenaname + ".Spawn.X", loc.getX());
		database.set(arenaname + ".Spawn.Y", loc.getY());
		database.set(arenaname + ".Spawn.Z", loc.getZ());
		database.set(arenaname + ".Spawn.Yaw", loc.getYaw());
		database.set(arenaname + ".Spawn.Pitch", loc.getPitch());
		database.set(arenaname + ".WinItems", new ArrayList<String>());

		arenas.add(new Arena(arenaname, loc, new ArrayList<ItemStack>()));

		// Saving. To be on the save side :)
		save();
	}

	public Arena getArena(String arenaname) {
		for (Arena arena : arenas) {
			if (arena.getName().equalsIgnoreCase(arenaname))
				return arena;
		}
		return null;
	}

	/**
	 * Saving the Database
	 * If an Error occurred, then print it in the Console
	 */
	public static void save() {
		try {
			database.save("plugins/TempleRun/database.yml");
		} catch (Exception e) {
			TempleRun.debug("An Error occurred while saving the Database!");
		}
	}
}
