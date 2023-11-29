package eu.duckrealm.quackclaim.subcommands.team;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.QuackConfig;
import eu.duckrealm.quackclaim.util.Team;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class sellclaimchunk {
	public static boolean sell(Team team, int amount) {
		if(team.getMaxClaimChunks() <= QuackConfig.DEFAULTCHUNKS) return false;
		team.deposit(amount * QuackConfig.CHUNKPRICE);
		int old = team.getMaxClaimChunks();
		team.setMaxClaimChunks(old - amount);
		return true;
	}
}
