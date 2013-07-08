package de.jan.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.jan.TempleRun;
import de.jan.manager.ArenaManager;
import de.jan.manager.PlayerManager;
import de.jan.trplayer.TRPlayer;

public class Arena {

	private String arenaname;
	private Location spawn;
	private ArrayList<ItemStack> winitems;

	public Arena(String arenaname, Location spawn, ArrayList<ItemStack> winitems) {
		this.arenaname = arenaname;
		this.spawn = spawn;
		this.winitems = winitems;
	}

	/**
	 * Getting the Name of the Arena
	 * @return the name of the Arena
	 */
	public String getName() {
		return arenaname;
	}

	/**
	 * Set the Arenaname
	 * @param arenaname
	 */
	public void setName(String arenaname) {

		FileConfiguration database = TempleRun.database;
		arenaname = arenaname.toLowerCase();

		for (String s : database.getKeys(false)) {
			if (s.equalsIgnoreCase(this.arenaname)) {

				// Getting the Old Datas
				String world = database.getString(this.arenaname + ".Spawn.World");
				double x = database.getDouble(this.arenaname + ".Spawn.X");
				double y = database.getDouble(this.arenaname + ".Spawn.Y");
				double z = database.getDouble(this.arenaname + ".Spawn.Z");
				float yaw = (float) database.getDouble(this.arenaname + ".Spawn.Yaw");
				float pitch = (float) database.getDouble(this.arenaname + ".Spawn.Pitch");

				ArrayList<String> winitems = (ArrayList<String>) database.getStringList(this.arenaname + ".WinItems");

				// Delete the old Arena
				database.set(this.arenaname, null);

				// Setting the old arena datas
				database.set(arenaname + ".Spawn.World", world);
				database.set(arenaname + ".Spawn.X", x);
				database.set(arenaname + ".Spawn.Y", y);
				database.set(arenaname + ".Spawn.Z", z);
				database.set(arenaname + ".Spawn.Yaw", yaw);
				database.set(arenaname + ".Spawn.Pitch", pitch);
				database.set(arenaname + ".Spawn.WinItems", winitems);

				ArenaManager.save();
			}
		}

		this.arenaname = arenaname;
	}

	/**
	 * Getting the Spawn of the Arena
	 * @return the Spawn of the Arena
	 */
	public Location getSpawn() {
		return spawn;
	}

	/**
	 * Set the Spawn of the Arena
	 * @param spawn
	 */
	public void setLocation(Location spawn) {
		this.spawn = spawn;

		FileConfiguration database = TempleRun.database;

		database.set(arenaname + ".Spawn.World", spawn.getWorld().getName());
		database.set(arenaname + ".Spawn.X", spawn.getX());
		database.set(arenaname + ".Spawn.Y", spawn.getY());
		database.set(arenaname + ".Spawn.Z", spawn.getZ());
		database.set(arenaname + ".Spawn.Yaw", spawn.getYaw());
		database.set(arenaname + ".Spawn.Pitch", spawn.getPitch());

		// Saving the Database to be on the Save side.
		ArenaManager.save();
	}

	/**
	 * Gettings the Winitems per Arena
	 * @return the ItemStack Array with all Winitems
	 */
	public ArrayList<ItemStack> getWinItems() {
		return winitems;
	}

	public void addWinItem(ItemStack item) {
		winitems.add(item);

		FileConfiguration database = TempleRun.database;

		ArrayList<String> items = (ArrayList<String>) database.getStringList(arenaname + ".WinItems");

		// When the itemsList is null
		if (items == null)
			items = new ArrayList<String>();

		items.add(item.getTypeId() + ":" + item.getData().getData() + ":" + item.getAmount());
		database.set(arenaname + ".WinItems", items);

		// Saving the Database to be on the Save side.
		ArenaManager.save();
	}

	public void delete() {
		FileConfiguration config = TempleRun.database;

		// Delete Arena from the Config
		config.set(arenaname, null);

		// Remove Arena from the ArrayList
		ArenaManager manager = TempleRun.arenaManager;
		PlayerManager pmanager = TempleRun.playerManager;

		ArenaManager.arenas.remove(manager.getArena(arenaname));

		ArenaManager.save();

		// Kick all players who are in this Arena
		List<TRPlayer> it = PlayerManager.players;
		while (it.iterator().hasNext()) {
			TRPlayer trplayer = it.iterator().next();
			if (trplayer != null) {
				if (trplayer.getArena().getName().toLowerCase().equalsIgnoreCase(getName())) {
					Player bplayer = trplayer.getPlayer();
					if (bplayer != null);
						pmanager.leave(bplayer, false);
				}
			}
		}
	}
}
