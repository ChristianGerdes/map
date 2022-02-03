package bfst20.mapit.model.osm;

import java.io.Serializable;
import java.util.function.LongSupplier;

public class OSMNode implements LongSupplier, Comparable<OSMNode>, Serializable {
    private static final long serialVersionUID = -7126876477437256091L;
    public long id;
    public float lat, lon;

    public OSMNode(long id, float lon, float lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
    }

    public OSMNode(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public long getAsLong() {
        return id;
    }

    public int compareTo(OSMNode that) {
        return (int) (this.id - that.id);
    }

    public String toString() {
        return "" + this.id;
    }

    public float getLat() {
        return this.lat;
    }

    public float getLon() {
        return this.lon;
    }
}