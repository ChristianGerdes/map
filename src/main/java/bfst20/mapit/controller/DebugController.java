package bfst20.mapit.controller;

import javafx.scene.Scene;
import bfst20.mapit.Model;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import bfst20.mapit.model.TreeType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.CornerRadii;
import bfst20.mapit.model.ui.DebugModel;
import javafx.scene.layout.BackgroundFill;

public class DebugController {
  	private DebugModel debugModel;
  	private Pane debugBox;

  	public DebugController(Pane debugBox, Model model, Scene scene) {
		this.debugBox = debugBox;
		this.debugModel = model.getDebugModel();
		this.debugBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

		debugModel.setDebugInfo("Zoom factor", "1");
		debugModel.setDebugInfo("Pixel width", "1");
		debugModel.setDebugInfo("Objects", "0");
		debugModel.setDebugInfo("Total trees", "" + TreeType.values().length);
		debugModel.setDebugInfo("Searched trees", "0");

		this.initializeKeyboardShortcuts(scene);
  	}

	private void initializeKeyboardShortcuts(Scene scene) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
			if (keyEvent.getCode() == KeyCode.F1) {
				debugModel.toggleDebug();
			}

			if (keyEvent.getCode() == KeyCode.F2) {
				debugModel.toggleLimitedRange();
			}

			if (keyEvent.getCode() == KeyCode.F3) {
				debugModel.toggleRouteDebug();
			}
		});
	}

	public void setDebugInfo(String key, String val) {
		debugModel.setDebugInfo(key, val);
	}

	public void toggleDebug() {
		debugModel.toggleDebug();
	}
}