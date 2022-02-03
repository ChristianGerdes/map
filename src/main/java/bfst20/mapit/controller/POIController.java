package bfst20.mapit.controller;

import bfst20.mapit.Model;
import javafx.geometry.Pos;
import bfst20.mapit.MapCanvas;
import bfst20.mapit.model.Type;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import bfst20.mapit.model.ui.POIModel;
import javafx.scene.control.TextField;
import bfst20.mapit.model.io.IconsLoader;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.points.POI;

public class POIController {
	private Model model;
	private MapCanvas mapCanvas;
	private VBox points;
	private POIModel poiModel;
	private Label measurementLabel;

	public POIController(Model model, MapCanvas mapCanvas, VBox points, Label measurementLabel) {
		this.mapCanvas = mapCanvas;
		this.model = model;
		this.poiModel = model.getPOIModel();
		this.points = points;
		this.measurementLabel = measurementLabel;
	}

	public POI createNewPOI(Point mc) {
		var road = (Road) model.nearest(mc, Type.NORMALROADS);

		String name = road.getName().equals("This road has no name") ? "Point of interest" : road.getName();

		POI poi = new POI(mc.getX(), mc.getY(), name);

		// Adds poi to an arraylist in model
		poiModel.addPOI(poi);

		HBox item = new HBox();
		item.setSpacing(12);
		item.setAlignment(Pos.CENTER_LEFT);
		item.getStyleClass().add("poi-item");

		ImageView icon = new ImageView(IconsLoader.getInstance().getIcon("POI_UI", "UI", "svg"));
		icon.setFitHeight(25);
		icon.setFitWidth(25);
		icon.setPreserveRatio(true);

		StackPane iconWrapper = new StackPane();
		iconWrapper.setAlignment(Pos.CENTER);
		iconWrapper.getStyleClass().add("poi-item-icon");
		iconWrapper.getChildren().add(icon);

		TextField textfield = new TextField(poi.getName());
		textfield.getStyleClass().add("input");
		HBox.setHgrow(textfield, Priority.ALWAYS);

		ImageView closeIcon = new ImageView(IconsLoader.getInstance().getIcon("close", "UI", "svg"));
		closeIcon.setFitHeight(20);
		closeIcon.setFitWidth(20);
		closeIcon.setPreserveRatio(true);

		Button deleteButton = new Button();
		deleteButton.setGraphic(closeIcon);
		deleteButton.getStyleClass().addAll("btn", "btn-icon");

		item.getChildren().addAll(iconWrapper, textfield, deleteButton);

		this.points.getChildren().add(item);
		this.poiModel.invertPlacingPOI();

		model.getDAWAAddressModel().addPOItoAutoFillList(poi);

		iconWrapper.setOnMouseClicked(event -> {
			mapCanvas.panToPoint(mc, 40000);

			model.getMeasurementLabelModel().measureDistance(mapCanvas, measurementLabel);

			mapCanvas.requestFocus();
		});

		textfield.setOnAction(event -> {
			poi.setPOI(textfield.getText());

			mapCanvas.requestFocus();

			model.notifyObservers();
		});

		deleteButton.setOnMouseClicked(event -> {
			poiModel.removePOIFromList(poi);
			this.points.getChildren().remove(item);
			model.getDAWAAddressModel().updataDAWAList(poi);
			this.mapCanvas.repaint();
		});

		return poi;
	}
}