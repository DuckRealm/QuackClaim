package eu.duckrealm.quackclaim;

import com.sun.net.httpserver.HttpExchange;
import eu.duckrealm.quackclaim.commands.*;
import eu.duckrealm.quackclaim.listener.*;
import eu.duckrealm.quackclaim.map.ChunkRenderer;
import eu.duckrealm.quackclaim.map.MapTile;
import eu.duckrealm.quackclaim.map.QRendererStats;
import eu.duckrealm.quackclaim.map.TileInfo;
import eu.duckrealm.quackclaim.util.Configuration;
import eu.duckrealm.quackclaim.util.QuackConfig;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import eu.duckrealm.quackclaim.webserver.WebServer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public final class QuackClaim extends JavaPlugin {
    public static final Team SERVERTEAM = new Team(new UUID(0,0), new UUID(0, 0));
    private final String version = getPluginMeta().getVersion();
    public static QuackClaim instance;
    public static Map<Long, TileInfo> tileInfo = new HashMap<>();
    public static Economy economy;
    public static boolean economyEnabled = false;
    public static ChunkRenderer chunkRenderer = new ChunkRenderer();
    public static HashMap<Long, UUID> claims = new HashMap<>();
    public static List<UUID> ignoringClaims = new ArrayList<>();

    byte[] htmlBytes;
    byte[] pixiBytes;
    byte[] logoBytes;

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
        try {
            htmlBytes = getResource("index.min.html").readAllBytes();
            pixiBytes = getResource("pixi.js").readAllBytes();
            logoBytes = getResource("quackclaim-logo.png").readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getLogger().info("Registering events...");
        Bukkit.getPluginManager().registerEvents(new BlockChangeEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpenEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new AttackEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new PistonMoveEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new MovementEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChunkLoadEventListener(), this);

        getLogger().info("Registering commands...");
        Objects.requireNonNull(Bukkit.getPluginCommand("claim")).setExecutor(new ClaimCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("qteam")).setExecutor(new TeamCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("unclaim")).setExecutor(new UnClaimCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("qadmin")).setExecutor(new AdminCommand());
        Objects.requireNonNull(Bukkit.getPluginCommand("map")).setExecutor(new MapCommand());

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

        if(QuackConfig.WEBENABLED) LaunchWebServer();

        getLogger().info("");
        getLogger().info("******************************************");
    }

    private void LaunchWebServer() {
        try {
            WebServer server = new WebServer();

            server.onRequest("/", (HttpExchange exchange) -> {
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, htmlBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(htmlBytes);
                os.close();
            });

            server.onRequest("/pixi.js", (HttpExchange exchange) -> {
                exchange.getResponseHeaders().set("Content-Type", "application/javascript");
                exchange.sendResponseHeaders(200, pixiBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(pixiBytes);
                os.close();
            });

            server.onRequest("/quackclaim-logo.png", (HttpExchange exchange) -> {
                exchange.getResponseHeaders().set("Content-Type", "image/png");
                exchange.sendResponseHeaders(200, logoBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(logoBytes);
                os.close();
            });

            server.onRequest("/version", (HttpExchange exchange) -> {
                String response = String.format("Running QuackClaim v %s",version);
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });

            server.onRequest("/tile", (HttpExchange exchange) -> {
                Map<String, String> query = WebServer.parseQueryString(exchange.getRequestURI().getQuery());
                if(!query.containsKey("x") || !query.containsKey("z") || !query.containsKey("dim")) {
                    String response = "400 Bad Request";
                    exchange.sendResponseHeaders(400, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                String absolutePath = QuackClaim.instance.getDataFolder().getAbsolutePath();
                String path = String.format("%s/tiles/%s_%sx%s.png", absolutePath, query.get("dim"), query.get("x"), query.get("z"));
                File file = new File(path);
                if(!file.exists()) {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }

				exchange.sendResponseHeaders(200, file.length());
                OutputStream os = exchange.getResponseBody();
                Files.copy(file.toPath(), os);
                os.close();

            });

            server.onRequest("/tile/info", (HttpExchange exchange) -> {
                Map<String, String> query = WebServer.parseQueryString(exchange.getRequestURI().getQuery());
                if(!query.containsKey("x") || !query.containsKey("z") || !query.containsKey("dim")) {
                    String response = "400 Bad Request";
                    exchange.sendResponseHeaders(400, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }

                long key = Chunk.getChunkKey(Integer.parseInt(query.get("x")), Integer.parseInt(query.get("z")));
                if(tileInfo.containsKey(key)) {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }

                String response = tileInfo.get(key).toString();
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            });

                server.onRequest("/tile/list", (HttpExchange exchange) -> {
                AtomicReference<String> list = new AtomicReference<>("");
                tileInfo.forEach((Long key, TileInfo info) -> {
                    list.set(list + (String.format("dim=%s&z=%s&x=%s\n", info.getDim(), info.getZ(), info.getX())));
                });
                String response = list.toString();
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
