package eu.duckrealm.quackclaim.manager;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;

public class ClaimManager {

    public ClaimManager() { }

    public static boolean isPermitted(Player player, Chunk chunk) {
        UUID teamID = QuackClaim.claims.get(chunk.getChunkKey());
        if(teamID == null) return true;
        boolean isTrusted = Teams.getTeam(teamID).isTrusted(player.getUniqueId());
        return isTrusted;
    }

    public static boolean isExplosionPermitted(Chunk chunk) {
        UUID teamID = QuackClaim.claims.get(chunk.getChunkKey());
        if(teamID == null) return true;
        return Teams.getTeam(teamID).isExplosionOn();
    }

    public static boolean isPVPOn(Chunk chunk) {
        UUID teamID = QuackClaim.claims.get(chunk.getChunkKey());
        if(teamID == null) return true;
        return Teams.getTeam(teamID).isPVPOn();
    }

    public static boolean isExtendPermitted(Chunk source, Chunk destination) {
        UUID sourceClaim = QuackClaim.claims.get(source.getChunkKey());
        UUID destinationClaim = QuackClaim.claims.get(destination.getChunkKey());

        if(sourceClaim == null && destinationClaim == null) return true;
        if(sourceClaim == destinationClaim) return true;
        if(sourceClaim == null && destinationClaim != null) return false;
        if(sourceClaim != null && destinationClaim == null) return  true;
        return true;
    }

    public static boolean isBlockTransferLegal(Chunk source, Chunk destination) {
        return isBlockTransferLegal(Teams.getTeamByChunk(source), Teams.getTeamByChunk(destination));
    }
    public static boolean isBlockTransferLegal(Team source, Team destination) {
        if(isNull(source)) source = QuackClaim.SERVERTEAM;
        if(isNull(destination)) destination = QuackClaim.SERVERTEAM;
        return source.equals(destination);
    }

    public static boolean isBlockTransferLegal(long sourceChunkKey, long destinationChunkKey) {
        return isBlockTransferLegal(Teams.getTeamByChunkKey(sourceChunkKey), Teams.getTeamByChunkKey(destinationChunkKey));
    }
}
