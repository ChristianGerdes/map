package bfst20.mapit.model;

import javafx.scene.paint.Color;

public enum Type {
	UNKNOWN, COASTLINE, ROAD,
	ISLAND(TreeType.ISLAND, -1, true, "F9F5ED", "f5f4f0", "323432", "323432"),

	// RURAL AREAS
	FORESTAREA(TreeType.FOREST, -1, true, "CDEECE", "CDEECE", "3A3938", "3A3938"),

	// VEGETATION AND SURFACE
	ALLOTMENTSAREA(TreeType.SURFACE, -1, true, "c9e1bf", "c9e1bf", "3A3938", "3A3938"),
	ORCHARDAREA(TreeType.SURFACE, -1, true, "ace0a0", "ace0a0", "3A3938", "3A3938"),
	VINEYARDAREA(TreeType.SURFACE, -1, true, "ace0a0", "ace0a0", "3A3938", "3A3938"),
	WOOD(TreeType.SURFACE, -1, true, "CDEECE", "CDEECE", "454545", "454545"),
	HEATH(TreeType.SURFACE, -1, true, "CDEECE", "CDEECE", "2A2B2A", "2A2B2A"),
	SAND(TreeType.SURFACE, -1, true, "FFF1BA", "FFF1BA", "2A2B2A", "2A2B2A"),
	GOLFCOURSE(TreeType.SURFACE, -1, true, "B6E3B5", "B6E3B5", "454545", "454545"),
	RESIDENTIALAREA(TreeType.SURFACE, -1, true, "E9E9EA", "E9E9EA", "444444", "444444"),

	WATER(TreeType.WATER, -1, true, "B0DAE8", "B0DAE8", "191A1A", "191A1A"),

	// OTHER AREAS
	BASINAREA(TreeType.OTHER, -1, true, "a9d3df", "a9d3df", "2A2B2A", "2A2B2A"),
	CEMETERYAREA(TreeType.OTHER, -1, true, "aacbae", "aacbae", "2A2B2A", "2A2B2A"),
	GARAGEAREA(TreeType.OTHER, -1, true, "deddcb", "deddcb", "2A2B2A", "2A2B2A"),
	RAILWAYAREA(TreeType.OTHER, -1, true, "ebdbe8", "ebdbe8", "2A2B2A", "2A2B2A"),
	REACREATIONGROUNDAREA(TreeType.OTHER, -1, true, "defce2", "defce2", "2A2B2A", "2A2B2A"),
	RELIGIOUSAREA(TreeType.OTHER, -1, true, "cdccc8", "cdccc8", "2A2B2A", "2A2B2A"),
	RESERVOIRAREA(TreeType.OTHER, -1, true, "a9d3df", "a9d3df", "2A2B2A", "2A2B2A"),

	// AIRPORT STUFF
	APRON(TreeType.TERMINALS, -1, true, "DADAE0", "DADAE0", "2A2B2A", "2A2B2A"),
	HANGAR(TreeType.TERMINALS, -1, true, "D9D0C9", "D9D0C9", "2A2B2A", "2A2B2A"),
	HELIPORT(TreeType.TERMINALS, -1, true, "B9A99C", "B9A99C", "2A2B2A", "2A2B2A"),
	RUNWAY(TreeType.TERMINALS, 0.000101252434*4, false, "BBBBCC", "BBBBCC", "2A2B2A", "2A2B2A"),
	TAXIWAY(TreeType.TERMINALS, 3 * 1.6875405741623497E-5, false, "BBBBCC", "BBBBCC", "2A2B2A", "2A2B2A"),
	TERMINAL(TreeType.TERMINALS, -1, false, "B9A99C", "B9A99C", "2A2B2A", "2A2B2A"),
	RAIL(TreeType.TERMINALS, 1.6875405741623497E-5, false, "4f4f4f", "4f4f4f", "5E5CE6", "5E5CE6"),

	// BUILDINGS
	BUILDING(TreeType.BUILDINGS, 0.33 * 10.0 * 10e-7, true, "f0f0f0", "d5d5dd", "4C4C4C", "2B2B2B"),

	// ROADS
	PATHS(TreeType.PATHS),
	SMALLROADS(TreeType.SMALLROADS),
	NORMALROADS(TreeType.NORMALROADS),
	BIGROADS(TreeType.BIGROADS),
	MAJORROAD(TreeType.MAJORROADS),

	// Cartographic elements
	AMENITY(TreeType.CARTOGRAPHICS, -1, false, "03a8f425", "03a8f450", "454545", "454545"),

	// Debug
	RELAXEDROAD(TreeType.RELAXEDROADS, -1, false, "F4DDBA", "F4DDBA", "454545", "454545"), ROUTE(TreeType.ROUTES),
	INTERSECTIONNODE(TreeType.INTERSECTIONNODES, -1, false, "F4DDBA", "F4DDBA", "454545", "454545"),
	ROADNAMES(TreeType.ROADNAMES),

	// CITY NAMES
	HAMLET(TreeType.HAMLETS, -1, false, "F4DDBA", "F4DDBA", "454545", "454545"),
	VILLAGE(TreeType.VILLAGES, -1, false, "F4DDBA", "F4DDBA", "454545", "454545"),
	TOWN(TreeType.TOWNS, -1, false, "F4DDBA", "F4DDBA", "454545", "454545"),
	CITY(TreeType.CITIES, -1, false, "F4DDBA", "F4DDBA", "454545", "454545");

	public TreeType treeType;

	public double lineWidth;

	public boolean fill;

	public Color lightStrokeColor;

	public Color lightFillColor;

	public Color darkStrokeColor;

	public Color darkFillColor;

	private Type() {
	}

	private Type(TreeType treeType) {
		this.treeType = treeType;
		this.fill = false;
	}

	private Type(TreeType treeType, double lineWidth, boolean fill, String lightStrokeColor, String lightFillColor,
			String darkStrokeColor, String darkFillColor) {
		this.treeType = treeType;
		this.lineWidth = lineWidth;
		this.fill = fill;
		this.lightStrokeColor = Color.web("#" + lightStrokeColor);
		this.lightFillColor = Color.web("#" + lightFillColor);
		this.darkStrokeColor = Color.web("#" + darkStrokeColor);
		this.darkFillColor = Color.web("#" + darkFillColor);
	}

	public static boolean isRoad(Type road) {
		switch (road) {
			case MAJORROAD:
			case BIGROADS:
			case NORMALROADS:
			case SMALLROADS:
			case PATHS:
			case ROUTE:
				return true;
			default:
				return false;
		}
	}
}
