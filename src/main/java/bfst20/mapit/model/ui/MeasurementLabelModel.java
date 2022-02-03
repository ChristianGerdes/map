package bfst20.mapit.model.ui;

import bfst20.mapit.MapCanvas;
import java.text.DecimalFormat;
import javafx.scene.control.Label;
import bfst20.mapit.util.utilFunctions;
import bfst20.mapit.datastructures.Point;

public class MeasurementLabelModel {

  public MeasurementLabelModel() {
  }

  public void measureDistance(MapCanvas mapCanvas, Label measurement_label) {
    Point point1 = mapCanvas.toModelCoords(0, 20);
    Point point2 = mapCanvas.toModelCoords(200, 20);
    double distance = utilFunctions.distanceInKmBetweenCoordinates(-point1.getY(), point1.getX() / 0.56f,
        -point2.getY(), point2.getX() / 0.56f);

    String roundedDistance = "";
    String unit = "";

    if (distance > 1.0) {
      DecimalFormat decimalFormat = new DecimalFormat(".#");
      roundedDistance = decimalFormat.format(distance);
      unit = "km";
    } else {
      DecimalFormat decimalFormat = new DecimalFormat("#");
      roundedDistance = decimalFormat.format(distance * 1000);
      unit = "m";
    }

    measurement_label.setText(roundedDistance + " " + unit);
  }
}