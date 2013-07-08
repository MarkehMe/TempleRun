package de.jan.walkchecker;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.jan.TempleRun;
import de.jan.manager.WalkCheckerManager;

public class WalkChecker {
	
	private Player player;
	protected Location loc;
	
	protected int firstTask;
	protected int mainTask;
	
	private int staySeconds = 0;
	
	public WalkChecker(Player player) {
		
		if(!TempleRun.instance.getConfig().getBoolean("TempleRun.Settings.enableChecker")) {
			return;
		}
		
		this.player = player;
		Location playerLoc = player.getLocation();
		this.loc = new Location(player.getWorld(), playerLoc.getBlockX(), playerLoc.getBlockY(), playerLoc.getBlockZ());
		
		// Checker Starten
		startCheck();
		
		WalkCheckerManager wManager = TempleRun.walkManager;
		wManager.checkerList.add(this);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void startCheck() {
		firstTask = Bukkit.getScheduler().scheduleSyncDelayedTask(TempleRun.instance, new Runnable() {
			
			@Override
			public void run() {
				
				mainTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(TempleRun.instance, new Runnable() {
					@Override
					public void run() {
						int seconds = TempleRun.instance.getConfig().getInt("TempleRun.Settings.stayTime");
						if(staySeconds >= seconds) {
							TempleRun.walkManager.leaveBecauseWalkChecker(player);
							return;
						}
						
						Location playerLoc = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
						if(isSameLocation(loc, playerLoc)) {
							staySeconds++;
						} else {
							staySeconds = 0;
							loc = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
						}
					}
				}, 20L, 20L);
			}
			// Start the Check in 5 Seconds after joining.
		}, 20L * 5);
	}
	
	public void cancel() {
		Bukkit.getScheduler().cancelTask(firstTask);
		Bukkit.getScheduler().cancelTask(mainTask);
	}
	
	private boolean isSameLocation(Location loc1, Location loc2) {
		int x1 = loc1.getBlockX();
		int x2 = loc2.getBlockX();
		
		int z1 = loc1.getBlockZ();
		int z2 = loc2.getBlockZ();
		
		if(x1 == x2 && z1 == z2) {
			return true;
		}
		return false;
	}
}
