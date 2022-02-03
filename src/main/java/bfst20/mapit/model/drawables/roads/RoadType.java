package bfst20.mapit.model.drawables.roads;

import bfst20.mapit.model.Type;
import bfst20.mapit.model.directions.VehicleType;

import static bfst20.mapit.model.Type.*;

import javafx.scene.paint.Color;

/**
 * RoadType
 */
public enum RoadType {
  MOTORWAYROAD, TRUNKROAD, PRIMARYROAD, SECONDARYROAD, TERTIARYROAD, RESIDENTIALROAD, UNCLASSIFIEDROAD, SERVICEROAD,
  PEDESTRIANROAD, ROADROAD, CYCLEWAYROAD, PATH, TRACK, DEFAULTHIGHWAY, FOOTWAY, LIVINGSTREETROAD, MOTORWAY_LINKROAD,
  ROUTE;

  public static Type getType(RoadType road) {
    switch (road) {
      case ROUTE:
        return Type.ROUTE;

      case MOTORWAYROAD:
        return MAJORROAD;

      case PRIMARYROAD:
      case SECONDARYROAD:
      case TRUNKROAD:
        return BIGROADS;

      case MOTORWAY_LINKROAD:
      case TERTIARYROAD:
      case UNCLASSIFIEDROAD:
      case ROADROAD:
      case DEFAULTHIGHWAY:
        return NORMALROADS;

      case SERVICEROAD:
      case LIVINGSTREETROAD:
      case RESIDENTIALROAD:
        return SMALLROADS;
      case PEDESTRIANROAD:
      case CYCLEWAYROAD:
      case PATH:
      case TRACK:
      case FOOTWAY:
        return PATHS;

      default:
        throw new IllegalArgumentException("RoadType: " + road + " does not exist.");
    }
  }

  private static class SIZE {
    public static final double MOTORWAY = 0.000101252434;
    public static final double PRIMARY = 0.000101252434 * 0.66;
    public static final double SECONDARY = 0.00008062621;
    public static final double NORMAL = 0.00005062621;

  }

  public static double getRoadWidth(RoadType road, double pixelWidth) {
    double dynamicSizeThreshold = 2.6688416032513344E-5;// 5.71609499209486946E-5;
    // 1.2088416032513344E-5
    switch (road) {
      case ROUTE:
      case MOTORWAYROAD:
      case MOTORWAY_LINKROAD:
        if (pixelWidth < dynamicSizeThreshold) {
          return SIZE.MOTORWAY;
        } else {
          return 3 * pixelWidth;
        }
      case SECONDARYROAD:
        if (pixelWidth < dynamicSizeThreshold) {
          return SIZE.SECONDARY;
        } else {
          return 1.5 * pixelWidth;
        }

      case PRIMARYROAD:
        if (pixelWidth < dynamicSizeThreshold) {
          return SIZE.PRIMARY;
        } else {
          return 2.5 * pixelWidth;
        }
      case PEDESTRIANROAD:
      case CYCLEWAYROAD:
      case PATH:
      case TRACK:
      case FOOTWAY:
        return pixelWidth;
      default:
        return SIZE.NORMAL;

    }
  }

  public static Color getLightColor(RoadType road) {
    switch (road) {
      case ROUTE:
        return Color.web("#669df7");
      case MOTORWAYROAD:
      case MOTORWAY_LINKROAD:
        return Color.web("#FFEBA1");
      case PRIMARYROAD:
        return Color.web("#FFEBA1");
      case SECONDARYROAD:
        return Color.web("#FFEBA1");
      case TRUNKROAD:
        return Color.web("#FFEBA1");
      case TERTIARYROAD:
      case RESIDENTIALROAD:
      case UNCLASSIFIEDROAD:
      case SERVICEROAD:
      case ROADROAD:
      case LIVINGSTREETROAD:
      case DEFAULTHIGHWAY:
        return Color.WHITE;
      case PEDESTRIANROAD:
        return Color.web("#dddde9");
      case CYCLEWAYROAD:
        return Color.web("#6e6fe1");
      case TRACK:
        return Color.web("#AB9347");
      case PATH:
        return Color.web("#F59C88");
      case FOOTWAY:
        return Color.web("#e9691a");
      default:
        throw new IllegalArgumentException("Road: " + road + " has no light color defined.");
    }
  }

  public static Color getDarkColor(RoadType road) {
    switch (road) {
      case MOTORWAYROAD:
      case MOTORWAY_LINKROAD:
      case PRIMARYROAD:
      case SECONDARYROAD:
      case FOOTWAY:
      case TERTIARYROAD:
      case RESIDENTIALROAD:
      case UNCLASSIFIEDROAD:
      case SERVICEROAD:
      case ROADROAD:
      case LIVINGSTREETROAD:
      case DEFAULTHIGHWAY:
      case PEDESTRIANROAD:
      case CYCLEWAYROAD:
      case TRACK:
      case TRUNKROAD:
      case PATH:
        return Color.web("#CE7500");
      case ROUTE:
        return Color.web("#88f436");
      default:
        throw new IllegalArgumentException("Road: " + road + " has no dark color defined.");

    }
  }

  public static VehicleType getAllowedVehicle(RoadType type) {
    switch (type) {
      case PEDESTRIANROAD:
      case FOOTWAY:
      case CYCLEWAYROAD:
      case PATH:
        return VehicleType.BIKE;
      case MOTORWAYROAD:
      case MOTORWAY_LINKROAD:
        return VehicleType.CAR;
      default:
        return null;
    }
  }

  public static double getSpeedLimit(RoadType type) {
    switch (type) {
      case MOTORWAYROAD:
      case MOTORWAY_LINKROAD:
        return 130;
      case DEFAULTHIGHWAY:
      case TRUNKROAD:
      case PRIMARYROAD:
      case SECONDARYROAD:
      case TERTIARYROAD:
      case ROADROAD:
        return 80;
      case UNCLASSIFIEDROAD:
      case RESIDENTIALROAD:
      case SERVICEROAD:
      case LIVINGSTREETROAD:
        return 50;
      case PEDESTRIANROAD:
      case FOOTWAY:
      case CYCLEWAYROAD:
      case TRACK:
      case PATH:
        return 5;
      default:
        throw new IllegalArgumentException("Type " + type + " is not a road");
    }
  }

  public static boolean shouldDrawName(RoadType roadType) {
    switch (roadType) {
      case PEDESTRIANROAD:
      case FOOTWAY:
      case CYCLEWAYROAD:
      case PATH:
        return false;
      default:
        return true;
    }
  }

  public static boolean isDashed(RoadType road) {
    switch (road) {
      case FOOTWAY:
      case PATH:
      case TRACK:
      case CYCLEWAYROAD:
        return true;
      default:
        return false;
    }
  }
}