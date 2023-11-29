package eu.duckrealm.quackclaim.map;

import eu.duckrealm.quackclaim.QuackClaim;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.codehaus.plexus.util.Base64;
import org.joml.Vector2d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MapTile {

    private final BufferedImage image;
    private final int score;
    private final long inhabitedTime;
    private final Vector2d position;
    private final String world;
    public MapTile(BufferedImage image, int score, long inhabitedTime, Vector2d position, String world) {
        this.image = image;
        this.score = score;
        this.inhabitedTime = inhabitedTime;
        this.position = position;
        this.world = world;
    }

    public Vector2d getPosition() {
        return position;
    }
    public BufferedImage getImage() {
        return image;
    }

    public int getScore() {
        return score;
    }

    public long getInhabitedTime() {
        return inhabitedTime;
    }

    public Color getScoreColor() {
        if(score < 300) return Color.GREEN;
        if(score < 1000) return Color.YELLOW;
        return Color.RED;
    }

    public Color getInhabitedTimeColor() {
        if(score < 3000) return Color.GREEN;
        if(score < 10000) return Color.YELLOW;
        return Color.RED;
    }

    public boolean save(){
        try {
            String path = QuackClaim.instance.getDataFolder().getAbsolutePath() + "/tiles/" + world + "_" + (int) Math.round(getPosition().x) + "x" + (int) Math.round(getPosition().y) + ".png";
            File file = new File(path);
            ImageIO.write(image,"png", file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
