package eu.duckrealm.quackclaim.util;

import eu.duckrealm.quackclaim.QuackClaim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static javax.swing.UIManager.put;

public class Teams {
    private static final HashMap<UUID, Team> teams = new HashMap<>();
    private static final HashMap<String, UUID> teamsByNames = new HashMap<>();
    private static final HashMap<UUID, UUID> playerinTeam = new HashMap<>();

    public static void putTeam(Team team) {
        teamsByNames.put(team.getTeamName(), team.getTeamID());
        teams.put(team.getTeamID(), team);
    }

    public static Team getTeam(UUID teamID) {
        return teams.get(teamID);
    }

    public static Team getTeamByName(String name) {
        return getTeam(teamsByNames.get(name));
    }

    public static Team getTeamByPlayer(UUID player) {
        return getTeam(playerinTeam.get(player));
    }

    public static Team getTeamByChunk(Chunk chunk) {return getTeam(QuackClaim.claims.get(chunk.getChunkKey()));}

    public static Team getTeamByChunkKey(long chunkKey) {return getTeam(QuackClaim.claims.get(chunkKey));}

    public static boolean isPlayerInTeam(UUID player) {
        return playerinTeam.containsKey(player);
    }

    public static void setPlayerInTeam(UUID player, UUID teamID) {
        playerinTeam.put(player, teamID);
    }

    public static void removePlayerInTeam(UUID player) {
        playerinTeam.remove(player);
    }

    public static List<String> getAllTeamNames() {
        return teamsByNames.keySet().stream().toList();
    }
    public static void removeTeam(UUID teamID) {
        teamsByNames.remove(getTeam(teamID).getTeamName());
        teams.remove(teamID);
    }

    public static Collection<Team> getValues() {
        return teams.values();
    }

    public static Component getAllTeamsAsComponent() {
        Component component = Component.text(teams.size(), NamedTextColor.GREEN)
                .append(Component.text(" teams:", NamedTextColor.WHITE));

        for(Team team : getValues()) {
            component = component.append(Component.newline());
            component = component.append(team.getTeamComponent());
        }

        return component;
    }
    public static List<UUID> getAllTeams() {
        return teams.keySet().stream().toList();
    }

    public static List<String> getAllPermissions() {
        return Arrays.stream(new String[] { "buyChunks", "sellChunks", "deposit", "withdraw", "immediateWithdraw", "edit" }).toList();
    }

}
