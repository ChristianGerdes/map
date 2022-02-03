package bfst20.mapit.controller;

import bfst20.mapit.Model;
import javafx.geometry.Pos;
import bfst20.mapit.MapCanvas;
import java.text.DecimalFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.io.IconsLoader;
import bfst20.mapit.model.directions.DirectionInformation;

public class DirectionsController {
	private Model model;
	private VBox directionsTable;
	private Label directionTotalDistanceLabel;
	private MapCanvas mapcanvas;

	public DirectionsController(Model model, VBox directionsTable, Label directionTotalDistanceLabel, MapCanvas mapcanvas) {
		this.model = model;
		this.directionsTable = directionsTable;
		this.directionTotalDistanceLabel = directionTotalDistanceLabel;
		this.mapcanvas = mapcanvas;
	}

	public void printDirections() {
		this.directionsTable.getChildren().clear();

		if (model.directions == null || model.directions.isEmpty()) {
			return;
		}

		this.directionTotalDistanceLabel.setText("Total distance: " + this.getFormattedTotalRouteDistance());

		for (DirectionInformation direction : this.model.directions) {
			HBox directionElement = generateDirection(direction);

			directionElement.setOnMouseClicked(event -> {
				panToDirection(direction.getCoords());
			});

			directionsTable.getChildren().add(directionElement);
		}
	}

	private String getFormattedTotalRouteDistance() {
		String roundedDistance = "";
		String unit = "";

		if (this.model.getTotalRouteDistance() > 1.0) {
			DecimalFormat decimalFormat = new DecimalFormat(".#");
			roundedDistance = decimalFormat.format(this.model.getTotalRouteDistance());
			unit = "km";
		} else {
			DecimalFormat decimalFormat = new DecimalFormat("#");
			roundedDistance = decimalFormat.format(this.model.getTotalRouteDistance() * 1000);
			unit = "m";
		}

		return roundedDistance + " " + unit;
	}

	private void panToDirection(float[] coords) {
		Point point = new Point(coords[0], coords[1]);
		mapcanvas.requestFocus();
		mapcanvas.panToPoint(point, 250000);
	}

	public void clearDirections() {
		directionsTable.getChildren().clear();
	}

	public HBox generateDirection(DirectionInformation direction) {
		ImageView icon = new ImageView(IconsLoader.getInstance().getDirectionsIcon(direction.getDirection(), model.isLightColorMode()));
		icon.setPreserveRatio(true);
		icon.setFitWidth(25);
		icon.setFitHeight(25);

		Label distanceText = new Label(direction.getFormattedDistance());
		distanceText.getStyleClass().add("direction-item-distance");

		Label directionText = new Label(direction.getFormattedDirection());
		directionText.getStyleClass().add("direction-item-direction");

		VBox directionInformation = new VBox(2);
		directionInformation.getChildren().addAll(distanceText, directionText);

		HBox wrapper = new HBox(12);
		wrapper.setAlignment(Pos.CENTER_LEFT);
		wrapper.getStyleClass().add("directions-item");
		wrapper.getChildren().addAll(icon, directionInformation);

		return wrapper;
	}
}