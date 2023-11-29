package eu.duckrealm.quackclaim.subcommands.team;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.QuackConfig;
import eu.duckrealm.quackclaim.util.Team;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class buyclaimchunk {
	public static boolean buy(Team team, int amount) {
		if(team.withdraw(amount * QuackConfig.CHUNKPRICE)) {
			int old = team.getMaxClaimChunks();
			team.setMaxClaimChunks(old + amount);
			return true;
		}
		return false;
	}
}
