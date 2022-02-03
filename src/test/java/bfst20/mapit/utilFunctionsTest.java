package bfst20.mapit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import bfst20.mapit.util.utilFunctions;
import javafx.scene.text.Font;

/**
 * utilFunctionsTest
 */
public class utilFunctionsTest {

  @Test
  void distanceInKmBetweenCoordinatesTest() {
    // Given: https://gps-coordinates.org/distance-between-coordinates.php
    double lat1 = -55.861923;
    double lon1 = 5.908744;
    double lat2 = -55.854965;
    double lon2 = 5.966279;
    var length = utilFunctions.distanceInKmBetweenCoordinates(lat1, lon1, lat2, lon2);
    assertEquals(3.6729968395453794, length);
  }

  @Test
  void createFontTest() {
    String fontFamily = "Verdana", fontWeight = "BOLD";
    int fontSize = 20;
    Font actual = utilFunctions.createFont(fontFamily, fontWeight, fontSize);
    assertEquals(fontFamily, actual.getFamily());
    assertEquals(fontSize, actual.getSize());
  }

  @Test
  void createFontExceptionTest() {
    String fontFamily = "Verdana", fontWeight = "NO FONT WEIGHT";
    int fontSize = 20;
    try {
      Font actual = utilFunctions.createFont(fontFamily, fontWeight, fontSize);
    } catch (Exception e) {
      assertEquals("FontWeight doesn't exist", e.getMessage());
    }
  }

  @Test
  void calculateDimensionsTest() {
    float[] coords = new float[] { 0, 0, 1, 1, 2, 2, 3, 0 };
    var dimensions = utilFunctions.calculateDimensions(coords);
    float[] actual = new float[] { 3, 2 };
    assertArrayEquals(actual, dimensions);
  }

  @Test
  void calculateSmallestPointTest() {
    float[] coords = new float[] { 0, 0, 1, 1, 2, 2, 3, 0 };
    var smallestPoint = utilFunctions.calculateSmallestPoint(coords);
    float[] actual = new float[] { 0, 0 };
    assertArrayEquals(actual, smallestPoint);
  }
}
