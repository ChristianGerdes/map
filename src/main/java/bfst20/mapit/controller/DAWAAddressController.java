package bfst20.mapit.controller;

import bfst20.mapit.Model;
import bfst20.mapit.MapCanvas;
import bfst20.mapit.model.Type;
import javafx.scene.control.TextField;
import bfst20.mapit.model.DAWAAddress;
import bfst20.mapit.model.AutoComplete;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.io.IconsLoader;
import bfst20.mapit.model.ui.DAWAAddressModel;
import org.controlsfx.control.textfield.TextFields;
import bfst20.mapit.model.drawables.points.NavPoint;
import bfst20.mapit.model.drawables.debug.IntersectionNode;
import org.controlsfx.control.textfield.AutoCompletionBinding;

public class DAWAAddressController {
	private DAWAAddressModel dawaAddressModel;
	private Model model;
	private Search searchController;
	private MapCanvas mapCanvas;

	public DAWAAddressController(TextField searchInput, TextField navInputFrom, TextField navInputTo, Model model, Search searchController, MapCanvas mapCanvas) {
		this.searchController = searchController;
		this.model = model;
		this.mapCanvas = mapCanvas;
		this.dawaAddressModel = model.getDAWAAddressModel();

		var searchInputAutoComplete = TextFields.bindAutoCompletion(searchInput, dawaAddressModel.getAddressesSuggestion());
		var navInputFromAutoComplete = TextFields.bindAutoCompletion(navInputFrom, dawaAddressModel.getAddressesSuggestion());
		var navInputToAutoComplete = TextFields.bindAutoCompletion(navInputTo, dawaAddressModel.getAddressesSuggestion());

		searchInputAutoComplete.setOnAutoCompleted(this::showCoords);
		navInputToAutoComplete.setOnAutoCompleted(this::setAutoCompleteTo);
		navInputFromAutoComplete.setOnAutoCompleted(this::setAutoCompleteFrom);
	}

	private void showCoords(AutoCompletionBinding.AutoCompletionEvent<AutoComplete> event) {
		var address = event.getCompletion();

		double lon = address.getLon();
		double lat = address.getLat();

		// If its a poi we don't need an icon
		if (address instanceof DAWAAddress) {
			searchController.setSearchPoint(
				new NavPoint(
					(float) lon,
					(float) lat,
					IconsLoader.getInstance().getIcon("pin", "UI", "svg")
				)
			);
		}

		mapCanvas.panToPoint(new Point(lon, lat), 300000);
	}

	private void setAutoCompleteTo(AutoCompletionBinding.AutoCompletionEvent<AutoComplete> event) {
		var address = event.getCompletion();
		double lon = address.getLon();
		double lat = address.getLat();

		// If its a poi we don't need an icon
		var intersection = model.nearest(new Point(lon, lat), Type.INTERSECTIONNODE);

		if (address instanceof DAWAAddress) {
			searchController.setEndPoint(
				new NavPoint(
					(float) lon,
					(float) lat,
					IconsLoader.getInstance().getIcon("END_POINT", "UI", "svg")
				)
			);
		}

		searchController.setTo(((IntersectionNode) intersection).id);
	}

	private void setAutoCompleteFrom(AutoCompletionBinding.AutoCompletionEvent<AutoComplete> event) {
		var address = event.getCompletion();
		double lon = address.getLon();
		double lat = address.getLat();

		var intersection = model.nearest(new Point(lon, lat), Type.INTERSECTIONNODE);

		// If its a poi we don't need an icon
		if (address instanceof DAWAAddress) {
			searchController.setStartPoint(
				new NavPoint(
					(float) lon,
					(float) lat,
					IconsLoader.getInstance().getIcon("START_POINT", "UI", "svg")
				)
			);
		}

		searchController.setFrom(((IntersectionNode) intersection).id);
	}
}