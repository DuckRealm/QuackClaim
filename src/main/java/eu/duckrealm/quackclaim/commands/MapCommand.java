package eu.duckrealm.quackclaim.commands;

import eu.duckrealm.quackclaim.util.QuackConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.management.OperatingSystemMXBean;
import java.util.List;

public class MapCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(Component.text("Visit the map at: ", NamedTextColor.GRAY)
                    .append(Component.text(QuackConfig.MAPADDRESS, NamedTextColor.GREEN).clickEvent(ClickEvent.openUrl(QuackConfig.MAPADDRESS))));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
