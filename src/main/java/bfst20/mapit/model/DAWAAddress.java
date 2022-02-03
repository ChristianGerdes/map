package bfst20.mapit.model;

import java.io.Serializable;

public class DAWAAddress implements Serializable, AutoComplete {
	private static final long serialVersionUID = 1065291935174766791L;

	private String description;

	private float lon;

	private float lat;

  	public DAWAAddress(String description, double x, double y) {
		this.description = description;
		this.lon = (float) x * 0.56f;
		this.lat = (float) -y;
  	}

  	public String toString() {
		return description;
  	}

  	public float getLon() {
		return this.lon;
  	}

  	public float getLat() {
		return this.lat;
  	}
}