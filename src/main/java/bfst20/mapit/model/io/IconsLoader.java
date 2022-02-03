package bfst20.mapit.model.io;

import java.util.HashSet;

import bfst20.mapit.datastructures.LinearProbingHashST;
import bfst20.mapit.model.directions.Direction;
import javafx.scene.image.Image;

/**
 * Icons
 */
public class IconsLoader {

  private static IconsLoader instance;
  LinearProbingHashST<String, Image> iconList;
  public HashSet<String> notFound = new HashSet<>();

  private IconsLoader() {
    iconList = new LinearProbingHashST<>();
  }

  public static IconsLoader getInstance() {
    if (instance == null) {
      instance = new IconsLoader();
    }
    return instance;
  }

  public Image getIcon(String iconName, String iconType, String imgType) {
    if (iconList.contains(iconName)) {
      return iconList.get(iconName);
    } else {
      Image icon = loadIcon(iconName, iconType, imgType);
      if (iconName != null) {
        iconList.put(iconName, icon);
        return icon;
      }
      return null;
    }
  }

  private Image loadIcon(String iconName, String iconType, String imgType) {
    try {
      return new Image(
          getClass().getClassLoader().getResourceAsStream("icons/" + iconType + "/" + iconName + "." + imgType));
    } catch (Exception e) {
      notFound.add(iconName);
      System.out.println("Missing icon: " + iconName);
      e.printStackTrace();
      return null;
    }
  }

  public Image getPOIicon(String isUI) {
    String imagePath = isUI != null && isUI.equals("UI") ? "icons/UI/POI_UI.svg" : "icons/UI/POI.svg";
    try {
      return new Image(getClass().getClassLoader().getResourceAsStream(imagePath), 200, 200, true, true);
    } catch (Exception e) {
      notFound.add("POI - Logo not found");
      return null;
    }
  }

  public Image getCursorIcon(String iconName, String iconType){
    try {
      return new Image(
          getClass().getClassLoader().getResourceAsStream("icons/" + iconType + "/" + iconName + ".svg"), 35,35, true,
          false);
    } catch (Exception e) {
      notFound.add(iconName);
      System.out.println("Missing icon: " + iconName);
      e.printStackTrace();
      return null;
    }
  }

  public Image getDirectionsIcon(Direction direction, boolean LIGHTMODE) {
    try {
      if (LIGHTMODE) {
        return new Image(
            getClass().getClassLoader().getResourceAsStream("icons/UI/direction/" + direction + "_LIGHT" + ".png"), 34,
            34, true, true);
      } else {
        return new Image(
            getClass().getClassLoader().getResourceAsStream("icons/UI/direction/" + direction + "_DARK" + ".png"), 34,
            34, true, true);
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Direction: " + direction + ", mode: " + (LIGHTMODE ? "LIGHT" : "DARK"));
      return null;
    }
  }
}