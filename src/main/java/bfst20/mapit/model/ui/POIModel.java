package bfst20.mapit.model.ui;

import java.util.ArrayList;

import bfst20.mapit.model.drawables.points.NavPoint;
import bfst20.mapit.model.drawables.points.POI;

public class POIModel {
	private ArrayList<POI> pois = new ArrayList<POI>();
	private boolean placingPOI = false;
	private boolean isVisible = true;

	private NavPoint poiPoint;

	public ArrayList<POI> getPOI() {
		return this.pois;
	}

	public void addPOI(POI poi) {
		this.pois.add(poi);
	}

	public void invertPlacingPOI() {
		placingPOI = ! placingPOI;
	}

	public boolean isPlacingPOI() {
		return placingPOI;
	}

	public void removePOIFromList(POI poi) {
		pois.remove(poi);
	}

	public void setVisibility(boolean checkBoxSelected) {
		isVisible = checkBoxSelected;
	}

	public boolean isVisible() {
		return this.isVisible;
	}
	public void setPoiPoint(NavPoint point){
		this.poiPoint = point;
	}
	public NavPoint getPoiPoint(){
		return this.poiPoint;
	}
}