package eu.duckrealm.quackclaim.commands;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class AdminCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) commandSender;

        switch (args[0]) {
            case "delete" -> {
                if(!QuackClaim.claims.containsKey(player.getChunk().getChunkKey())) {
                    player.sendMessage(Component.text("You are not standing inside a team claim", NamedTextColor.RED));
                    return true;
                }
                Team team = Teams.getTeam(QuackClaim.claims.get(player.getChunk().getChunkKey()));


                UUID toDelete = team.getTeamID();
                for (UUID trusted : team.getTrusted()) {
                    Teams.removePlayerInTeam(trusted);
                }
                Teams.removePlayerInTeam(team.getOwner());
                Teams.removeTeam(team.getTeamID());

                BukkitRunnable deleteClaims = new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        QuackClaim.claims.forEach((Long chunkKey, UUID teamID) -> {
                            if(teamID.equals(toDelete)) {
                                QuackClaim.claims.remove(chunkKey);
                            }
                        });
                    }
                };
                deleteClaims.runTaskAsynchronously(QuackClaim.instance);
            }

            case "list" -> {
                player.sendMessage(Teams.getAllTeamsAsComponent());
            }

            case "edit" -> {
                return editTeam(player, command, label, args);
            }

            case "save" -> {
                Configuration teamData = new Configuration(QuackClaim.instance, "./data/teams.yml");
                Configuration claimData = new Configuration(QuackClaim.instance, "./data/claims.yml");

                try {
                    teamData.load();
                } catch (IOException e) {
                    player.sendMessage(Component.text("Critical Error while loading team file! Claims will break!", NamedTextColor.RED));
                    throw new RuntimeException(e);
                }
                try {
                    claimData.load();
                } catch (IOException e) {
                    player.sendMessage(Component.text("Critical Error while loading claim file! Claims will break!", NamedTextColor.RED));
                    throw new RuntimeException(e);
                }

                List<Map<String, Object>> serializedTeams = new ArrayList<>();

                for (Team team : Teams.getValues()) {
                    serializedTeams.add(team.toMap());
                }

                teamData.getConfig().set("teams", serializedTeams);

                QuackClaim.claims.forEach((Long chunkKey, UUID teamID) -> {
                    claimData.getConfig().set(chunkKey.toString(), teamID.toString());
                });

                try {
                    claimData.save();
                } catch (IOException e) {
                    player.sendMessage(Component.text("Critical Error while saving claim file! Claims will break!", NamedTextColor.RED));
                    throw new RuntimeException(e);
                }

                try {
                    teamData.save();
                } catch (IOException e) {
                    player.sendMessage(Component.text("Critical Error while saving teams file! Claims will break!", NamedTextColor.RED));
                    throw new RuntimeException(e);
                }

                player.sendMessage(Component.text("Successfully saved current teams and claims to disk!", NamedTextColor.GREEN));
            }

            case "resetPlayer" -> {
                if(args.length < 2) {
                    player.sendMessage(Component.text("Specify player to reset", NamedTextColor.RED));
                    return true;
                }
                Player player2 = Bukkit.getPlayer(args[1]);
                if(player2 == null || !player2.isOnline()) {
                    player.sendMessage(Component.text("Player is not online or cant be found.", NamedTextColor.RED));
                    return true;
                }
                Team team = Teams.getTeamByPlayer(player2.getUniqueId());
                if(team != null) {
                    if(team.getOwner().equals(player.getUniqueId())) {
                        Teams.removeTeam(team.getTeamID());
                    } else {
                        team.uninvitePlayer(player2.getUniqueId());
                        team.untrustPlayer(player2);
                    }
                }
                Teams.removePlayerInTeam(player2.getUniqueId());

                player.sendMessage(Component.text("Player has been reset!", NamedTextColor.GREEN));
                player2.sendMessage(Component.text("You have been reset by an admin!", NamedTextColor.RED));
            }

            case "chunkPerformance" -> {
                player.sendMessage(Component.text("This chunk gets a load score of: ")
                        .append(Component.text(ChunkLoadAnalyzer.getChunkScore(player.getChunk()), NamedTextColor.GREEN)));
            }

            case "reload" -> {
                QuackConfig.initialize();
                player.sendMessage(Component.text("Reloaded Configs", NamedTextColor.GREEN));
            }

            case "ignore" -> {
                if(!QuackClaim.ignoringClaims.contains(player.getUniqueId())) {
                    QuackClaim.ignoringClaims.add(player.getUniqueId());
                    player.sendMessage(Component.text("Now ignoring claims.", NamedTextColor.GREEN));
                    return true;
                }
                QuackClaim.ignoringClaims.remove(player.getUniqueId());
                player.sendMessage(Component.text("No longer ignoring claims.", NamedTextColor.RED));
                return true;
            }

            default -> player.sendMessage(Component.text("Not a valid option", NamedTextColor.RED));
        }
        return true;
    }


    private boolean editTeam(Player player, Command command, String label, String[] args) {
        if(!QuackClaim.claims.containsKey(player.getChunk().getChunkKey())) {
            player.sendMessage(Component.text("You are not standing inside a team claim", NamedTextColor.RED));
            return true;
        }
        Team team = Teams.getTeam(QuackClaim.claims.get(player.getChunk().getChunkKey()));

        if(args.length < 3) {
            player.sendMessage(Component.text("Missing value", NamedTextColor.RED));
            return true;
        }
        switch (args[1]) {
            case "freeToJoin" -> {
                boolean on = misc.OnOffToBoolean(args[2]);
                team.setFreeToJoin(on);
                player.sendMessage(Component.text("Property freeToJoin was set to: ")
                        .append(on ? Component.text("true", NamedTextColor.GREEN) : Component.text("false", NamedTextColor.RED)));
            }
            case "pvp" -> {
                boolean on = misc.OnOffToBoolean(args[2]);
                team.setPVP(on);
                player.sendMessage(Component.text("Property pvp was set to: ")
                        .append(on ? Component.text("true", NamedTextColor.GREEN) : Component.text("false", NamedTextColor.RED)));
            }
            case "explosions" -> {
                boolean on = misc.OnOffToBoolean(args[2]);
                team.setExplosion(on);
                player.sendMessage(Component.text("Property explosions was set to: ")
                        .append(on ? Component.text("true", NamedTextColor.GREEN) : Component.text("false", NamedTextColor.RED)));
            }
            case "color" -> {
                TextColor textColor = TextColor.fromHexString(args[2]);
                if(textColor == null) {
                    player.sendMessage(Component.text(String.format("%s is a invalid color", args[2]), NamedTextColor.RED));
                    return true;
                }
                team.setTeamColor(textColor.asHexString());
                player.sendMessage(Component.text("Property color was set to: ")
                        .append(Component.text("■", textColor)));
            }
            case "description" -> {
                String[] subArray =  Arrays.copyOfRange(args, 2, args.length);
                String description = String.join(" ", subArray).trim();

                if (description.length() > QuackConfig.MAXDESCLENGTH) {
                    player.sendMessage(Component.text("Your description is longer than ", NamedTextColor.RED)
                            .append(Component.text(QuackConfig.MAXDESCLENGTH, NamedTextColor.GREEN)
                                    .append(Component.text(" characters!", NamedTextColor.RED))));
                    return true;
                }

                player.sendMessage(Component.text("Property description was set to: ")
                        .append(Component.text(description)));

                team.setTeamDescription(description);
            }
            case "name" -> {
                String[] subArray =  Arrays.copyOfRange(args, 2, args.length);
                String name = String.join(" ", subArray).trim();

                if (name.length() > QuackConfig.MAXNAMELENGTH) {
                    player.sendMessage(Component.text("Your name is longer than ", NamedTextColor.RED)
                            .append(Component.text(QuackConfig.MAXNAMELENGTH, NamedTextColor.GREEN)
                                    .append(Component.text(" characters!", NamedTextColor.RED))));
                    return true;
                }
                player.sendMessage(Component.text("Property name was set to: ")
                        .append(Component.text(name)));
                team.setTeamName(name);
            }

            default -> player.sendMessage(Component.text("Not a valid option", NamedTextColor.RED));
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> editOptionsBool = Arrays.stream(new String[] {"pvp", "explosions", "freeToJoin"}).toList();
        if (args.length == 1) return Arrays.stream(new String[] { "save", "edit", "list", "delete", "chunkPerformance", "resetPlayer", "chunkRenderTime", "reload" }).toList();
        switch(args[0]) {
            case "edit" -> {
                if (args.length > 2 && editOptionsBool.contains(args[1])) return Arrays.stream(new String[] { "on", "off" }).toList();
                return Arrays.stream(new String[] { "freeToJoin", "pvp", "explosions", "color", "description", "name" }).toList();
            }
        }
        return Collections.emptyList();
    }
}
