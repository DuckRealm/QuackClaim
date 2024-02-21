package eu.duckrealm.quackclaim.map;

import eu.duckrealm.quackclaim.QuackClaim;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joml.Vector2d;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TileInfo {
	private final long inhabitedTime;
	private final UUID teamID;
	private final int chunkScore;
	private final Vector2d position;
	private final String dim;
	private final Long ucid;

	public TileInfo(long inhabitedTime, UUID teamID, int chunkScore, Vector2d position, String dim, Long ucid) {
		this.inhabitedTime = inhabitedTime;
		this.chunkScore = chunkScore;
		this.teamID = teamID;
		this.position = position;
		this.dim = dim;
		this.ucid = ucid;
	}

	@Override
	public String toString() {
		return String.format("{\"inhabitedTime\":%s,\"claimedBy\":%s,\"load\":%s}", inhabitedTime, teamID.toString(), chunkScore);
	}

	public String getDim() {
		return this.dim;
	}

	public int getX() {
		return (int) position.x();
	}

	public int getZ() {
		return (int) position.y();
	}

	public boolean save(){
        String path = QuackClaim.instance.getDataFolder().getAbsolutePath() + "/tiles/data/" + dim + "/" + getX() + "x" + getZ() + ".info";
        File file = new File(path);
        try {
            YamlConfiguration config = new YamlConfiguration();


            config.set("inhabitedTime", inhabitedTime);
            config.set("teamID", teamID.toString());
            config.set("score", chunkScore);
            config.set("chunkX", getX());
            config.set("chunkZ", getZ());
            config.set("world", getDim());
            config.set("ucid", ucid);

            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
