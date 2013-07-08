package de.jan.manager;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.jan.TempleRun;
import de.jan.trplayer.TRPlayer;
import de.jan.walkchecker.WalkChecker;

public class WalkCheckerManager {

	private PlayerManager playerManager;
	private GhostManager ghostManager;
	private String prefix = "§b[§6TempleRun§b] §7";
	
	public ArrayList<WalkChecker> checkerList = new ArrayList<WalkChecker>();
	
	@SuppressWarnings("deprecation")
	public void leaveBecauseWalkChecker(Player player) {
		playerManager = TempleRun.playerManager;
		String playername = player.getName();
		WalkChecker checker = getWalkChecker(playername);
		if(checker == null) {
			return;
		}
		
		checker.cancel();
		checkerList.remove(checker);

		// Set the Speed back to 0.2
		player.setWalkSpeed(0.2F);

		playerManager.clearInventory(player);

		TRPlayer trplayer = playerManager.getTRPlayer(playername);

		if (trplayer != null) {
			trplayer.restoreInventory();
			player.teleport(trplayer.getOldlocation());
			PlayerManager.players.remove(trplayer);
			
			TempleRun.ghostManager = ghostManager;
			if (TempleRun.instance.getConfig().getBoolean("TempleRun.Settings.ghost", false)) {
				ghostManager.removeGhost(player);
			}
			int seconds = TempleRun.instance.getConfig().getInt("TempleRun.Settings.stayTime");
			player.sendMessage(prefix + "You are not allowed to stay longer then §e" + seconds + " §7seconds in TempleRun.");
			player.updateInventory();
		}
	}
	
	public WalkChecker getWalkChecker(String playername) {
		for(WalkChecker w : checkerList) {
			if(w.getPlayer().getName().equalsIgnoreCase(playername)) {
				return w;
			}
		}
		return null;
	}
}
