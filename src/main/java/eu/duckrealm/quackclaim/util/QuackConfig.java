package eu.duckrealm.quackclaim.util;

import eu.duckrealm.quackclaim.QuackClaim;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class QuackConfig {
	public static double CHUNKPRICE = 5000;
	public static int DEFAULTCHUNKS = 100;
	public static int MAXDESCLENGTH = 200;
	public static int MAXNAMELENGTH = 20;
	public static boolean ECOENABLED = true;

    public static void initialize() {
		Configuration quackconfig = new Configuration(QuackClaim.instance, "./config/quackconfig.yml");
        try {
            quackconfig.load();
        } catch (IOException e) {
			Bukkit.getLogger().severe("Could not load default config");
        }
        YamlConfiguration config = quackconfig.getConfig();

		if(config.getKeys(false).isEmpty()) {
			config.set("eco.enabled", ECOENABLED);
			config.set("eco.chunkprice", CHUNKPRICE);
			config.set("team.DefaultChunkAmount", DEFAULTCHUNKS);
			config.set("team.MaximumDescriptionLength", MAXDESCLENGTH);
			config.set("team.MaximumNameLength", MAXNAMELENGTH);
            try {
                quackconfig.save();
            } catch (IOException e) {
				Bukkit.getLogger().severe("Could not save default config");
            }
        } else {
			ECOENABLED = config.getBoolean("eco.enabled", ECOENABLED);
			CHUNKPRICE = config.getDouble("eco.chunkprice", CHUNKPRICE);
			DEFAULTCHUNKS = config.getInt("team.DefaultChunkAmount", DEFAULTCHUNKS);
			MAXDESCLENGTH = config.getInt("team.MaximumDescriptionLength", MAXDESCLENGTH);
			MAXNAMELENGTH = config.getInt("team.MaximumNameLength", MAXNAMELENGTH);
		}
	}

}
