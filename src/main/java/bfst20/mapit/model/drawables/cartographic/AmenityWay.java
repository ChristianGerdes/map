package bfst20.mapit.model.drawables.cartographic;

import bfst20.mapit.model.Type;
import bfst20.mapit.model.drawables.LinePath;
import bfst20.mapit.model.io.*;
import bfst20.mapit.model.osm.OSMWay;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AmenityWay extends LinePath {

  /**
   *
   */
  private static final long serialVersionUID = -8015562702127239301L;
  private final double fontSize = 0.0001;
  String name;
  float[] centerPoint;

  public AmenityWay(OSMWay currentWay, Type type, String amenityName) {
    super(currentWay, type);
    this.name = amenityName;
    this.centerPoint = calcCenterPoint();
  }

  private float[] calcCenterPoint() {
    float lon = 0f, lat = 0f;
    for (int i = 0; i < this.coords.length - 1; i += 2) {
      lon += this.coords[i];
      lat += this.coords[i + 1];
    }
    return new float[] { lon / this.coords.length * 2, lat / this.coords.length * 2 };
  }

  @Override
  public void draw(GraphicsContext gc) {
    super.draw(gc);
    double pixelwidth = 1 / Math.sqrt(Math.abs(gc.getTransform().determinant()));
    double iconRenderDistance = 4.0 * 10e-7;
    if (pixelwidth < iconRenderDistance) {
      Image icon = IconsLoader.getInstance().getIcon(name, "amenities", "svg");
      if (icon != null) {
        gc.drawImage(icon, centerPoint[0] - fontSize / 2, centerPoint[1] - fontSize / 2, fontSize, fontSize);
      }
    }
  }
}
