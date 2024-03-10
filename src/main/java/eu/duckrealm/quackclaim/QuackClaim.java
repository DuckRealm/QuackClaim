package eu.duckrealm.quackclaim;

import eu.duckrealm.quackclaim.commands.*;
import eu.duckrealm.quackclaim.listener.*;
import eu.duckrealm.quackclaim.util.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public final class QuackClaim extends JavaPlugin {
    public static final Team SERVERTEAM = new Team(new UUID(0,0), new UUID(0, 0));
    private final String version = getPluginMeta().getVersion();
    public static QuackClaim instance;
    public static Economy economy;
    public static boolean economyEnabled = false;
    public static HashMap<Long, UUID> claims = new HashMap<>();
    public static List<UUID> ignoringClaims = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("******************************************");
        getLogger().info("");

        getLogger().info("Loading QuackClaim v" + version);
        getLogger().info("");

        if(setupEconomy()) {
            getLogger().info(String.format("Economy provider found. Using: %s", economy.getName()));
        } else {
            getLogger().info("Economy not found. Unable to buy claims");
        }


        getLogger().info("Insuring correct directory structure..");
        File plugindir = getDataFolder();
        File datadir = new File(plugindir.getAbsolutePath() + File.separator + "data");
        File tilesdir = new File(plugindir.getAbsolutePath() + File.separator + "tiles");
        File configdir = new File(plugindir.getAbsolutePath() + File.separator + "config");
        if(!plugindir.exists()) plugindir.mkdirs();
        if(!datadir.exists()) datadir.mkdir();
        if(!tilesdir.exists()) tilesdir.mkdir();
        if(!configdir.exists()) configdir.mkdir();

        getLogger().info("Loading resources...");
        QuackConfig.initialize();

        getLogger().info("Registering events...");
        Bukkit.getPluginManager().registerEvents(new BlockChangeEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpenEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new AttackEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new PistonMoveEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new MovementEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProjectileListener(), this);

        getLogger().info("Registering commands...");
        Objects.requireNonNull(Bukkit.getPluginCommand("claim")).setExecutor(new ClaimCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("qteam")).setExecutor(new TeamCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("unclaim")).setExecutor(new UnClaimCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("qadmin")).setExecutor(new AdminCommand());

        getLogger().info("Loading teams...");
        Configuration teamData = new Configuration(QuackClaim.instance, "./data/teams.yml");
        Configuration claimData = new Configuration(QuackClaim.instance, "./data/claims.yml");

        try {
            teamData.load();
        } catch (IOException e) {
            getLogger().warning("Critical Error in QuackClaim! Claims will break! Server will be shutdown for safety.");
            Bukkit.getServer().shutdown();
            throw new RuntimeException(e);
        }
        try {
            claimData.load();
        } catch (IOException e) {
            getLogger().warning("Critical Error in QuackClaim! Claims will break! Server will be shutdown for safety.");
            Bukkit.getServer().shutdown();
            throw new RuntimeException(e);
        }

        List<?> rawTeams = teamData.getConfig().getList("teams");

        if (rawTeams != null && !rawTeams.isEmpty()) {
            for (Object teamObject : rawTeams) {
                if (teamObject instanceof Map<?, ?> teamMap) {
                    Team team = Team.fromMap(teamMap);
                    Teams.putTeam(team);
                }
            }
        }

        YamlConfiguration claimConfig = claimData.getConfig();
        for (String key : claimConfig.getKeys(true)) {
            String value = claimConfig.getString(key);
            UUID teamID = UUID.fromString(value);
            if(teamID == null) continue;
            Team team = Teams.getTeam(teamID);
            if(team == null) continue;
            team.addClaimedChunk();
            claims.put(Long.parseLong(key), teamID);
        }

        getLogger().info("Indexing tiles...");
        getLogger().info("");
        getLogger().info("******************************************");
    }

    @Override
    public void onDisable() {
        Configuration teamData = new Configuration(QuackClaim.instance, "./data/teams.yml");
        Configuration claimData = new Configuration(QuackClaim.instance, "./data/claims.yml");

        try {
            teamData.load();
        } catch (IOException e) {
            getLogger().warning("Critical Error in QuackClaim! Claims will break! Server will be shutdown for safety.");
            Bukkit.getServer().shutdown();
            throw new RuntimeException(e);
        }
        try {
            claimData.load();
        } catch (IOException e) {
            getLogger().warning("Critical Error in QuackClaim! Claims will break! Server will be shutdown for safety.");
            Bukkit.getServer().shutdown();
            throw new RuntimeException(e);
        }

        List<Map<String, Object>> serializedTeams = new ArrayList<>();

        for (Team team : Teams.getValues()) {
            getLogger().info(String.format("Serializing team %s", team.getTeamID()));
            serializedTeams.add(team.toMap());
        }

        teamData.getConfig().set("teams", serializedTeams);

        claims.forEach((Long chunkKey, UUID teamID) -> {
            claimData.getConfig().set(chunkKey.toString(), teamID.toString());
        });

        try {
            claimData.save();
        } catch (IOException e) {
            getLogger().warning("Critical Error in QuackClaim! Claims will break! Server will be shutdown for safety.");
            Bukkit.getServer().shutdown();
            throw new RuntimeException(e);
        }

        try {
            teamData.save();
        } catch (IOException e) {
            getLogger().warning("Critical Error in QuackClaim! Claims will break! Server will be shutdown for safety.");
            Bukkit.getServer().shutdown();
            throw new RuntimeException(e);
        }
    }

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("Vault not found.");
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
            getLogger().info("No economy Provider!");
			return false;
		}
        getLogger().info("Economy provider " + rsp.getProvider().getName());
		economy = rsp.getProvider();
		economyEnabled = economy != null;
        return economyEnabled;
	}


	public static List<String> getOnlinePlayers()
    {
        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }
    
}
