package bfst20.mapit.controller;

import javafx.scene.Scene;
import bfst20.mapit.Model;
import javafx.event.ActionEvent;

public class StylesheetController {
	private Scene scene;
	private final String lightModeCSS = getClass().getResource("/styles/lightMode.css").toExternalForm();
	private final String darkModeCSS = getClass().getResource("/styles/darkMode.css").toExternalForm();
	private final String mainCSSUrl = getClass().getResource("/styles/styles.css").toExternalForm();
	private Model model;

	public StylesheetController(Model model, Scene scene) {
		this.model = model;
		this.scene = scene;
		this.scene.getStylesheets().add(mainCSSUrl);
		this.scene.getStylesheets().add(lightModeCSS);
	}

	public void changeColorMode(ActionEvent e) {
		if (model.isLightColorMode()) {
			scene.getStylesheets().remove(lightModeCSS);
			scene.getStylesheets().add(darkModeCSS);
			model.setLightColorMode(false);
		} else {
			scene.getStylesheets().remove(darkModeCSS);
			scene.getStylesheets().add(lightModeCSS);
			model.setLightColorMode(true);
		}

		model.setLightColorMode(model.isLightColorMode());
		model.notifyObservers();
	}
}