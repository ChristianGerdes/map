package bfst20.mapit.util;

import java.util.HashMap;
import javafx.scene.text.Font;

public class Fonts {
  	HashMap<String, Font> fontMap;
  	private static Fonts instance;

	public static Fonts getInstance() {
		if (instance == null) {
			instance = new Fonts();
		}

		return instance;
	}

	private Fonts() {
		this.fontMap = new HashMap<String, Font>();
	}

	public Font roadNameFont() {
		final String fontFamily = "Verdana";
		final String fontWeight = "NORMAL";
		final double fontSize = 0.00003;

		if (fontMap.containsKey("roadNameFont")) {
			return fontMap.get("roadNameFont");
		} else {
			var font = utilFunctions.createFont(fontFamily, fontWeight, fontSize);
			fontMap.put("roadNameFont", font);
			return font;
		}
	}
}