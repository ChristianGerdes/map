package bfst20.mapit.model;

public enum CursorType {
    NORMAL, POI("POI"), STARTPOINT("START_POINT"), ENDPOINT("END_POINT");

    public String icon;

    private CursorType() {}

    private CursorType(String icon) {
        this.icon = icon;
    }
}