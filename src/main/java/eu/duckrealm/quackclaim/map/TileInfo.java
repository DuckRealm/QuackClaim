package eu.duckrealm.quackclaim.map;

import org.joml.Vector2d;

import java.util.UUID;

public class TileInfo {
	private final long inhabitedTime;
	private final UUID teamID;
	private final int chunkScore;
	private Vector2d position;
	private String dim;

	public TileInfo(long inhabitedTime, UUID teamID, int chunkScore, Vector2d position, String dim) {
		this.inhabitedTime = inhabitedTime;
		this.chunkScore = chunkScore;
		this.teamID = teamID;
		this.position = position;
		this.dim = dim;
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
}
