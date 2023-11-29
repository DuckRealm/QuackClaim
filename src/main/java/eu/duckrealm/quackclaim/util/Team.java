package eu.duckrealm.quackclaim.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Team {
    final UUID owner;
    List<UUID> trusted = new ArrayList<>();
    final UUID teamID;
    String TeamName = "";
    String TeamDescription = "";
    String TeamColor = "#52bbff";
    List<UUID> invitedPlayers = new ArrayList<>();
    List<UUID> bannedPlayers = new ArrayList<>();
    List<String> memberPermissions = new ArrayList<>();

    double teamMoney = 0;
    int maxClaimChunks = QuackConfig.DEFAULTCHUNKS;
    int claimedChunks = 0;
    boolean freeToJoin = false;
    boolean pvp = false;
    boolean explosions = false;
    public Team(Player owner, UUID teamID) {
        this.teamID = teamID;
        this.owner = owner.getUniqueId();
        this.TeamName = String.format("%s's team", owner.getName());
        this.TeamDescription = String.format("%s's amazing team", owner.getName());
    }

    public Team(UUID owner, UUID teamID) {
        this.teamID = teamID;
        this.owner = owner;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public UUID getTeamID() {
        return this.teamID;
    }

    public List<UUID> getTrusted() {
        return trusted;
    }

    public List<String> getTrustedString() {
        List<String> stringListTrusted = new ArrayList<>();

        trusted.forEach((UUID uuid) -> {
            stringListTrusted.add(uuid.toString());
        });

        return stringListTrusted;
    }

    public List<String> getInvitedString() {
        List<String> stringListInvited = new ArrayList<>();

        invitedPlayers.forEach((UUID uuid) -> {
            stringListInvited.add(uuid.toString());
        });

        return stringListInvited;
    }

    public Component getTeamComponent() {
        return Component.text(getTeamName(), TextColor.fromHexString(getTeamColor()))
                        .hoverEvent(Component.text("Team Name: ", NamedTextColor.WHITE)
                            .append(Component.text(getTeamName(), TextColor.fromHexString(getTeamColor())))
                            .append(Component.newline())
                            .append(Component.text("Team Description: ", NamedTextColor.WHITE))
                            .append(Component.text(getTeamDescription()))
                            .append(Component.newline())
                            .append(Component.text("Team ID: ", NamedTextColor.WHITE))
                            .append(Component.text(getTeamID().toString())));
    }
    public void setTeamName(String name) {
        this.TeamName = name;
    }

    public String getTeamName() {
        return this.TeamName;
    }

    public boolean isTeamBanned(UUID player) {
        return this.bannedPlayers.contains(player);
    }

    public void setTeamBanned(UUID player, boolean banned) {
        if(banned && !bannedPlayers.contains(player)) {
            bannedPlayers.add(player);
            return;
        }
        bannedPlayers.remove(player);
    }

    public List<String> getBannedString() {
        List<String> stringListBanned = new ArrayList<>();

        bannedPlayers.forEach((UUID uuid) -> {
            stringListBanned.add(uuid.toString());
        });

        return stringListBanned;
    }

    public List<String> getBannedNames() {
        List<String> stringListBanned = new ArrayList<>();

        bannedPlayers.forEach((UUID uuid) -> {
            stringListBanned.add(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
        });

        return stringListBanned;
    }

    public void addClaimedChunk() {
        claimedChunks++;
    }
    public void subtractClaimedChunk() {claimedChunks--;}
    public int getClaimedChunks() {
        return claimedChunks;
    }
    public void setTeamColor(String color) {
        this.TeamColor = color;
    }

    public String getTeamColor() {
        return this.TeamColor;
    }

    public void setTeamDescription(String name) {
        this.TeamDescription = name;
    }

    public String getTeamDescription() {
        return this.TeamDescription;
    }

    public boolean trustPlayer(Player player) {
        if(trusted.contains(player.getUniqueId())) return false;
        maxClaimChunks += 10;
        trusted.add(player.getUniqueId());
        return true;
    }

    public boolean untrustPlayer(Player player) {
        if(!trusted.contains(player.getUniqueId())) return false;
        maxClaimChunks -= 10;
        trusted.remove(player.getUniqueId());
        return true;
    }

    public boolean trustPlayer(UUID player) {
        if(trusted.contains(player)) return false;
        maxClaimChunks += 10;
        trusted.add(player);
        return true;
    }

    public boolean untrustPlayer(UUID player) {
        if(!trusted.contains(player)) return false;
        maxClaimChunks -= 10;
        trusted.remove(player);
        return true;
    }

    public  boolean isTrusted(UUID player) {
        if(player.equals(owner)) return true;
        return trusted.contains(player);
    }

    public boolean isPVPOn() {
        return pvp;
    }

    public void setPVP(boolean on) {
        pvp = on;
    }

    public boolean isFreeToJoin() {
        return freeToJoin;
    }

    public void setFreeToJoin(boolean on) {
        freeToJoin = on;
    }

    public boolean isExplosionOn() {
        return explosions;
    }

    public void setMaxClaimChunks(int chunks) {
        maxClaimChunks = chunks;
    }

    public int getMaxClaimChunks() {
        return maxClaimChunks;
    }

    public void setExplosion(boolean on) {
        explosions = on;
    }

    public void invitePlayer(UUID player) {
        invitedPlayers.add(player);
    }

    public void uninvitePlayer(UUID player) {
        invitedPlayers.remove(player);
    }
    public boolean isPlayerInvited(UUID player) {
        return invitedPlayers.contains(player) || freeToJoin;
    }

    @Override
    public String toString() {
        return "Team{" +
                "owner=" + owner +
                ", teamID=" + teamID +
                ", TeamName='" + TeamName + '\'' +
                ", TeamDescription='" + TeamDescription +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Team team = (Team) object;
        return pvp == team.pvp && explosions == team.explosions && Objects.equals(owner, team.owner) && Objects.equals(trusted, team.trusted) && Objects.equals(teamID, team.teamID) && Objects.equals(TeamName, team.TeamName) && Objects.equals(TeamDescription, team.TeamDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, trusted, teamID, TeamName, TeamDescription, pvp, explosions);
    }

    public Component getInfoComponent() {
        return Component.text("Team Name: ", NamedTextColor.WHITE)
                .append(getTeamComponent())
                .append(Component.newline())
                .append(Component.text("Team Description: ", NamedTextColor.WHITE))
                .append(Component.text(getTeamDescription(), NamedTextColor.WHITE))
                .append(Component.newline())
                .append(Component.text("Team ID: ", NamedTextColor.WHITE))
                .append(Component.text(getTeamID().toString())
                        .append(Component.newline())
                        .append(Component.text(getTrustedString().toString())));
    }

    public boolean getMemberPermission(String permission) {
        return memberPermissions.contains(permission);
    }

    public void setMemberPermissions(String permission, boolean enabled) {
        if(enabled && !memberPermissions.contains(permission)) {
            memberPermissions.add(permission);
        } else {
            memberPermissions.remove(permission);
        }
    }
    
    public static Team fromMap(Map<?,?> teamMap) {
        UUID owner = UUID.fromString((String) teamMap.get("owner"));
        List<String> trustedList = (List<String>) teamMap.get("trusted");
        List<String> invitedList = (List<String>) teamMap.get("invitedPlayers");
        List<String> bannedList = (List<String>) teamMap.get("bannedPlayers");
        List<String> permissionList = (List<String>) teamMap.get("permissions");
        UUID teamID = UUID.fromString((String) teamMap.get("teamID"));
        String teamName = (String) teamMap.get("teamName");
        String teamDescription = (String) teamMap.get("teamDescription");
        String teamColor = (String) teamMap.get("teamColor");
        int claimChunks = (int) teamMap.get("claimChunks");
        boolean pvp = (boolean) teamMap.get("pvp");
        boolean freeToJoin = (boolean) teamMap.get("freeToJoin");
        boolean explosions = (boolean) teamMap.get("explosions");

        Team team = new Team(owner, teamID);
        Teams.setPlayerInTeam(owner, teamID);
        for (String trustedPlayer : trustedList) {
            if (Teams.isPlayerInTeam(UUID.fromString(trustedPlayer))) {
                Bukkit.getLogger().info("Player already in a team! This is an error. Please investigate!");
                Bukkit.getLogger().info(String.format("Attempting to add to team: %s", teamID));
                Bukkit.getLogger().info(String.format("Player already in team: %s", Teams.getTeamByPlayer(UUID.fromString(trustedPlayer)).getTeamID()));
                continue;
            }
            Teams.setPlayerInTeam(UUID.fromString(trustedPlayer), teamID);
            team.trustPlayer(UUID.fromString(trustedPlayer));
        }

        for (String invitedPlayer : invitedList) {
            team.invitePlayer(UUID.fromString(invitedPlayer));
        }

        for (String bannedPlayer : bannedList) {
            team.setTeamBanned(UUID.fromString(bannedPlayer), true);
        }

        for(String permission : permissionList) {
            team.setMemberPermissions(permission, true);
        }

        team.setPVP(pvp);
        team.setExplosion(explosions);
        team.setFreeToJoin(freeToJoin);
        team.setTeamName(teamName);
        team.setTeamColor(teamColor);
        team.setMaxClaimChunks(claimChunks);
        team.setTeamDescription(teamDescription);

        return team;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> teamMap = new HashMap<>();
        teamMap.put("owner", this.getOwner().toString());
        teamMap.put("trusted", this.getTrustedString());
        teamMap.put("invitedPlayers", this.getInvitedString());
        teamMap.put("bannedPlayers", this.getBannedString());
        teamMap.put("teamID", this.getTeamID().toString());
        teamMap.put("teamName", this.getTeamName());
        teamMap.put("teamDescription", this.getTeamDescription());
        teamMap.put("teamColor", this.getTeamColor());
        teamMap.put("pvp", this.isPVPOn());
        teamMap.put("claimChunks", this.maxClaimChunks);
        teamMap.put("freeToJoin", this.isFreeToJoin());
        teamMap.put("explosions", this.isExplosionOn());
        teamMap.put("permissions", this.memberPermissions);
        return teamMap;
    }

    public boolean hasMoney(double amount) {
        return teamMoney >= amount;
    }

    public double getMoney() {
        return teamMoney;
    }
    public void deposit(double amount) {
        teamMoney += amount;
    }

    public boolean withdraw(double amount) {
        if(teamMoney < amount) return false;
        teamMoney -= amount;
        return true;
    }
}
