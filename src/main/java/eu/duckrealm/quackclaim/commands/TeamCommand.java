package eu.duckrealm.quackclaim.commands;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.subcommands.team.buyclaimchunk;
import eu.duckrealm.quackclaim.subcommands.team.sellclaimchunk;
import eu.duckrealm.quackclaim.util.QuackConfig;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import eu.duckrealm.quackclaim.util.misc;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.Name;
import java.util.*;

public class TeamCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if(args.length < 1) {
            player.sendMessage(Component.text("Missing action. Use /qt <action>", NamedTextColor.RED));
            return true;
        }
        switch (args[0]) {
            case "create" -> {
                if(Teams.isPlayerInTeam(player.getUniqueId())){
                    Team team = Teams.getTeamByPlayer(player.getUniqueId());

                    player.sendMessage(Component.text("You are already in: ", NamedTextColor.RED)
                            .append(team.getTeamComponent()));

                    return true;
                }
                UUID teamID = UUID.randomUUID();
                Team team = new Team(player, teamID);
                Teams.putTeam(team);
                Teams.setPlayerInTeam(player.getUniqueId(), teamID);

                player.sendMessage(Component.text("Created ", NamedTextColor.GRAY)
                        .append(team.getTeamComponent()));
            }

            case "join" ->{
                if(Teams.isPlayerInTeam(player.getUniqueId())){
                    Team team = Teams.getTeamByPlayer(player.getUniqueId());

                    player.sendMessage(Component.text("You are already in: ", NamedTextColor.RED)
                            .append(team.getTeamComponent()));
                    return true;
                }

                Team team = Teams.getTeamByName(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));

                if(team == null) {
                    player.sendMessage(Component.text("Team does not exist!", NamedTextColor.RED));
                    return true;
                }

                if(team.isPlayerInvited(player.getUniqueId())) {

                    if(!team.trustPlayer(player.getUniqueId())) {
                        player.sendMessage(Component.text("Trusting went wrong", NamedTextColor.RED));
                        return true;
                    }

                    team.uninvitePlayer(player.getUniqueId());
                    Teams.setPlayerInTeam(player.getUniqueId(), team.getTeamID());
                    player.sendMessage(Component.text("You are now part of ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent()));

                    return true;
                }

                Teams.putTeam(team);

                player.sendMessage(Component.text("You are not invited!", NamedTextColor.RED));
            }

            case "kick" ->{
                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }

                if(args.length < 2) {
                    player.sendMessage(Component.text("Specify player to invite", NamedTextColor.RED));
                    return true;
                }

                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if(targetPlayer == null || !targetPlayer.isOnline()) {
                    targetPlayer = (Player) Bukkit.getOfflinePlayer(args[1]);
                    return true;
                }

                if(!Teams.isPlayerInTeam(targetPlayer.getUniqueId())){
                    player.sendMessage(Component.text("Player not in a team", NamedTextColor.RED));
                    return true;
                }

                if(!Teams.isPlayerInTeam(player.getUniqueId())){
                    player.sendMessage(Component.text("You are not in a team", NamedTextColor.RED));
                    return true;
                }

                Team playerTeam = Teams.getTeamByPlayer(targetPlayer.getUniqueId());
                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(!playerTeam.equals(team)) {
                    player.sendMessage(Component.text("Player not in your team", NamedTextColor.RED));
                }

                if(!player.getUniqueId().equals(team.getOwner())) {
                    player.sendMessage(Component.text("You can not kick members ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }


                if(team.isTrusted(targetPlayer.getUniqueId())) {
                    team.untrustPlayer(targetPlayer.getUniqueId());
                    team.uninvitePlayer(targetPlayer.getUniqueId());
                    Teams.removePlayerInTeam(targetPlayer.getUniqueId());
                    player.sendMessage(Component.text("Kicked ", NamedTextColor.GRAY)
                            .append(Component.text(targetPlayer.getName(), NamedTextColor.GOLD))
                            .append(Component.text(" from ", NamedTextColor.GRAY))
                            .append(team.getTeamComponent()));
                    targetPlayer.sendMessage(Component.text("You have been kicked from ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent()));
                    return true;
                }

                Teams.putTeam(team);
                player.sendMessage(Component.text("Player not in your team!", NamedTextColor.RED));
            }

            case "leave" -> {
                if(!Teams.isPlayerInTeam(player.getUniqueId())){
                    Team team = Teams.getTeamByPlayer(player.getUniqueId());

                    player.sendMessage(Component.text("You are not in a team yet! There is nothing to leave.", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(player.getUniqueId().equals(team.getOwner())) {
                    player.sendMessage(Component.text("You can not leave ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as the owner!", NamedTextColor.GRAY)));
                    return true;
                }

                team.untrustPlayer(player.getUniqueId());
                team.uninvitePlayer(player.getUniqueId());
                Teams.removePlayerInTeam(player.getUniqueId());

                player.sendMessage(Component.text("You are no longer part of ", NamedTextColor.GRAY)
                        .append(team.getTeamComponent()));

            }

            case "delete" -> {
                if(!Teams.isPlayerInTeam(player.getUniqueId())){
                    Team team = Teams.getTeamByPlayer(player.getUniqueId());
                    player.sendMessage(Component.text("You are not in a team!", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(!player.getUniqueId().equals(team.getOwner())) {
                    player.sendMessage(Component.text("You can not delete ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }

                UUID toDelete = team.getTeamID();
                for (UUID trusted : team.getTrusted()) {
                    team.untrustPlayer(trusted);
                    team.uninvitePlayer(trusted);
                    Teams.removePlayerInTeam(trusted);
                }
                Teams.removeTeam(team.getTeamID());
                Teams.removePlayerInTeam(player.getUniqueId());

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

            case "ignore" -> {
                Team team = Teams.getTeam(UUID.fromString(args[1]));
                if(team.isPlayerInvited(player.getUniqueId())) {
                    team.uninvitePlayer(player.getUniqueId());

                    player.sendMessage(Component.text("You ignored the invite from ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent()));

                    return true;
                }
                player.sendMessage(Component.text("You are not invited!", NamedTextColor.RED));
            }

            case "info" -> {
                if(QuackClaim.claims.containsKey(player.getChunk().getChunkKey())) {
                    Team team = Teams.getTeam(QuackClaim.claims.get(player.getChunk().getChunkKey()));
                    if(team != null) {
                        player.sendMessage(team.getInfoComponent());
                        return true;
                    }
                }
                if(Teams.isPlayerInTeam(player.getUniqueId())){
                    Team team = Teams.getTeamByPlayer(player.getUniqueId());
                    player.sendMessage(team.getInfoComponent());
                    return true;
                }
                player.sendMessage(Component.text("No team to inspect.", NamedTextColor.RED)
                        .append(Component.newline())
                        .append(Component.text("Join a team or stand inside a claim to inspect", NamedTextColor.GRAY)));
            }

            case "edit" -> {
                return editTeam(player, command, label, args);
            }

            case "invite" -> {
                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }
                if(args.length < 2) {
                    player.sendMessage(Component.text("Specify player to invite", NamedTextColor.RED));
                    return true;
                }
                Player playerToInvite = Bukkit.getPlayer(args[1]);
                if(playerToInvite == null || !playerToInvite.isOnline()) {
                    player.sendMessage(Component.text("Player is not online or cant be found.", NamedTextColor.RED));
                    return true;
                }
                Team team = Teams.getTeamByPlayer(player.getUniqueId());
                team.invitePlayer(playerToInvite.getUniqueId());

                playerToInvite.sendMessage(Component.text(player.getName(), NamedTextColor.GREEN)
                        .append(Component.text(" wants you to join ", NamedTextColor.GRAY))
                        .append(team.getTeamComponent()));

                playerToInvite.sendMessage(Component.text("Join", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand(String.format("/qt join %s", team.getTeamName())))
                        .append(Component.text(" | ", NamedTextColor.GOLD)
                                .append(Component.text("Ignore", NamedTextColor.RED)).clickEvent(ClickEvent.runCommand(String.format("/qt ignore %s", team.getTeamName())))));
            }

            case "ban" -> {
                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }
                if(args.length < 2) {
                    player.sendMessage(Component.text("Specify player to ban from your team", NamedTextColor.RED));
                    return true;
                }
                Player playerToBan = Bukkit.getPlayer(args[1]);
                if(playerToBan == null || !playerToBan.isOnline()) {
                    player.sendMessage(Component.text("Player is not online or cant be found.", NamedTextColor.RED));
                    return true;
                }
                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(team.isTeamBanned(playerToBan.getUniqueId())) {
                    team.untrustPlayer(playerToBan.getUniqueId());
                    team.uninvitePlayer(playerToBan.getUniqueId());
                    Teams.removePlayerInTeam(playerToBan.getUniqueId());
                }

                team.setTeamBanned(playerToBan.getUniqueId(), true);
            }

            case "pardon" -> {
                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }
                if(args.length < 2) {
                    player.sendMessage(Component.text("Specify player to pardon from your team", NamedTextColor.RED));
                    return true;
                }
                Player playerToBan = Bukkit.getPlayer(args[1]);
                if(playerToBan == null || !playerToBan.isOnline()) {
                    player.sendMessage(Component.text("Player is not online or cant be found.", NamedTextColor.RED));
                    return true;
                }
                Team team = Teams.getTeamByPlayer(player.getUniqueId());
                team.setTeamBanned(playerToBan.getUniqueId(), false);
            }

            case "buyChunk" -> {
                if(!QuackClaim.economyEnabled) {
                    player.sendMessage(Component.text("No economy registered. Ask an admin to install one.", NamedTextColor.RED));
                    return true;
                }

                if(!QuackConfig.ECOENABLED) {
                    player.sendMessage(Component.text("Economy is disabled.", NamedTextColor.RED));
                    return true;
                }

                    if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(team.getMemberPermission("buyChunks")) {
                    player.sendMessage(Component.text("You can not buy chunks for ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }

                int amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;

                if(buyclaimchunk.buy(team, amount)) {
                    player.sendMessage(Component.text("Bought ", NamedTextColor.GRAY)
                            .append(Component.text(amount, NamedTextColor.GREEN))
                            .append(Component.text(amount > 1 ? " chunks" : " chunk")));
                } else {
                    player.sendMessage(Component.text("Unable to buy chunks, too little money.", NamedTextColor.RED));
                }
            }

            case "sellChunk" -> {
                if(!QuackClaim.economyEnabled) {
                    player.sendMessage(Component.text("No economy registered. Ask an admin to install one.", NamedTextColor.RED));
                    return true;
                }

                if(!QuackConfig.ECOENABLED) {
                    player.sendMessage(Component.text("Economy is disabled.", NamedTextColor.RED));
                    return true;
                }
                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(team.getMemberPermission("sellChunks")) {
                    player.sendMessage(Component.text("You can not sell chunks of ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }

                int amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;

                if(!sellclaimchunk.sell(team, amount))  {
                    player.sendMessage(Component.text("Cant sell chunks", NamedTextColor.RED));
                }

                player.sendMessage(Component.text("Sold ", NamedTextColor.GRAY)
                        .append(Component.text(amount, NamedTextColor.GREEN))
                        .append(Component.text(amount > 1 ? " chunks" : " chunk")));

            }

            case "deposit" -> {
                if(!QuackClaim.economyEnabled) {
                    player.sendMessage(Component.text("No economy registered. Ask an admin to install one.", NamedTextColor.RED));
                    return true;
                }

                if(!QuackConfig.ECOENABLED) {
                    player.sendMessage(Component.text("Economy is disabled.", NamedTextColor.RED));
                    return true;
                }

                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(team.getMemberPermission("deposit")) {
                    player.sendMessage(Component.text("You can not deposit to ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }

                double amount = args.length > 1 ? Double.parseDouble(args[1]) : 0;
                String currencyName = " " + ((amount > 1) ? QuackClaim.economy.currencyNamePlural() : QuackClaim.economy.currencyNameSingular());

                EconomyResponse response = QuackClaim.economy.withdrawPlayer(player, amount);

                if(response.transactionSuccess()) {
                    team.deposit(amount);
                    player.sendMessage(Component.text("Deposited ", NamedTextColor.GRAY)
                            .append(Component.text(amount, NamedTextColor.GREEN))
                            .append(Component.text(currencyName, NamedTextColor.GOLD)));
                    return true;
                }

                player.sendMessage(Component.text(response.errorMessage, NamedTextColor.RED));
                return true;
            }

            case "withdraw" -> {
                if(!QuackClaim.economyEnabled) {
                    player.sendMessage(Component.text("No economy registered. Ask an admin to install one.", NamedTextColor.RED));
                    return true;
                }

                if(!QuackConfig.ECOENABLED) {
                    player.sendMessage(Component.text("Economy is disabled.", NamedTextColor.RED));
                    return true;
                }

                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());

                if(team.getMemberPermission("withdraw")) {
                    player.sendMessage(Component.text("You can not withdraw from ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }

                double amount = args.length > 1 ? Double.parseDouble(args[1]) : 0;
                String currencyName = " " + ((amount > 1) ? QuackClaim.economy.currencyNamePlural() : QuackClaim.economy.currencyNameSingular());

                if(team.getMemberPermission("immediateWithdraw")) {

                    EconomyResponse response = QuackClaim.economy.depositPlayer(player, amount);

                    if(response.transactionSuccess()) {
                        team.withdraw(amount);
                        player.sendMessage(Component.text("Withdrew ", NamedTextColor.GRAY)
                                .append(Component.text(amount, NamedTextColor.GREEN))
                                .append(Component.text(currencyName, NamedTextColor.GOLD)));
                        return true;
                    }

                    player.sendMessage(Component.text(response.errorMessage, NamedTextColor.RED));
                    return true;
                }

                Player owner = Bukkit.getPlayer(team.getOwner());
                if(owner == null) {
                    player.sendMessage(Component.text("Owner not online! Please wait for them.", NamedTextColor.RED));
                    return true;
                }

                ClickCallback allowWithdraw = (Audience audience) -> {
                    if(audience instanceof Player) {
                        owner.sendMessage(Component.text("Allowed withdraw for ", NamedTextColor.GREEN)
                                .append(Component.text(player.getName(), NamedTextColor.GOLD)));
                        if(!team.hasMoney(amount)) {
                            owner.sendMessage(Component.text("Not enough money", NamedTextColor.RED));
                        }
                        EconomyResponse response = QuackClaim.economy.depositPlayer(player, amount);

                        if(response.transactionSuccess()) {
                            team.withdraw(amount);
                            player.sendMessage(Component.text("Withdrew ", NamedTextColor.GRAY)
                                    .append(Component.text(amount, NamedTextColor.GREEN))
                                    .append(Component.text(currencyName, NamedTextColor.GOLD)));
                            return;
                        }
                        owner.sendMessage(Component.text(response.errorMessage, NamedTextColor.RED));
                        player.sendMessage(Component.text(response.errorMessage, NamedTextColor.RED));
                    }
                };

                ClickCallback denyWithdraw = (Audience audience) -> {
                    owner.sendMessage(Component.text("Denied withdraw for ", NamedTextColor.RED)
                            .append(Component.text(player.getName(), NamedTextColor.GOLD)));
                    player.sendMessage(Component.text("You were denied the withdraw.", NamedTextColor.RED));
                };

                owner.sendMessage(Component.text(player.getName(), NamedTextColor.GOLD)
                        .append(Component.text(" wants to withdraw ", NamedTextColor.GRAY))
                        .append(Component.text(amount, NamedTextColor.GREEN))
                        .append(Component.text(" from your teams Account.", NamedTextColor.GRAY))
                        .append(Component.newline())
                        .append(Component.text("Allow", NamedTextColor.GREEN).clickEvent(ClickEvent.callback(allowWithdraw)))
                        .append(Component.text(" | ", NamedTextColor.GOLD))
                        .append(Component.text("Deny", NamedTextColor.RED).clickEvent(ClickEvent.callback(denyWithdraw))));

                return true;
            }

            case "bal" -> {
                if(!QuackClaim.economyEnabled) {
                    player.sendMessage(Component.text("No economy registered. Please ask an admin to install one.", NamedTextColor.RED));
                    return true;
                }

                if(!Teams.isPlayerInTeam(player.getUniqueId())) {
                    player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
                    return true;
                }

                Team team = Teams.getTeamByPlayer(player.getUniqueId());
                double amount = team.getMoney();
                String currencyName = " " + ((amount > 1) ? QuackClaim.economy.currencyNamePlural() : QuackClaim.economy.currencyNameSingular());
                player.sendMessage(Component.text("Balance: ", NamedTextColor.GRAY)
                        .append(Component.text(amount, NamedTextColor.GREEN))
                        .append(Component.text(currencyName, NamedTextColor.GOLD)));

            }

            default -> player.sendMessage(Component.text("Not a valid option", NamedTextColor.RED));
        }
        return true;
    }

    private boolean editTeam(Player player, Command command, String label, String[] args) {
        if(!Teams.isPlayerInTeam(player.getUniqueId())) {
            player.sendMessage(Component.text("No team to inspect.", NamedTextColor.RED)
                    .append(Component.newline())
                    .append(Component.text("Join a team or create one!", NamedTextColor.GRAY)));
            return true;
        }
        Team team = Teams.getTeamByPlayer(player.getUniqueId());

        if(!player.getUniqueId().equals(team.getOwner()) || team.getMemberPermission("edit")) {
            player.sendMessage(Component.text("You can not edit ", NamedTextColor.GRAY)
                    .append(team.getTeamComponent())
                    .append(Component.text(" as a member!", NamedTextColor.GRAY)));
            return true;
        }

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

            case "permission" -> {
                if(!player.getUniqueId().equals(team.getOwner())) {
                    player.sendMessage(Component.text("You can not edit permissions of ", NamedTextColor.GRAY)
                            .append(team.getTeamComponent())
                            .append(Component.text(" as a member!", NamedTextColor.GRAY)));
                    return true;
                }

                if(!Teams.getAllPermissions().contains(args[2])) {
                    player.sendMessage(Component.text("Invalid permission: ", NamedTextColor.RED)
                            .append(Component.text(args[2], NamedTextColor.WHITE)));
                    return true;
                }

                boolean enabled = team.getMemberPermission(args[2]);
                team.setMemberPermissions(args[2], !enabled);

                player.sendMessage(Component.text("Set ", NamedTextColor.GRAY)
                        .append(Component.text(args[2], NamedTextColor.WHITE))
                        .append(Component.text(" to ", NamedTextColor.GRAY))
                        .append(enabled ? Component.text("on", NamedTextColor.GREEN) : Component.text("off", NamedTextColor.RED)));

                return true;
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
        if (args.length == 1) return Arrays.stream(new String[] { "deposit", "withdraw", "create", "join", "info", "edit", "invite", "ignore", "leave", "delete", "ban", "pardon", "kick", "buyChunk", "sellChunk" }).toList();
        switch(args[0]) {
            case "edit" -> {
                if(args.length > 2 && args[1].equals("permission")) return Teams.getAllPermissions();
                if (args.length > 2 && editOptionsBool.contains(args[1])) return Arrays.stream(new String[] { "on", "off" }).toList();
                return Arrays.stream(new String[] { "freeToJoin", "pvp", "explosions", "color", "description", "name", "permission" }).toList();
            }

            case "ban", "kick", "invite" ->{
                return QuackClaim.getOnlinePlayers();
            }

            case "pardon" -> {
                return Teams.getTeamByPlayer(((Player) sender).getUniqueId()).getBannedNames();
            }

            case "join" -> {
                return Teams.getAllTeamNames();
            }

        }
        return Collections.emptyList();
    }
}
