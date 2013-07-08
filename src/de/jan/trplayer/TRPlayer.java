package de.jan.trplayer;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.jan.arena.Arena;
import de.jan.walkchecker.WalkChecker;

public class TRPlayer {

	private Location oldLocation;
	private ItemStack[] inventory;
	private ItemStack[] armor;
	private Location checkpoint;
	public int coins = 0;
	private ArrayList<Integer> usedCoins = new ArrayList<Integer>();
	private ArrayList<Location> usedBlocks = new ArrayList<Location>();
	private long startTime = System.currentTimeMillis();
	private Arena arena;
	private Player player;
	
	public TRPlayer(Player player, Arena arena) {
		this.player = player;
		this.arena = arena;
		this.oldLocation = player.getLocation();
		this.inventory = player.getInventory().getContents();
		this.armor = player.getInventory().getArmorContents();
		checkpoint = arena.getSpawn();
		
		// WalkChecker
		new WalkChecker(player);
	}

	public String getName() {
		return player.getName();
	}

	public Location getOldlocation() {
		return oldLocation;
	}

	public long getStarttime() {
		return startTime;
	}

	public void addUsedCoin(Integer id) {
		this.usedCoins.add(id);
	}

	public boolean hasUsedCoin(Integer id) {
		return this.usedCoins.contains(id);
	}

	public void addUsedBlock(Location loc) {
		usedBlocks.add(loc);
	}

	public boolean hasUsedBlock(Location loc) {
		for (Location l : usedBlocks) {
			if (loc.getBlockX() == l.getBlockX() && loc.getBlockY() == l.getBlockY() && loc.getBlockZ() == l.getBlockZ()) {
				return true;
			}
		}
		return false;
	}

	public int getCoins() {
		return coins;
	}

	public void addCoin() {
		this.coins += 1;
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, (float) 2, (float) 4);
	}

	public void resetCoins() {
		this.coins = 0;
		this.usedCoins.clear();
		this.usedBlocks.clear();
	}

	public Location getCheckpoint() {
		return checkpoint;
	}

	public void setCheckpoint(Location loc) {
		this.checkpoint = loc;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getPlayer() {
		return player;
	}

	@SuppressWarnings("deprecation")
	public void restoreInventory() {
		player.getInventory().setContents(inventory);
		player.getInventory().setArmorContents(armor);
		player.updateInventory();
	}
}
