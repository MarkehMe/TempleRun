package TempleRun.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import TempleRun.TempleRun;
import TempleRun.Util.Util;

public class PlayerListener implements Listener {
	
	public TempleRun main;
	
	public PlayerListener(TempleRun main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		if(Util.isPlaying(player.getName())) {
			
			Location loc = Util.getOldLocation(player.getName());
			player.teleport(loc);
			
			Util.removePlayer(player.getName());
		}
		
	}

}
