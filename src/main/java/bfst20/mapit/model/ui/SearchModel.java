package bfst20.mapit.model.ui;

import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.drawables.points.NavPoint;

public class SearchModel {
	private VehicleType vehicleType = VehicleType.CAR;
	private RouteType routeType = RouteType.FASTEST;
	private long to, from;
	public boolean settingStartPoint = false;
	public boolean settingEndPoint = false;
	public NavPoint startPoint;
	public NavPoint endPoint;
	private NavPoint searchPoint;

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;

		this.setRouteType(
			vehicleType == VehicleType.CAR ? RouteType.FASTEST : RouteType.SHORTEST
		);
	}

	public RouteType getRouteType() {
		return routeType;
	}

	public void setRouteType(RouteType routeType) {
		this.routeType = routeType;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}

	public long getFrom() {
		return this.from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public void setStartPoint(NavPoint navPoint) {
		this.startPoint = navPoint;
	}

	public void setEndPoint(NavPoint navPoint) {
		this.endPoint = navPoint;
	}

	public void setSearchPoint(NavPoint searchPoint) {
		this.searchPoint = searchPoint;
	}

	public NavPoint getSearchPoint() {
		return this.searchPoint;
	}
}