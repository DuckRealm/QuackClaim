package eu.duckrealm.quackclaim.util;

import java.util.Objects;

public class Region {
    private int x;
    private int y;

    public Region(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Region{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Region region = (Region) object;
        return x == region.x && y == region.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
