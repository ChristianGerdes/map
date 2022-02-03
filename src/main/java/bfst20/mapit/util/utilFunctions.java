package bfst20.mapit.util;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.FontWeight;

public class utilFunctions {

	public static double distanceInKmBetweenCoordinates(double lat1, double lon1, double lat2, double lon2) {
		double p = Math.PI / 180;
		double a = 0.5 - Math.cos((lat2 - lat1) * p) / 2
				+ Math.cos(lat1 * p) * Math.cos(lat2 * p) * (1 - Math.cos((lon2 - lon1) * p)) / 2;
		return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371km
	}

	public static double getTextWidth(String name, Font font) {
		Text text = new Text(name);
		text.setFont(font);
		return text.getLayoutBounds().getWidth();
	}

	public static double euclideanDistance(double lat1, double lon1, double lat2, double lon2) {
		return Math.sqrt(Math.pow(lon2 - lon1, 2) + Math.pow(lat2 - lat1, 2));
	}

	public static Font createFont(String fontFamily, String fontWeight, double fontSize) {
		FontWeight fw;
		switch (fontWeight) {
			case "BLACK":
				fw = FontWeight.BLACK;
				break;
			case "BOLD":
				fw = FontWeight.BOLD;
				break;
			case "EXTRA_BOLD":
				fw = FontWeight.EXTRA_BOLD;
				break;
			case "EXTRA_LIGHT":
				fw = FontWeight.EXTRA_LIGHT;
				break;
			case "LIGHT":
				fw = FontWeight.LIGHT;
				break;
			case "MEDIUM":
				fw = FontWeight.MEDIUM;
				break;
			case "NORMAL":
				fw = FontWeight.NORMAL;
				break;
			case "SEMI_BOLD":
				fw = FontWeight.SEMI_BOLD;
				break;
			case "THIN":
				fw = FontWeight.THIN;
				break;
			default:
				throw new IllegalArgumentException("FontWeight doesn't exist");
		}
		return Font.font(fontFamily, fw, fontSize);
	}

	public static float[] calculateDimensions(float[] coords) {
		float minX = coords[0];
		float minY = coords[1];
		float maxX = coords[0];
		float maxY = coords[1];

		for (int i = 0; i < coords.length / 2; ++i) {
			if (coords[i * 2] < minX) {
				minX = coords[i * 2];
			}

			if (coords[i * 2] > maxX) {
				maxX = coords[i * 2];
			}

			if (coords[i * 2 + 1] < minY) {
				minY = coords[i * 2 + 1];
			}

			if (coords[i * 2 + 1] > maxY) {
				maxY = coords[i * 2 + 1];
			}
		}

		return new float[] { maxX - minX, maxY - minY };
	}

	public static float[] calculateSmallestPoint(float[] coords) {
		float minX = coords[0];
		float minY = coords[1];

		for (int i = 0; i < coords.length / 2; ++i) {
			if (coords[i * 2] < minX) {
				minX = coords[i * 2];
			}

			if (coords[i * 2 + 1] < minY) {
				minY = coords[i * 2 + 1];
			}
		}
		return new float[] { minX, minY };
	}
}