package bfst20.mapit.controller;

import bfst20.mapit.Model;
import javafx.scene.control.TextField;
import bfst20.mapit.model.ui.SearchModel;
import bfst20.mapit.model.io.IconsLoader;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.drawables.points.NavPoint;

public class Search {
	private Model model;
	private TextField navInputFrom;
	private TextField navInputTo;
	private SearchModel searchModel;
	private boolean navPointsSwitched = false;

  	public Search(Model model, TextField navInputFrom, TextField navInputTo) {
		this.model = model;
		this.searchModel = model.getSearchModel();
		this.navInputTo = navInputTo;
		this.navInputFrom = navInputFrom;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.searchModel.setVehicleType(vehicleType);
	}

	public void setRouteType(RouteType routeType) {
		this.searchModel.setRouteType(routeType);
	}

	public void setFrom(long from, Point point) {
		this.searchModel.setFrom(from);

		this.model.navigationStartPoint = point;
	}

	public void setTo(long to, Point point) {
		this.searchModel.setTo(to);

		this.model.navigationEndPoint = point;
	}

	public void swapNavInput() {
		String temp = navInputFrom.getText();
		String temp2 = navInputTo.getText();
		navInputFrom.setText(temp2);
		navInputTo.setText(temp);

		// Swapping from to;
		long tempId = searchModel.getFrom();
		searchModel.setFrom(searchModel.getTo());
		searchModel.setTo(tempId);

		if (searchModel.startPoint != null && searchModel.endPoint != null) {
			var startIcon = IconsLoader.getInstance().getIcon("START_POINT", "UI", "svg");
			var endIcon = IconsLoader.getInstance().getIcon("END_POINT", "UI", "svg");
			if (!navPointsSwitched) {
				searchModel.endPoint.setImage(startIcon);
				searchModel.startPoint.setImage(endIcon);
			} else {
				searchModel.endPoint.setImage(endIcon);
				searchModel.startPoint.setImage(startIcon);
			}
			navPointsSwitched = !navPointsSwitched;
			model.notifyObservers();
		}
	}

	public void resetRoute(DirectionsController directionsController) {
		navInputFrom.setText("");
		navInputTo.setText("");

		// reset route and relaxedRoads
		directionsController.clearDirections();

		// remove icons from canvas
		model.resetRoute();

		searchModel.setFrom(0);
		searchModel.setTo(0);
		searchModel.setStartPoint(null);
		searchModel.setEndPoint(null);
		searchModel.setSearchPoint(null);
		// removes directions

		// Repaints the canvas
		model.notifyObservers();
	}

	public void setTo(long to) {
		searchModel.setTo(to);
	}

	public void setFrom(long from) {
		searchModel.setFrom(from);
	}

	public void setEndPoint(NavPoint navPoint) {
		searchModel.setEndPoint(navPoint);
	}

	public void setStartPoint(NavPoint navPoint) {
		searchModel.setStartPoint(navPoint);
	}

	public void setSearchPoint(NavPoint navPoint) {
		searchModel.setSearchPoint(navPoint);
	}

	public boolean isSettingStartPoint() {
		return searchModel.settingStartPoint;
	}

	public boolean isSettingEndPoint() {
		return searchModel.settingEndPoint;
	}

	public void swapStartPoint() {
		searchModel.settingStartPoint = !searchModel.settingStartPoint;
	}

	public void swapEndPoint() {
		searchModel.settingEndPoint = !searchModel.settingEndPoint;
	}
}