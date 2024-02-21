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
	public static int DEFAULTPORT = 8080;
	public static String MAPADDRESS = "https://localhost:8080/";
	public static boolean WEBENABLED = true;
	public static boolean ECOENABLED = true;
	public static boolean WEBPROXYENABLED = false;
	public static String WEBPROXYIP = "0.0.0.0";

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
			config.set("web.port", DEFAULTPORT);
			config.set("web.address", MAPADDRESS);
			config.set("web.enabled", WEBENABLED);
			config.set("web.proxy.enabled", WEBPROXYENABLED);
			config.set("web.proxy.ip", WEBPROXYIP);
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
			DEFAULTPORT = config.getInt("web.port", DEFAULTPORT);
			MAPADDRESS = config.getString("web.address", MAPADDRESS);
			WEBENABLED = config.getBoolean("web.enabled", WEBENABLED);
			WEBPROXYENABLED = config.getBoolean("web.proxy.enabled", WEBPROXYENABLED);
			WEBPROXYIP = config.getString("web.proxy.ip", WEBPROXYIP);
		}
	}

}
