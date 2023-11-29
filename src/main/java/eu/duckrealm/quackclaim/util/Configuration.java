package eu.duckrealm.quackclaim.util;

import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private final File file;
    private final Plugin plugin;
    private YamlConfiguration config;
    private final Boolean copyDefaults;

    public Configuration(@NotNull Plugin plugin, String fileName, Boolean copyDefaults)
    {
        this.plugin = plugin;
        this.copyDefaults = copyDefaults;
        this.file = new File(plugin.getDataFolder(), fileName);
    }

    public Configuration(Plugin plugin, String fileName)
    {
        this(plugin, fileName, false);
    }

    private void createIfNotExists() throws IOException
    {
        if (!file.exists() || file.isDirectory())
        {
            try {
                plugin.saveResource(file.getName(), false);
            } catch (IllegalArgumentException exception) {
                Validate.isTrue(file.createNewFile(), String.format("Unable to create '%s.yml' file.", file.getName()));
            }
        }
    }

    public void load() throws IOException
    {
        createIfNotExists();
        config = YamlConfiguration.loadConfiguration(file);
        getConfig().options().copyDefaults(copyDefaults);
    }

    public void save() throws IOException
    {
        createIfNotExists();
        getConfig().save(file);
    }

    public YamlConfiguration getConfig()
    {
        return config;
    }
}
